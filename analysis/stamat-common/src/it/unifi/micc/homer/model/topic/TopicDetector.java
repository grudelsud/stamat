/**
 * 
 */
package it.unifi.micc.homer.model.topic;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;
import it.unifi.micc.homer.model.language.LanguageDetector;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.StringOperations;
import it.unifi.micc.homer.util.WordCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.MalletLogger;

/**
 * @author bertini
 * 
 */
public class TopicDetector {
	private static boolean disableLangDetection = false;

	private static int numIterations = 2000;
	private static int numInterval = 50;
	private static int numRepetitions = 3;

	private String langModelsPath = "";
	private String langStopwordPath = "";
	private int numberOfTopics = HomerConstants.AUTODETECT_NUMTOPICS;
	private int numTopWords = HomerConstants.AUTODETECT_NUMKEYWORDS;

	/**
	 * @param langModelsPath
	 * @param langStopwordPath
	 */
	public TopicDetector(String langModelsPath, String langStopwordPath)
	{
		super();
		this.langModelsPath = langModelsPath;
		this.langStopwordPath = langStopwordPath;
	}

	public static Vector<SemanticKeyword> extract(String text, String langModelsPath, String langStopwordPath)
	{
		int numTopics = HomerConstants.AUTODETECT_NUMTOPICS;
		int numTopWords = HomerConstants.AUTODETECT_NUMKEYWORDS;
		return TopicDetector.extract(text, langModelsPath, langStopwordPath, numTopics, numTopWords);
	}

