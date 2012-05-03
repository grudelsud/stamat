package it.unifi.micc.homer;

/**
 * 
 */

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.language.Lc4jLangIdentifier;

import java.util.Vector;

/**
 * @author bertini
 *
 */
public class LanguageAnalyser {

	public Vector<SemanticKeyword> process(String text, String langModelsPath, String langStopwordPath) {

		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		Lc4jLangIdentifier.setLangModelsPath(langModelsPath);
		Lc4jLangIdentifier.setLangStopwordPath(langStopwordPath);
		if( textDocument.getLanguage() == null ) {
			textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
		}
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>();
		SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float) 1.0, KeywordType.LANGUAGE, 0.0);
		v.add(sw);
		return v;
	}
}
