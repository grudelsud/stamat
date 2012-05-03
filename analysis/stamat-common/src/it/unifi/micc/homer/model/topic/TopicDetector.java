/**
 * 
 */
package it.unifi.micc.homer.model.topic;

import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.model.language.LanguageIdentifier.Language;
import it.unifi.micc.homer.model.language.Lc4jLangIdentifier;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.StringOperations;
import it.unifi.micc.homer.util.WordCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	private boolean disableLangDetection;
	private String langModelsPath = HomerConstants.DEFAULT_REL_LANG_MODELS_PATH;
	private String langStopwordPath = HomerConstants.DEFAULT_REL_LANG_STOPWORD_PATH;
	private int numIterations = 2000;
	private int numInterval = 50;
	private int numRepetitions = 3;
	private int numberOfTopics;
	private int numTopWords;

	/**
	 * @param langModelsPath
	 * @param langStopwordPath
	 */
	public TopicDetector(String langModelsPath, String langStopwordPath) {
		super();
		this.langModelsPath = langModelsPath;
		this.langStopwordPath = langStopwordPath;
		this.disableLangDetection = false;
	}

	public List<Topic> extractTopics(List<TextDocument> texts, int numberTopics, int numberTopWords) {

		// Pipes: tokenize, lowercase, remove stopwords, map to features
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		pipeList.add(new Input2CharSequence("UTF-8"));
		Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));
		pipeList.add(new TokenSequenceLowercase());
		// stopword removal is done at later stage: we should pass the stopword file name here...:
		// pipeList.add(new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		// pipeList.add(new TokenSequenceRemoveStopwords());
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		List<Instance> tmpInstanceList = new ArrayList<Instance>();

		Language language = null;
		StringBuffer allSanitizedText = new StringBuffer();
		for (TextDocument text : texts) {
			String sanitizedText = text.getContent();
			sanitizedText = StringOperations.removeURLfromString(sanitizedText);
			sanitizedText = StringOperations.removeMentions(sanitizedText);
			sanitizedText = StringOperations.removeNonLettersFromString(sanitizedText);

			if (!disableLangDetection) {
				Lc4jLangIdentifier lId = new Lc4jLangIdentifier(langModelsPath, langStopwordPath);
				sanitizedText = lId.cleanTextDocumentStopwords(text).getContent();
				// FIXME at present keywords are assigned to the language of the
				// last document ! Let's make at least a majority vote !
				language = text.getLanguage();
			}

			if (!sanitizedText.trim().equalsIgnoreCase("")) {
				Instance inst = new Instance(sanitizedText, null, text, text.getContent());
				tmpInstanceList.add(inst);
				allSanitizedText.append(sanitizedText);
			}
		}

		instances.addThruPipe(tmpInstanceList.iterator());

		MalletLogger.getLogger(ParallelTopicModel.class.getName()).setLevel(Level.OFF);

		if (numberTopics == HomerConstants.AUTODETECT_NUMTOPICS)
			numberOfTopics = estimateNumberOfTopics(allSanitizedText);
		else
			numberOfTopics = numberTopics;
		if (numberTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = estimateNumTopWords(allSanitizedText);
		else
			numTopWords = numberTopWords;

		List<Topic> topics = new ArrayList<Topic>();

		try {
			for(int i=0; i < getNumRepetitions(); i++) {
				ParallelTopicModel lda = new ParallelTopicModel(numberOfTopics);
				lda.setNumIterations(numIterations);
				lda.setOptimizeInterval(numInterval);
				lda.addInstances(instances);
				try {
					lda.estimate();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}

				// System.out.println(model.displayTopWords(5, true));
				Object[][] topWords = MalletTopWordsExtractor.getTopWordsWithWeights(lda.getSortedWords(),
						numberOfTopics, numTopWords, lda.getAlphabet());
				int limit = WordCounter.countWords(allSanitizedText.toString()) < numberOfTopics ? WordCounter.countWords(allSanitizedText.toString()) : numberOfTopics;
				// Object[][] topWords = model.getTopWords(numTopWords);
				for (int topicCount = 0; topicCount < limit; topicCount++) {
					Topic topic = new Topic(lda.alpha[topicCount], language);
					for (Object word : topWords[topicCount]) {
						// topic.addWord(word.toString());
						topic.addWord((TopicWord) word);
					}
					topics.add(topic);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return topics;
	}

	public List<Topic> trainTopicModel(List<TextDocument> texts, int numberTopics, int numberTopWords,TrainedModel modelLDA){
		// Pipes: tokenize, lowercase, remove stopwords, map to features
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		pipeList.add(new Input2CharSequence("UTF-8"));
		Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));
		pipeList.add(new TokenSequenceLowercase());
		// stopword removal is done at later stage: we should pass the stopword file name here...:
		// pipeList.add(new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		// pipeList.add(new TokenSequenceRemoveStopwords());
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		List<Instance> tmpInstanceList = new ArrayList<Instance>();

		Language language = null;
		StringBuffer allSanitizedText = new StringBuffer();
		for (TextDocument text : texts) {
			String sanitizedText = text.getContent();
			sanitizedText = StringOperations.removeURLfromString(sanitizedText);
			sanitizedText = StringOperations.removeMentions(sanitizedText);
			sanitizedText = StringOperations.removeNonLettersFromString(sanitizedText);

			if (!disableLangDetection) {
				Lc4jLangIdentifier lId = new Lc4jLangIdentifier(langModelsPath, langStopwordPath);
				sanitizedText = lId.cleanTextDocumentStopwords(text).getContent();
				// FIXME at present keywords are assigned to the language of the
				// last document ! Let's make at least a majority vote !
				language = text.getLanguage();
			}

			if (!sanitizedText.trim().equalsIgnoreCase("")) {
				Instance inst = new Instance(sanitizedText, null, text, text.getContent());
				tmpInstanceList.add(inst);
				allSanitizedText.append(sanitizedText);
			}
		}

		instances.addThruPipe(tmpInstanceList.iterator());

		MalletLogger.getLogger(ParallelTopicModel.class.getName()).setLevel(Level.OFF);

		if (numberTopics == HomerConstants.AUTODETECT_NUMTOPICS)
			numberOfTopics = estimateNumberOfTopics(allSanitizedText);
		else
			numberOfTopics = numberTopics;
		if (numberTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = estimateNumTopWords(allSanitizedText);
		else
			numTopWords = numberTopWords;

		List<Topic> topics = new ArrayList<Topic>();
		ParallelTopicModel lda = new ParallelTopicModel(numberOfTopics);
		try {
			lda.setNumIterations(numIterations);
			lda.setOptimizeInterval(numInterval);
			lda.addInstances(instances);
			lda.estimate();
			modelLDA.saveModelFile(lda);
			Object[][] topWords = MalletTopWordsExtractor.getTopWordsWithWeights(lda.getSortedWords(),
					numberOfTopics, numTopWords, lda.getAlphabet());
			int limit = WordCounter.countWords(allSanitizedText.toString()) < numberOfTopics ? WordCounter.countWords(allSanitizedText.toString()) : numberOfTopics;
			// Object[][] topWords = model.getTopWords(numTopWords);
			for (int topicCount = 0; topicCount < limit; topicCount++) {
				Topic topic = new Topic(lda.alpha[topicCount], language);
				for (Object word : topWords[topicCount]) {
					// topic.addWord(word.toString());
					topic.addWord((TopicWord) word);
				}
				topics.add(topic);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return topics;

	}


	public  List<Topic> InferenceTopic(List<TextDocument> texts,TrainedModel modelLDA, int numberTopWords) {

		// Pipes: tokenize, lowercase, remove stopwords, map to features
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		pipeList.add(new Input2CharSequence("UTF-8"));
		Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));
		pipeList.add(new TokenSequenceLowercase());
		// stopword removal is done at later stage: we should pass the stopword
		// file name here...:
		// pipeList.add( new TokenSequenceRemoveStopwords(new
		// File("stoplists/en.txt"), "UTF-8", false, false, false) );
		// pipeList.add(new TokenSequenceRemoveStopwords());
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		List<Instance> tmpInstanceList = new ArrayList<Instance>();

		Language language = null;
		StringBuffer allSanitizedText = new StringBuffer();
		for (TextDocument text : texts) {
			String sanitizedText = text.getContent();
			sanitizedText = StringOperations.removeURLfromString(sanitizedText);
			sanitizedText = StringOperations.removeMentions(sanitizedText);
			sanitizedText = StringOperations.removeNonLettersFromString(sanitizedText);

			if (!disableLangDetection) {
				Lc4jLangIdentifier lId = new Lc4jLangIdentifier(langModelsPath, langStopwordPath);
				sanitizedText = lId.cleanTextDocumentStopwords(text).getContent();
				// FIXME at present keywords are assigned to the language of the
				// last document ! Let's make at least a majority vote !
				language = text.getLanguage();
			}

			if (!sanitizedText.trim().equalsIgnoreCase("")) {
				Instance inst = new Instance(sanitizedText, null, text, text.getContent());
				tmpInstanceList.add(inst);
				allSanitizedText.append(sanitizedText);
			}
		}

		instances.addThruPipe(tmpInstanceList.iterator());

		Instance inst = instances.get(0);

		List<Topic> topics = new ArrayList<Topic>();
		ParallelTopicModel lda = modelLDA.getModel();
		TopicInferencer inferencer = lda.getInferencer();

		double[] alphaEstimated = inferencer.getSampledDistribution(inst, 10, 1, 5);
		//System.out.println("0\t" + testProbabilities[0]);

		Object[][] topWords = MalletTopWordsExtractor.getTopWordsWithWeights(lda.getSortedWords(),
				lda.getNumTopics(), numberTopWords, lda.getAlphabet());
		int limit = WordCounter.countWords(allSanitizedText.toString()) < lda.getNumTopics() ? WordCounter.countWords(allSanitizedText.toString()) : lda.getNumTopics();
		// Object[][] topWords = model.getTopWords(numTopWords);
		for (int topicCount = 0; topicCount < limit; topicCount++) {
			Topic topic = new Topic(alphaEstimated[topicCount], language);
			for (Object word : topWords[topicCount]) {
				// topic.addWord(word.toString());
				topic.addWord((TopicWord) word);
			}
			topics.add(topic);
		}


		return topics;

	}


	private int estimateNumTopWords(StringBuffer allSanitizedText) {
		int numTopics = estimateNumberOfTopics(allSanitizedText);
		int maxWords;
		if (numTopics < 2)
			maxWords = 8;
		else if (numTopics < 4)
			maxWords = numTopics * 6;
		else
			maxWords = (numTopics / 10) > 10 ? (numTopics * 8) : (numTopics * 7);
			if (maxWords > 100)
				maxWords = 100;
			return maxWords;
	}

	private int estimateNumberOfTopics(StringBuffer allSanitizedText) {
		int numWords = WordCounter.countWords(allSanitizedText.toString());
		int numTopics;
		if( numWords < 200 )
			numTopics = 1;
		else if( numWords < 500 )
			numTopics = 2;
		else 
			numTopics = (numWords / 1500) > 7 ? (numWords / 1500) : 3;
			if( numTopics > 15 )
				numTopics = 15;
			return numTopics;
	}

	/**
	 * @return the disableLangDetection
	 */
	public boolean isDisableLangDetection() {
		return disableLangDetection;
	}

	/**
	 * @param disableLangDetection
	 *            the disableLangDetection to set
	 */
	public void setDisableLangDetection(boolean disableLangDetection) {
		this.disableLangDetection = disableLangDetection;
	}

	/**
	 * @return the langModelsPath
	 */
	public String getLangModelsPath() {
		return langModelsPath;
	}

	/**
	 * @param langModelsPath
	 *            the langModelsPath to set
	 */
	public void setLangModelsPath(String langModelsPath) {
		this.langModelsPath = langModelsPath;
	}

	/**
	 * @return the langStopwordPath
	 */
	public String getLangStopwordPath() {
		return langStopwordPath;
	}

	/**
	 * @param langStopwordPath
	 *            the langStopwordPath to set
	 */
	public void setLangStopwordPath(String langStopwordPath) {
		this.langStopwordPath = langStopwordPath;
	}

	/**
	 * @return the numIterations
	 */
	public int getNumIterations() {
		return numIterations;
	}

	/**
	 * @param numIterations
	 *            the numIterations to set
	 */
	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

	/**
	 * @return the numInterval
	 */
	public int getNumInterval() {
		return numInterval;
	}

	/**
	 * @param numInterval the numInterval to set
	 */
	public void setNumInterval(int numInterval) {
		this.numInterval = numInterval;
	}

	/**
	 * @return the numRepetitions
	 */
	public int getNumRepetitions() {
		return numRepetitions;
	}

	/**
	 * @param numRepetitions the numRepetitions to set
	 */
	public void setNumRepetitions(int numRepetitions) {
		this.numRepetitions = numRepetitions;
	}

	/**
	 * @return the numberOfTopics
	 */
	public int getNumberOfTopics() {
		return numberOfTopics;
	}

	/**
	 * @param numberOfTopics the numberOfTopics to set
	 */
	public void setNumberOfTopics(int numberOfTopics) {
		this.numberOfTopics = numberOfTopics;
	}

	/**
	 * @return the numTopWords
	 */
	public int getNumTopWords() {
		return numTopWords;
	}

	/**
	 * @param numTopWords the numTopWords to set
	 */
	public void setNumTopWords(int numTopWords) {
		this.numTopWords = numTopWords;
	}

}
