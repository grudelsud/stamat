/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.language.Lc4jLangIdentifier;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerProperties;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bertini
 *
 */
public class LanguageAnalyser extends AbstractAnalyser {

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.IAnalyser#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Vector<SemanticKeyword> process(HttpServletRequest request,
			HttpServletResponse response) {
		// get text
		String text = getAnyText(request);
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		String langModelsPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_MODELS_PATH;
		String langStopwordPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_STOPWORD_PATH;
		Lc4jLangIdentifier.setLangModelsPath(langModelsPath);
		Lc4jLangIdentifier.setLangStopwordPath(langStopwordPath);
		if( textDocument.getLanguage() == null )
			textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>();
		SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float) 1.0, KeywordType.LANGUAGE, 0.0);
		v.add(sw);
				
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

}
