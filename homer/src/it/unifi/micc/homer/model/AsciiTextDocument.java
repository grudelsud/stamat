/**
 * 
 */
package it.unifi.micc.homer.model;

import java.io.Serializable;

import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;

import org.jsoup.Jsoup;

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
	
	public AsciiTextDocument(String content, boolean cleanHTML) {
		String text = content;
		if( cleanHTML ) {
			text = Jsoup.parse(content).text();	//extract text from HTML document
		}
		language = null;
		content = text;
	}

}
