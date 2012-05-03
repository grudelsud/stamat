/**
 * 
 */
package it.unifi.micc.homer;

import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.model.topic.Topic;
import it.unifi.micc.homer.model.topic.TopicDetector;
import it.unifi.micc.homer.model.topic.TrainedModel;
import it.unifi.micc.homer.util.HomerConstants;

import java.util.List;
import java.util.Vector;

/**
 * @author bertini
 *
 */
public class TopicModelTrainer {

	public static Vector<SemanticKeyword> process(String modelPath, List<TextDocument> trainingTexts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath) throws Exception {

		TopicDetector topicd = new TopicDetector(langModelsPath, langStopwordPath);
		TrainedModel modelLDA = TrainedModel.getInstance(modelPath);
		List<Topic> detectedTopics = topicd.trainTopicModel(trainingTexts, numTopics, numTopWords, modelLDA);		
	    
		if (numTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = topicd.getNumTopWords();
		
		Vector<SemanticKeyword> detectedSW = new Vector<SemanticKeyword>();
		for( Topic topic : detectedTopics ) {
			detectedSW.addAll(SemanticKeyword.convertTopicToSemanticKeyword(topic));
		}
		Vector<SemanticKeyword> result = SemanticKeyword.selectTopSemanticKeywords(detectedSW, numTopWords*numTopics);
		
		return result; 
	}
}
