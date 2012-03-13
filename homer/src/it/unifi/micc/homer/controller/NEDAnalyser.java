/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.namedentity.AnnieNEDDetector;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.WordCounter;

import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;


/**
 * @author bertini
 *
 */
public class NEDAnalyser extends AbstractAnalyser {

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.IAnalyser#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Vector<SemanticKeyword> process(HttpServletRequest request,
			HttpServletResponse response) {
		// get text
		String text = getAnyText(request);
		if( getTextTypeParameter(request).equals(HomerConstants.TEXTTYPE_HTML))
			text= Jsoup.parse(text).text();	//extract text from HTML document
		// get entity types
		ArrayList<KeywordType> keywordTypes = getKeywordsTypeParameter(request);
		
		AnnieNEDDetector ned = AnnieNEDDetector.getInstance();
		ArrayList<NamedEntity> result = ned.extractEntity(text, keywordTypes);	//returns entities without repetitions
		
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>(result.size());
		int docSize = WordCounter.countWords(text);
		for( NamedEntity an : result ) {
			SemanticKeyword as = convertNamedEntityToSemanticKeyword(an);
			as.setNumOccurrences(WordCounter.countWordInstances(text, as.getKeyword().trim()));
			as.setTf((float)((float)as.getNumOccurrences()/(float)docSize));
			if(as.getTf()==0)	// the regex of countWordInstances compute the presence of only separated words:
				as.setTf(1);	// if entity has some symbols next to it then it won't be counted	 
			v.add(as);
		}
		
		return v;
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.IAnalyser#writeToResponseStream(javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void writeToResponseStream(HttpServletResponse response,
			String output) {
		// TODO Auto-generated method stub

	}

//	/* (non-Javadoc)
//	 * @see it.unifi.micc.homer.controller.IAnalyser#search(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	@Override
//	public Vector<SemanticKeyword> analyse(String host, String db, String user,
//			String pass, String[] words, String inputURL, String inputFilePath,
//			String inputFileName, String outputPath, String outputFile)
//			throws HomerException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
	protected ArrayList<KeywordType> getKeywordsTypeParameter(HttpServletRequest request) {
		ArrayList<KeywordType> result = new ArrayList<KeywordType>();
		try {
			String[] keyword = request.getParameterValues( HomerConstants.POSTPAR_ENTITYTYPES );
			
			for(int i=0; i < keyword.length; i++) {
				String[] innerArray=keyword[i].split(","); 
				for(int j=0; j<innerArray.length; j++)
					result.add(KeywordType.fromString(innerArray[j]));
			}
		} catch (Exception e) {
			result.add(HomerConstants.DEFAULT_ENTITYTYPES);
		}
		return result;
	}
	private SemanticKeyword convertNamedEntityToSemanticKeyword(NamedEntity ne) {
		SemanticKeyword se = new SemanticKeyword(ne.getValue(), (float) 1.0, ne.getType(), 0.0);
		return se;
	}
}
