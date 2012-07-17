/**
 * 
 */
package it.unifi.micc.homer.controller.namedentity;

import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * @author alisi
 *
 */
public class StanfordNERecognizer implements NamedEntityDetector {

	private static String serializedClassifier;
	private static StanfordNERecognizer instance = null;
	
	private ArrayList<NamedEntity> entities = null;
	
	private StanfordNERecognizer() {
	}

	public static StanfordNERecognizer getInstance(String serializedClassifier) {
		if(instance == null) {
			instance = new StanfordNERecognizer();
			StanfordNERecognizer.serializedClassifier = serializedClassifier;
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.namedentity.NamedEntityDetector#extractEntity(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public ArrayList<NamedEntity> extractEntity(String text, ArrayList<KeywordType> type) {
		entities = new ArrayList<NamedEntity>();
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(this.serializedClassifier);
		String out = "<xml>" + classifier.classifyWithInlineXML(text).replaceAll("(\\[|\\])", "") + "</xml>";
		Document doc = Jsoup.parse(out, "", Parser.xmlParser());
		try {
			Elements people = doc.getElementsByTag("person");
			for(Element element : people) {
				entities.add(new NamedEntity(KeywordType.PERSON, element.html()));
			}
			Elements organizations = doc.getElementsByTag("organization");
			for(Element element : organizations) {
				entities.add(new NamedEntity(KeywordType.ORGANIZATION, element.html()));
			}
			Elements locations = doc.getElementsByTag("location");
			for(Element element : locations) {
				entities.add(new NamedEntity(KeywordType.LOCATION, element.html()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entities;
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.namedentity.NamedEntityDetector#getEntities()
	 */
	@Override
	public ArrayList<NamedEntity> getEntities() {
		return this.entities;
	}

}
