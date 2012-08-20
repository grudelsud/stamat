package it.unifi.micc.homer.controller.namedentity;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.DocumentContent;
import gate.Factory;
import gate.Gate;
import gate.corpora.DocumentContentImpl;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import it.unifi.micc.homer.util.StringOperations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import stamat.model.KeywordType;
import stamat.model.NamedEntity;

public class AnnieNEDDetector implements NamedEntityDetector {

	Logger logger = Logger.getLogger(AnnieNEDDetector.class.getName());

	/** Contains Annie for NED */
	private static AnnieNEDDetector instance = null;
	private static AnnieNEDAnalyser annie = null;

	/** Contains Entity detected */
	private ArrayList<NamedEntity> entities;
	int minNEDLength = 2; // entities must be > than minNEDLength

	private AnnieNEDDetector() {
		init();
	}

	private AnnieNEDDetector(String gateHomePath) {
		init(gateHomePath);
	}

	public static AnnieNEDDetector getInstance() {
		if (instance == null) {
			instance = new AnnieNEDDetector();
		}
		return instance;
	}

	public static AnnieNEDDetector getInstance(String gateHomePath) {
		if (instance == null) {
			instance = new AnnieNEDDetector(gateHomePath);
		}
		return instance;
	}

	private void init(String gateHomePath)
	{
		System.setProperty("gate.plugins.home", gateHomePath);
		this.init();
	}

	/** Init Gate e call the method for to initialize Annie */
	private void init()
	{
		String gateHomePath = System.getProperty("gate.plugins.home");
		try {
			// Initialize the GATE library
			Gate.init();
			// Load ANNIE plugin
			File gateHome = new File(gateHomePath);
			File pluginsHome = new File(gateHome, "plugins");
			Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "ANNIE").toURI().toURL());
			// Initialize ANNIE (this may take several hundreds of years...)
			if (annie == null) {
				AnnieNEDDetector.annie = new AnnieNEDAnalyser();
				annie.initAnnie();
			}
		} catch (GateException ge) {
			logger.log(Level.WARNING, "exception caught: " + ge.getMessage());
		} catch (IOException io) {
			logger.log(Level.WARNING, "exception caught: " + io.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.namedentity.NamedEntityDetector#extractEntity(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public ArrayList<NamedEntity> extractEntity(String text, ArrayList<KeywordType> type) {

		entities = new ArrayList<NamedEntity>();
		ArrayList<NamedEntity> auxEntities = new ArrayList<NamedEntity>();
		Document doc;
		DocumentContent content;
		Corpus auxCorpus;

		try {
			doc = (Document) Factory.createResource("gate.corpora.DocumentImpl");
			auxCorpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
			// content = (DocumentContent)
			// Factory.createResource("gate.corpora.DocumentContentImpl");
			content = new DocumentContentImpl(text);
			doc.setContent(content);
			auxCorpus.add(doc);

			// add a Corpus to Annie
			annie.setCorpus(auxCorpus);
			// execute Annie
			annie.runAnnie();

			// get all annotations of Document
			AnnotationSet defaultAnnotation = doc.getAnnotations();

			// set a list of annotations' names that you want
			Set<String> annotTypesRequired = new HashSet<String>();
			if (type.contains(KeywordType.ALLENTS)) {
				for (int i = 0; i < KeywordType.values().length; i++) {
					if (KeywordType.values()[i] != KeywordType.ALLENTS)
						annotTypesRequired.add(KeywordType.values()[i].toString());
				}
			} else {
				for (int i = 0; i < type.size(); i++) {
					annotTypesRequired.add(type.get(i).toString());
				}
			}

			// select from list of all annotations only those that you have selected
			Set<Annotation> annotRequired = new HashSet<Annotation>(defaultAnnotation.get(annotTypesRequired));

			Iterator<Annotation> it = annotRequired.iterator();
			while (it.hasNext()) {
				Annotation auxAnnotation = it.next();
				Iterator<NamedEntity> iter;
				int index = -1;
				String entity = null;
				// entities must have more than minNEDLength characters
				if (auxAnnotation.getEndNode().getOffset().intValue() - auxAnnotation.getStartNode().getOffset().intValue() > minNEDLength) {
					boolean occurrence = false;
					boolean substitute = false;
					entity = "";
					entity = StringOperations.tokenizeAndCorrect(doc
							.getContent()
							.getContent(new Long(auxAnnotation.getStartNode().getOffset().intValue()),
									new Long(auxAnnotation.getEndNode().getOffset().intValue())).toString());
					iter = auxEntities.iterator();
					while (iter.hasNext() && occurrence == false && substitute == false) {
						NamedEntity ed = iter.next();
						if (ed.getKeyword().equals(entity) == true || ed.getKeyword().length() > entity.length()
								&& ed.getKeyword().indexOf(entity) >= 0) {
							occurrence = true;
						} else if (ed.getKeyword().length() < entity.length() && entity.indexOf(ed.getKeyword()) >= 0) {
							substitute = true;
							index = auxEntities.indexOf(ed);
						}
					}
					if (occurrence == false && substitute == false) {
						NamedEntity currEntity = new NamedEntity();
						currEntity.setStart((auxAnnotation.getStartNode().getOffset().intValue()));
						currEntity.setEnd((auxAnnotation.getEndNode().getOffset().intValue()));
						currEntity.setKeyword(entity.trim());
						currEntity.setType(KeywordType.fromString(auxAnnotation.getType()));
						auxEntities.add(currEntity);
					} else if (substitute == true) {
						NamedEntity currEntity = new NamedEntity();
						currEntity.setStart((auxAnnotation.getStartNode().getOffset().intValue()));
						currEntity.setEnd((auxAnnotation.getEndNode().getOffset().intValue()));
						currEntity.setKeyword(entity.trim());
						currEntity.setType(KeywordType.fromString(auxAnnotation.getType()));
						auxEntities.set(index, currEntity);
					}
				}
			}
			Collections.sort(auxEntities);
			this.entities = auxEntities;
			auxCorpus.unloadDocument(doc);
		} catch (ResourceInstantiationException e) {
			logger.log(Level.WARNING, "gate threw a ResourceInstantiationException: " + e.getMessage());
		} catch (GateException e) {
			logger.log(Level.WARNING, "gate threw a GateException: " + e.getMessage());
		}
		return this.getEntities();
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.namedentity.NamedEntityDetector#getEntities()
	 */
	@Override
	public ArrayList<NamedEntity> getEntities() {
		return entities;
	}

	/**
	 * @return the minNEDLength
	 */
	public int getMinNEDLength() {
		return minNEDLength;
	}

	/**
	 * @param minNEDLength
	 *            the minNEDLength to set
	 */
	public void setMinNEDLength(int minNEDLength) {
		this.minNEDLength = minNEDLength;
	}

}