/**
 * 
 */
package it.unifi.micc.homer;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.model.topic.Topic;
import it.unifi.micc.homer.model.topic.TopicDetector;
import it.unifi.micc.homer.model.topic.TrainedModel;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.WordCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author bertini
 *
 */
public class TopicDetectionAnalyser {

	public static Vector<SemanticKeyword> process(String text, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) {
		
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		List<TextDocument> texts = new ArrayList<TextDocument>();
		texts.add(textDocument);
		
		TopicDetector topicd = new TopicDetector(langModelsPath, langStopwordPath);
		List<Topic> detectedTopics;
		
		if( ldaModelPath != null ) {
			try {
				TrainedModel modelLDA = TrainedModel.getInstance(ldaModelPath);
				detectedTopics = topicd.InferenceTopic(texts,modelLDA,numTopWords); 
			} catch (Exception e) {
				detectedTopics = topicd.extractTopics(texts, numTopics, numTopWords);
			}
		} else {
			detectedTopics = topicd.extractTopics(texts, numTopics, numTopWords);
		}
		
		if (numTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = topicd.getNumTopWords();
		
		Vector<SemanticKeyword> detectedSW = new Vector<SemanticKeyword>();
		for( Topic topic : detectedTopics ) {
			detectedSW.addAll(SemanticKeyword.convertTopicToSemanticKeyword(topic));
		}
		Vector<SemanticKeyword> result = SemanticKeyword.selectTopSemanticKeywords(detectedSW, numTopWords*numTopics);
		int docSize = WordCounter.countWords(text);
		for( SemanticKeyword kw : result ) {
			kw.setNumOccurrences(WordCounter.countWordInstances(text, kw.getKeyword()));
			kw.setTf((float)((float)kw.getNumOccurrences()/(float)docSize));
		}
		return result;
	}
}
