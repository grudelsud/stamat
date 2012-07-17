/**
 * 
 */
package it.unifi.micc.homer.model;

import java.io.Serializable;

import it.unifi.micc.homer.controller.language.LanguageIdentifier.Language;

/**
 * @author bertini
 * 
 */
public class AsciiTextDocument extends TextDocument implements Serializable {

	public AsciiTextDocument(String content, String language) {
		super(content, language);
	}
	
	public AsciiTextDocument(String content) {
		super(content, null);
	}
}
