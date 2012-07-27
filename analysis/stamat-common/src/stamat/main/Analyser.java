package stamat.main;

import it.unifi.micc.homer.controller.namedentity.AnnieNEDDetector;
import it.unifi.micc.homer.controller.namedentity.NamedEntityDetector;
import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.util.WordCounter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import stamat.controller.ned.StanfordNERecognizer;
import stamat.controller.topic.TopicDetector;
import stamat.controller.visual.Indexing;
import stamat.controller.visual.Search;
import stamat.controller.visual.SearchResult;
import stamat.model.KeywordType;
import stamat.model.NamedEntity;
import stamat.model.SemanticKeyword;
import stamat.model.Topic;
import stamat.model.TopicWord;
import stamat.util.StamatException;

/**
 * 
 * @author alisi
 *
 */
public class Analyser {
	private static Logger logger = Logger.getLogger(Analyser.class.getName());

	public static class constants {
		public static final int SUCCESS = 0;
		public static final int ERROR = 1;
	}

	public static class ned {
		
		/**
		 * @param text
		 * @param classifierPath
		 * @return
		 */
		public static ArrayList<NamedEntity> extractStanford(String text, String classifierPath)
		{
			NamedEntityDetector ned = StanfordNERecognizer.getInstance(classifierPath);
			ArrayList<NamedEntity> entityList = ned.extractEntity(text, null);
			return evalTFs(text, entityList);
		}

		/**
		 * @param text
		 * @param classifierPath
		 * @return
		 */
		public static String extractStanford2XML(String text, String classifierPath)
		{
			StanfordNERecognizer sner = StanfordNERecognizer.getInstance(classifierPath);
			String result = sner.extractEntity2XML(text, null);
			return result;
		}

		/**
		 * @param text
		 * @param classifierPath
		 * @return
		 */
		public static JSONObject extractStanford2JSON(String text, String classifierPath)
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray entities = Analyser.semanticKeywordList2JSON(Analyser.ned.extractStanford(text, classifierPath));
				result.put("success", entities);
			} catch (JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			return result;
		}

		/**
		 * @param text
		 * @param keywordTypes
		 * @param gateHome
		 * @return
		 */
		public static ArrayList<NamedEntity> exractAnnie(String text, ArrayList<KeywordType> keywordTypes, String gateHome)
		{
			NamedEntityDetector ned = AnnieNEDDetector.getInstance(gateHome);
			ArrayList<NamedEntity> entityList = ned.extractEntity(text, keywordTypes);	//returns entities without repetitions		
			return evalTFs(text, entityList);
		}

