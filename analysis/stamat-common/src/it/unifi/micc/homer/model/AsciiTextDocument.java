/**
 * 
 */
package it.unifi.micc.homer.model;

import java.io.Serializable;

import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;

/**
 * @author bertini
 * 
 */
public class AsciiTextDocument extends TextDocument implements Serializable {

	public AsciiTextDocument(String content, Language language) {
		super(content, language);
	}
	
	public AsciiTextDocument(String content) {
		super(content, null);
	}
}
