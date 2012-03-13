/**
 * 
 */
package it.unifi.micc.homer.model;

import it.unifi.micc.homer.util.HomerException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author bertini
 *
 */
public class SemanticKeyword {
	private String keyword;
	private float confidence;
	private double probability;
	private float tf;
	private KeywordType type;
	private int numOccurrences;
	
	/**
	 * @param keyword
	 * @param confidence
	 * @param type
	 */
	public SemanticKeyword(String keyword, float confidence, KeywordType type, double probability) {
		super();
		this.keyword = keyword;
		this.confidence = confidence;
		this.type = type;
		numOccurrences = 0;
		tf = (float) 0.0;
		this.probability=probability;
	}
	
	public JSONObject toJSONItem() throws HomerException {
		try {
			JSONObject oJson = new JSONObject();
			String sanitizedKeyword = StringEscapeUtils.escapeHtml4(keyword);
			oJson.put("keyword", sanitizedKeyword);
			oJson.put("tf", tf);
			oJson.put("occurrences", numOccurrences);
			oJson.put("confidence", confidence);
			oJson.put("probability", probability);
			return oJson;


		} catch (Throwable t) {
			throw new HomerException(t);
	}
	}

	public Document toRSSItem() throws HomerException {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder = factory.newDocumentBuilder();
			DOMImplementation      impl    = builder.getDOMImplementation();
			Document               xmldoc  = impl.createDocument(null, "item", null);

			// Root element.
			Element root = xmldoc.getDocumentElement();
			
			Element keywordTypeElement = xmldoc.createElementNS(null, type.toString().toLowerCase());
			root.appendChild(keywordTypeElement);
			
			Element e = null;
			Node    n = null;

			e = xmldoc.createElementNS(null, "keyword");
			String sanitizedKeyword = StringEscapeUtils.escapeHtml4(keyword); 
			n = xmldoc.createTextNode( sanitizedKeyword );
			e.appendChild(n);
			keywordTypeElement.appendChild(e);
			
			e = xmldoc.createElementNS(null, "tf");
			n = xmldoc.createTextNode( String.valueOf( tf ) );
			e.appendChild(n);
			keywordTypeElement.appendChild(e);
			
			e = xmldoc.createElementNS(null, "occurrences");
			n = xmldoc.createTextNode( String.valueOf( numOccurrences ) );
			e.appendChild(n);
			keywordTypeElement.appendChild(e);

			e = xmldoc.createElementNS(null, "confidence");
			n = xmldoc.createTextNode( String.valueOf( confidence ) );
			e.appendChild(n);
			keywordTypeElement.appendChild(e);
			
			e = xmldoc.createElementNS(null, "probability");
			n = xmldoc.createTextNode( String.valueOf( probability ) );
			e.appendChild(n);
			keywordTypeElement.appendChild(e);

			return xmldoc;

		} catch( Throwable t ) {
			throw new HomerException( t );
		}

	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the confidence
	 */
	public float getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	/**
	 * @return the type
	 */
	public KeywordType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(KeywordType type) {
		this.type = type;
	}

	/**
	 * @return the numOccurrences
	 */
	public int getNumOccurrences() {
		return numOccurrences;
	}

	/**
	 * @param numOccurrences the numOccurrences to set
	 */
	public void setNumOccurrences(int numOccurrences) {
		this.numOccurrences = numOccurrences;
	}

	/**
	 * @return the tf
	 */
	public float getTf() {
		return tf;
	}

	/**
	 * @param tf the tf to set
	 */
	public void setTf(float tf) {
		this.tf = tf;
	}
}