		/**
		 * @param text
		 * @param keywordTypes
		 * @param gateHome
		 * @return
		 */
		public static JSONObject exractAnnie2JSON(String text, ArrayList<KeywordType> keywordTypes, String gateHome)
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray entities = Analyser.semanticKeywordList2JSON(Analyser.ned.exractAnnie(text, keywordTypes, gateHome));
				result.put("success", entities);
			} catch (JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			return result;
		}
	}


	public static class topic {
		/**
		 * @param texts
		 * @param numTopics
		 * @param numTopWords
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @return
		 * @throws Exception
		 */
		public static Vector<SemanticKeyword> extract(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath)
		{
			Vector<SemanticKeyword> semanticKeywordVector = null;
			try {
				semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords);
			} catch (StamatException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return semanticKeywordVector;
		}

		/**
		 * @param texts
		 * @param numTopics
		 * @param numTopWords
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @return
		 */
		public static JSONObject extract2JSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath)
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.topic.extract(texts, numTopics, numTopWords, langModelsPath, langStopwordPath));
				result.put("success", keywords);
			} catch (JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			return result;
		}

		/**
		 * @param texts
		 * @param numTopics
		 * @param numTopWords
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @param ldaModelPath
		 * @return
		 * @throws Exception
		 */
		public static Vector<SemanticKeyword> extract(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) 
		{
			Vector<SemanticKeyword> semanticKeywordVector = null;
			try {
				semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords, ldaModelPath);
			} catch (StamatException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return semanticKeywordVector;
		}

		/**
		 * @param texts
		 * @param numTopics
		 * @param numTopWords
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @param ldaModelPath
		 * @return
		 */
		public static JSONObject extract2JSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) 
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.topic.extract(texts, numTopics, numTopWords, langModelsPath, langStopwordPath, ldaModelPath));
				result.put("success", keywords);
			} catch (JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			return result;
		}

		/**
		 * @param texts
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @param numTopWords
		 * @param ldaModelPath
		 * @return
		 * @throws Exception
		 */
		public static List<Topic> infer(List<String> texts, String langModelsPath, String langStopwordPath, int numTopWords, String ldaModelPath)
		{
			List<Topic> topics = null;
			try {
				topics = TopicDetector.infer(texts, langModelsPath, langStopwordPath, numTopWords, ldaModelPath);
			} catch (StamatException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return topics;
		}

		/**
		 * @param texts
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @param numTopWords
		 * @param ldaModelPath
		 * @return
		 */
		public static JSONObject infer2JSON(List<String> texts, String langModelsPath, String langStopwordPath, int numTopWords, String ldaModelPath)
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray topicsJSON = Analyser.topicList2JSON(Analyser.topic.infer(texts, langModelsPath, langStopwordPath, numTopWords, ldaModelPath));
				result.put("success", topicsJSON);
			} catch(JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			return result;
		}

		/**
		 * @param trainingTexts
		 * @param numTopics
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @param ldaModelPath
		 * @return
		 */
		public static JSONObject trainModel2JSON(List<String> trainingTexts, int numTopics, String langModelsPath, String langStopwordPath, String ldaModelPath) 
		{
			JSONObject result = new JSONObject();
			try {
				TopicDetector.train(trainingTexts, numTopics, langModelsPath, langStopwordPath, ldaModelPath);
				result.put("success", "all good");
			} catch (JSONException e) {
				logger.log(Level.WARNING, e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return result;
		}
	}


	public static class language {
		
		/**
		 * @param text
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @return
		 * @throws Exception
		 */
		public static Vector<SemanticKeyword> detection(String text, String langModelsPath, String langStopwordPath)
		{
			AsciiTextDocument textDocument = new AsciiTextDocument(text);
			Vector<SemanticKeyword> semanticKeywordVector = new Vector<SemanticKeyword>();
			try {
				textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
			} catch (StamatException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
			SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float)1.0, KeywordType.LANGUAGE, 1.0);
			semanticKeywordVector.add(sw);
			return semanticKeywordVector;
		}

		/**
		 * @param text
		 * @param langModelsPath
		 * @param langStopwordPath
		 * @return
		 */
		public static JSONObject detection2JSON(String text, String langModelsPath, String langStopwordPath) 
		{
			JSONObject result = new JSONObject();
			try {
				JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.language.detection(text, langModelsPath, langStopwordPath));
				result.put("success", keywords);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			return result;
		}
	}
	
	/**
	 * class visual
	 * performs analysis on images and retrieves a list of similar images
	 * 
	 * commento beppe: righe di codice da riutilizzare
	 * List<SearchResult> currentResultSCD = searchSCD.search(new File("ucid.v2-png/" + query));
	 * List<SearchResult> currentResultCLD = searchCLD.search(new File("ucid.v2-png/" + query));
	 * List<SearchResult> currentResultEHD = searchEHD.search(new File("ucid.v2-png/" + query));
	 * 
	 * RankFusion rankFusion = new RankFusion(currentResultSCD, currentResultCLD, currentResultEHD);
	 * List<SearchResult> mergedWithBorda = rankFusion.mergeWithBORDACount();
	 * List<SearchResult> mergedWithRankProduct = rankFusion.mergeWithRankProduct();
	 * List<SearchResult> mergedWithInvertedRankPosition = rankFusion.mergeWithInvertedRankPosition();
	 *
	 */
	public static class visual {

		/**
		 * @param indexPath
		 */
		public static int createEmptyIndex(String indexPath, StringBuilder message)
		{
			message = message == null ? new StringBuilder() : message;
			Indexing indexing = new Indexing(indexPath);
			try {
				indexing.createEmptyIndex();
				message.append("index created: " + indexPath);
				logger.log(Level.INFO, "index created: " + indexPath);
				return constants.SUCCESS;
			} catch (CorruptIndexException e) {
				logger.log(Level.SEVERE, e.getMessage());
				message.append(e.getMessage());
			} catch (LockObtainFailedException e) {
				logger.log(Level.SEVERE, e.getMessage());
				message.append(e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
				message.append(e.getMessage());
			}
			return constants.ERROR;
		}

		/**
		 * @param indexPath
		 * @param URL
		 */
		public static void updateIndexCEDDfromURL(String indexPath, String URL, String imageIdentifier)
		{
			Indexing indexing = new Indexing(indexPath);
			try {
				indexing.updateIndexCEDDfromUrl(URL, imageIdentifier);
			} catch (MalformedURLException e) {
				logger.log(Level.WARNING, e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}

		/**
		 * @param indexPath
		 * @param imagePath
		 */
		public static void updateIndexCEDDfromPath(String indexPath, String imagePath, String imageIdentifier) {
			Indexing indexing = new Indexing(indexPath);
			try {
				indexing.updateIndexCEDDfromPath(imagePath, imageIdentifier);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	
		/**
		 * @param indexPath
		 * @param imageFolderPath
		 * @throws CorruptIndexException
		 * @throws LockObtainFailedException
		 * @throws IOException
		 */
		public static void updateIndexCEDDfromFolder(String indexPath, String imageFolderPath) {
			Indexing indexing = new Indexing(indexPath);
			try {
				indexing.updateIndexCEDDfromFolder(imageFolderPath);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}

		/**
		 * @param queryImage
		 * @param numberOfResults
		 * @return
		 * @throws CorruptIndexException
		 * @throws IOException
		 */
		public static List<SearchResult> query(File queryImage,String indexPath, float weightSCD, float weightCLD, float weightEHD, int numberOfResults) {
			Search search = new Search(indexPath, numberOfResults+1);
			List<SearchResult> currentResult = null;
			try {
				currentResult = search.searcherCEDD(queryImage);
			} catch (CorruptIndexException e) {
				logger.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return currentResult;
		}
	
		/**
		 * @param imagePath
		 * @param numberOfResults
		 * @return
		 * @throws CorruptIndexException
		 * @throws IOException
		 */
		public static List<SearchResult> query(String imagePath, String indexPath, int numberOfResults) {
			return Analyser.visual.query(new File(imagePath), indexPath, 1f, 1f, 1f, numberOfResults);
		}
	
		/**
		 * @param imagePath
		 * @param numberOfResults
		 * @return
		 * @throws CorruptIndexException
		 * @throws IOException
		 */
		public static JSONObject query2JSON(String imagePath, String indexPath, int numberOfResults) {
			try {
				return Analyser.searchResult2JSON(Analyser.visual.query(imagePath, indexPath, numberOfResults));
			} catch (JSONException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return null;		
		}		
	}

	/**
	 * @param text
	 * @param result
	 * @return
	 */
	private static ArrayList<NamedEntity> evalTFs(String text, ArrayList<NamedEntity> result) 
	{
		int docSize = WordCounter.countWords(text);
		for( NamedEntity an : result ) {
			String keyword = an.getKeyword().trim();
			int numOccurrences = WordCounter.countWordInstances(text, keyword);
			an.setNumOccurrences(numOccurrences);
			an.setTf((float)numOccurrences / (float)docSize);
	
			// the regex of countWordInstances compute the presence of only separated words: if entity has some symbols next to it then it won't be counted	 
			if(an.getTf()==0) {
				an.setTf(1);
			}
		}
		return result;
	}

	/**
	 * @param topicList
	 * @return
	 */
	private static JSONArray topicList2JSON(List<Topic> topicList) 
	{
		JSONArray topicsJSON = new JSONArray();
		for(Topic topic : topicList) {
			JSONObject topicJSON = new JSONObject();
			JSONArray wordsJSON = new JSONArray();
			try {
				List<TopicWord> words = topic.getWords();
				for(TopicWord word : words) {
					JSONObject wordJSON = new JSONObject();
					wordJSON.put("word", word.getWord());
					wordJSON.put("count", word.getCount());
					wordJSON.put("weight", word.getWeight());
					wordsJSON.put(wordJSON);
				}
				topicJSON.put("words", wordsJSON);
				topicJSON.put("alpha", topic.getAlpha());
				topicJSON.put("language", topic.getLanguage());
				topicsJSON.put(topicJSON);
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
		return topicsJSON;
	}

	/**
	 * @param semanticKeywordList
	 * @return
	 * @throws JSONException 
	 */
	private static JSONArray semanticKeywordList2JSON(List<? extends SemanticKeyword> semanticKeywordList) throws JSONException 
	{
		JSONArray keywordsJSON = new JSONArray();
		for(SemanticKeyword semanticKeyword : semanticKeywordList) {
			JSONObject semanticKeywordJSON = new JSONObject();

			semanticKeywordJSON.put("keyword", semanticKeyword.getKeyword());
			semanticKeywordJSON.put("type", semanticKeyword.getType());
			semanticKeywordJSON.put("confidence", semanticKeyword.getConfidence());
			semanticKeywordJSON.put("num_occurences", semanticKeyword.getNumOccurrences());
			semanticKeywordJSON.put("tf", semanticKeyword.getTf());
			keywordsJSON.put(semanticKeywordJSON);				
		}
		return keywordsJSON;
	}

	/**
	 * @param result
	 * @return
	 * @throws JSONException 
	 */
	private static JSONObject searchResult2JSON(List<SearchResult> result) throws JSONException {
		JSONObject jsonDoc = new JSONObject();
		int size = result != null ? result.size() : 0;
		JSONArray elementsArray = new JSONArray();
		if (size > 0) {
			for (SearchResult element : result){
				elementsArray.put(element.toJSONItem());
				jsonDoc.put("results", elementsArray);
			}

		} else {
			jsonDoc.put("result", elementsArray);
		}
		return jsonDoc;
	}
}
