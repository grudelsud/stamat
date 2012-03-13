package it.unifi.micc.homer.model.namedentity;

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
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class AnnieNEDDetector {

	/** Contains Annie for NED */
	private static AnnieNEDDetector instance = null;
	private static AnnieNEDAnalyser annie = null;
	/** Contains Entity detected */
	private ArrayList<NamedEntity> entities;
	int minNEDLength = 2; // entities must be > than minNEDLength

	private AnnieNEDDetector() { // singleton
		init();
	}

	public static AnnieNEDDetector getInstance() {
		if (instance == null) {
			instance = new AnnieNEDDetector();
		}
		return instance;
	}

	/** Init Gate e call the method for to initialize Annie */
	private void init() {
		try {
			// Initialize the GATE library
			Gate.init();
			// Load ANNIE plugin
			File gateHome = Gate.getGateHome();
			if (gateHome == null)
				gateHome = new File("./WEB-INF/");
			File pluginsHome = new File(gateHome, "plugins");
			Gate.getCreoleRegister().registerDirectories(
					new File(pluginsHome, "ANNIE").toURI().toURL());
			// Initialize ANNIE (this may take several minutes)
			if (annie == null) {
				AnnieNEDDetector.annie = new AnnieNEDAnalyser();
				annie.initAnnie();
			}
		} catch (GateException ge) {
		} catch (IOException io) {
		}
	}

	/** Find the Entity specified in type from the text */
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
					entity = tokenizeAndCorrect(doc
							.getContent()
							.getContent(new Long(auxAnnotation.getStartNode().getOffset().intValue()),
									new Long(auxAnnotation.getEndNode().getOffset().intValue())).toString());
					iter = auxEntities.iterator();
					while (iter.hasNext() && occurrence == false && substitute == false) {
						NamedEntity ed = iter.next();
						if (ed.getValue().equals(entity) == true || ed.getValue().length() > entity.length()
								&& ed.getValue().indexOf(entity) >= 0) {
							occurrence = true;
						} else if (ed.getValue().length() < entity.length() && entity.indexOf(ed.getValue()) >= 0) {
							substitute = true;
							index = auxEntities.indexOf(ed);
						}
					}
					if (occurrence == false && substitute == false) {
						NamedEntity currEntity = new NamedEntity();
						currEntity.setStart((auxAnnotation.getStartNode().getOffset().intValue()));
						currEntity.setEnd((auxAnnotation.getEndNode().getOffset().intValue()));
						currEntity.setValue(entity.trim());
						currEntity.setType(KeywordType.fromString(auxAnnotation.getType()));
						auxEntities.add(currEntity);
					} else if (substitute == true) {
						NamedEntity currEntity = new NamedEntity();
						currEntity.setStart((auxAnnotation.getStartNode().getOffset().intValue()));
						currEntity.setEnd((auxAnnotation.getEndNode().getOffset().intValue()));
						currEntity.setValue(entity.trim());
						currEntity.setType(KeywordType.fromString(auxAnnotation.getType()));
						auxEntities.set(index, currEntity);
					}
				}
			}
			Collections.sort(auxEntities);
			this.entities = auxEntities;
			auxCorpus.unloadDocument(doc);
		} catch (ResourceInstantiationException re) {
		} catch (GateException ge) {
		}
		return this.getEntities();
	}

	public ArrayList<NamedEntity> getEntities() {
		return entities;
	}

	public String tokenizeAndCorrect(String data) {
		StringBuffer exactString = new StringBuffer("");
		StringTokenizer token = new StringTokenizer(data);
		while (token.hasMoreTokens()) {
			exactString.append(firstLetterCaps(token.nextToken()));
			exactString.append(" ");
		}
		return exactString.toString();
	}

	public String firstLetterCaps(String data) {
		String firstLetter = data.substring(0, 1).toUpperCase();
		String restLetters = data.substring(1).toLowerCase();
		return firstLetter + restLetters;
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