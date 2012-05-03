/**
 * 
 */
package it.unifi.micc.homer;

import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.namedentity.AnnieNEDDetector;
import it.unifi.micc.homer.util.WordCounter;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author bertini
 *
 */
public class NEDAnalyser {

	public static Vector<SemanticKeyword> process(String text, ArrayList<KeywordType> keywordTypes) {
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
	
	private static SemanticKeyword convertNamedEntityToSemanticKeyword(NamedEntity ne) {
		SemanticKeyword se = new SemanticKeyword(ne.getValue(), (float) 1.0, ne.getType(), 0.0);
		return se;
	}
}
