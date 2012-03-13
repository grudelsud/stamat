/**
 * 
 */
package it.unifi.micc.homer.model.topic;

import java.util.ArrayList;
import java.util.List;

import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;

/**
 * @author bertini
 *
 */
public class Topic {
	private List<TopicWord> words;
	private double alpha;
	private Language language;
	
	public Topic(double alpha, Language language){
		this.words = new ArrayList<TopicWord>();
		this.alpha = alpha;
		this.language = language;
	}
	
	public double getAlpha(){
		return alpha;
	}
	
	public void addWord(TopicWord word){
		this.words.add(word);
	}
	
	public List<TopicWord> getWords(){
		return words;
	}
	
	public Language getLanguage(){
		return language;
	}
}