	public static Vector<SemanticKeyword> extract(String text, String langModelsPath, String langStopwordPath, int numTopics, int numTopWords)
	{
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		List<TextDocument> texts = new ArrayList<TextDocument>();
		texts.add(textDocument);
		
		TopicDetector topicd = new TopicDetector(langModelsPath, langStopwordPath);
		List<Topic> detectedTopics = topicd.extractTopics(texts, numTopics, numTopWords, null);
		
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

	public static Vector<SemanticKeyword> extract(String text, String langModelsPath, String langStopwordPath, int numTopics, int numTopWords, String ldaModelPath) throws Exception
	{		
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		List<TextDocument> texts = new ArrayList<TextDocument>();
		texts.add(textDocument);
		
		TrainedModel trainedModel = TrainedModel.getInstance(ldaModelPath);
		TopicDetector topicd = new TopicDetector(langModelsPath, langStopwordPath);
		List<Topic> detectedTopics = topicd.extractTopics(texts, numTopics, numTopWords, trainedModel.getModel());

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

	public static void train(List<TextDocument> trainingTexts, int numTopics, String langModelsPath, String langStopwordPath, String ldaModelPath) throws Exception
	{
		MalletLogger.getLogger(ParallelTopicModel.class.getName()).setLevel(Level.OFF);
		TrainedModel tm;
		try {
			tm = TrainedModel.getInstance(ldaModelPath);			
		} catch(Exception e) {
			tm = TrainedModel.createInstance(ldaModelPath, numTopics);
		}
		ParallelTopicModel lda = tm.getModel();
		InstanceList instances = TopicDetector.textDocumentList2instanceList(trainingTexts, langModelsPath, langStopwordPath, new StringBuffer(), new StringBuffer());

		lda.setNumIterations(numIterations);
		lda.setOptimizeInterval(numInterval);
		lda.addInstances(instances);
		lda.estimate();
		tm.saveModelFile(lda);
	}

	/**
	 * @param texts
	 * @param numberOfTopics
	 * @param numTopWords
	 * @param lda can be null, in this case it would create an empty lda model and add stuff to it
	 * @return
	 */
	private List<Topic> extractTopics(List<TextDocument> texts, int numberOfTopics, int numTopWords, ParallelTopicModel lda)
	{
		StringBuffer allSanitizedText = new StringBuffer();
		StringBuffer language = new StringBuffer();
		InstanceList instances = TopicDetector.textDocumentList2instanceList(texts, this.langModelsPath, this.langStopwordPath, allSanitizedText, language);
		MalletLogger.getLogger(ParallelTopicModel.class.getName()).setLevel(Level.OFF);

		if (numberOfTopics == HomerConstants.AUTODETECT_NUMTOPICS) {
			this.numberOfTopics = numberOfTopics = estimateNumberOfTopics(allSanitizedText);
		} else {
			this.numberOfTopics = numberOfTopics;
		}

		if (numTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS) {
			this.numTopWords = numTopWords = estimateNumTopWords(allSanitizedText);
		} else {
			this.numTopWords = numTopWords;
		}

		List<Topic> topics = new ArrayList<Topic>();

		try {
//			for(int i=0; i < TopicDetector.numRepetitions; i++) {
				if( lda == null ) {
					lda = new ParallelTopicModel(this.numberOfTopics);
				}
				lda.setNumIterations(TopicDetector.numIterations);
				lda.setOptimizeInterval(TopicDetector.numInterval);
				lda.addInstances(instances);
				try {
					lda.estimate();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}

				// System.out.println(model.displayTopWords(5, true));
				Object[][] topWords = MalletTopWordsExtractor.getTopWordsWithWeights(lda.getSortedWords(),this.numberOfTopics, this.numTopWords, lda.getAlphabet());
				int limit = WordCounter.countWords(allSanitizedText.toString()) < this.numberOfTopics ? WordCounter.countWords(allSanitizedText.toString()) : this.numberOfTopics;
				// Object[][] topWords = model.getTopWords(numTopWords);
				for (int topicCount = 0; topicCount < limit; topicCount++) {
					Topic topic = new Topic(lda.alpha[topicCount], language.toString());
					for (Object word : topWords[topicCount]) {
						// topic.addWord(word.toString());
						topic.addWord((TopicWord) word);
					}
					topics.add(topic);
				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return topics;
	}

	private List<Topic> inferenceTopic(List<TextDocument> texts, String ldaModelPath, int numberTopWords) throws Exception
	{
		StringBuffer allSanitizedText = new StringBuffer();
		StringBuffer language = new StringBuffer();
		InstanceList instances = TopicDetector.textDocumentList2instanceList(texts, this.langModelsPath, this.langStopwordPath, allSanitizedText, language);

		Instance inst = instances.get(0);
	
		List<Topic> topics = new ArrayList<Topic>();
		TrainedModel tm = TrainedModel.getInstance(ldaModelPath);
		ParallelTopicModel lda = tm.getModel();
		TopicInferencer inferencer = lda.getInferencer();
	
		double[] alphaEstimated = inferencer.getSampledDistribution(inst, 10, 1, 5);
		//System.out.println("0\t" + testProbabilities[0]);
	
		Object[][] topWords = MalletTopWordsExtractor.getTopWordsWithWeights(lda.getSortedWords(),
				lda.getNumTopics(), numberTopWords, lda.getAlphabet());
		int limit = WordCounter.countWords(allSanitizedText.toString()) < lda.getNumTopics() ? WordCounter.countWords(allSanitizedText.toString()) : lda.getNumTopics();
		// Object[][] topWords = model.getTopWords(numTopWords);
		for (int topicCount = 0; topicCount < limit; topicCount++) {
			Topic topic = new Topic(alphaEstimated[topicCount], language.toString());
			for (Object word : topWords[topicCount]) {
				// topic.addWord(word.toString());
				topic.addWord((TopicWord) word);
			}
			topics.add(topic);
		}
		return topics;
	}

	private static InstanceList textDocumentList2instanceList(List<TextDocument> texts, String langModelsPath, String langStopwordPath, StringBuffer allSanitizedText, StringBuffer language) {
		// Pipes: tokenize, lowercase, remove stopwords, map to features
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");

		pipeList.add(new Input2CharSequence("UTF-8"));
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));
		pipeList.add(new TokenSequenceLowercase());
		// stopword removal is done at later stage: we should pass the stopword file name here...:
		// pipeList.add(new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		// pipeList.add(new TokenSequenceRemoveStopwords());
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));
		List<Instance> tmpInstanceList = new ArrayList<Instance>();
		Map<String, Integer> langFreq = new LinkedHashMap<String, Integer>();

		for (TextDocument text : texts) {
			String sanitizedText = text.getContent();
			sanitizedText = StringOperations.removeURLfromString(sanitizedText);
			sanitizedText = StringOperations.removeMentions(sanitizedText);
			sanitizedText = StringOperations.removeNonLettersFromString(sanitizedText);

			if (!TopicDetector.disableLangDetection) {
				LanguageDetector lId;
				String lang;
				try {
					lId = LanguageDetector.getInstance(langModelsPath, langStopwordPath);
					sanitizedText = lId.cleanTextDocumentStopwords(text).getContent();
					lang = text.getLanguage();
				} catch (HomerException e) {
					lang = "unknown";
				}
				Integer val = langFreq.get(language) == null ? new Integer(1) : (Integer)(langFreq.get(language) + 1);
				langFreq.put(lang, val);
			}

			if (!sanitizedText.trim().equalsIgnoreCase("")) {
				Instance inst = new Instance(sanitizedText, null, text, text.getContent());
				tmpInstanceList.add(inst);
				allSanitizedText.append(sanitizedText);
			}
		}
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(langFreq.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
                return (m2.getValue()).compareTo(m1.getValue());
            }
        });

        language.append(list.get(0).getKey());
		instances.addThruPipe(tmpInstanceList.iterator());
		return instances;
	}

	/**
	 * used to autodetect number of topics depending on length of input
	 * 
	 * @param allSanitizedText
	 * @return
	 */
	public static int estimateNumTopWords(StringBuffer allSanitizedText) {
		int numTopics = estimateNumberOfTopics(allSanitizedText);
		int maxWords;
		if (numTopics < 2) {
			maxWords = 8;
		} else if (numTopics < 4) {
			maxWords = numTopics * 6;
		} else {
			maxWords = (numTopics / 10) > 10 ? (numTopics * 8) : (numTopics * 7);
		}
		if (maxWords > 100) {
			maxWords = 100;
		}
		return maxWords;
	}

	/**
	 * used to autodetect number of topics depending on length of input
	 * 
	 * @param allSanitizedText
	 * @return
	 */
	public static int estimateNumberOfTopics(StringBuffer allSanitizedText) {
		int numWords = WordCounter.countWords(allSanitizedText.toString());
		int numTopics;
		if( numWords < 200 ) {
			numTopics = 1;
		} else if( numWords < 500 ) {
			numTopics = 2;
		} else {
			numTopics = (numWords / 1500) > 7 ? (numWords / 1500) : 3;
		}
		if( numTopics > 15 ) {
			numTopics = 15;
		}
		return numTopics;
	}
}
