/**
 * 
 */
package it.unifi.micc.homer.model;

import java.util.List;

import it.unifi.micc.homer.model.language.Lc4jLangIdentifier;
import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;

/**
 * @author bertini
 *
 */
public class TextDocument {
	protected List<SemanticKeyword> keywords;
	protected String content;
	protected Language language;
	/**
	 * 
	 */
	public TextDocument() {
		super();
	}
	/**
	 * @param content
	 * @param language
	 */
	public TextDocument(String content, Language language) {
		super();
		this.content = content;
		this.language = language;
	}
	/**
	 * @param content
	 */
	public TextDocument(String content) {
		super();
		this.content = content;
		this.language = null;
	}
	
	public boolean autoSetLanguage(String langModelsPath, String langStopwordPath) {
		boolean result = false;
		Lc4jLangIdentifier langIdent = new Lc4jLangIdentifier(langModelsPath, langStopwordPath);
		language = langIdent.identifyLanguageOf(content);
		if( language != Language.unknown )
			result = true;
		
		return result;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the language
	 */
	public Language getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}
	/**
	 * @return the keywords
	 */
	public List<SemanticKeyword> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(List<SemanticKeyword> keywords) {
		this.keywords = keywords;
	}
}
