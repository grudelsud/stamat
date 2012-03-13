/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.model.topic.Topic;
import it.unifi.micc.homer.model.topic.TopicDetector;
import it.unifi.micc.homer.model.topic.TopicWord;
import it.unifi.micc.homer.model.topic.TrainedModel;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.HomerProperties;
import it.unifi.micc.homer.util.HomerPropertiesLDAModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bertini
 *
 */
public class TopicTrainModelAnalyser extends AbstractAnalyser {

	
	public Vector<SemanticKeyword> process(HttpServletRequest request,HttpServletResponse response) {
		
		int numTopWords = getKeywordsNumParameter(request);
		int numTopics = getTopicsNumParameter(request);
		String langModelsPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_MODELS_PATH;
		String langStopwordPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_STOPWORD_PATH;
		
		List<TextDocument> texts = new ArrayList<TextDocument>();
		
		TopicDetector topicd = new TopicDetector(langModelsPath, langStopwordPath);
		
		
		
		TrainedModel modelLDA = new TrainedModel();
		String url_db = getDBURL(request);
		String db = getDBname(request);
		String user_db = getDBuser(request);
		String password_db = getDBpassword(request);
		
		
		modelLDA.setMYSQLparameters(url_db, db, user_db, password_db);
		
		String modelname = request.getParameter(HomerConstants.POSTPAR_MODELNAME);
		
		modelLDA.setModelFile(modelname);
		texts= modelLDA.getTrainingSample(modelname);
		
		List<Topic> detectedTopics = topicd.trainTopicModel(texts, numTopics, numTopWords,modelLDA);
		
	    
		if (numTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = topicd.getNumTopWords();
		
		Vector<SemanticKeyword> detectedSW = new Vector<SemanticKeyword>();
		for( Topic topic : detectedTopics ) {
			detectedSW.addAll(convertTopicToSemanticKeyword(topic));
		}
		Vector<SemanticKeyword> result = selectTopSemanticKeywords(detectedSW, numTopWords*numTopics);
		
		return result; 
	}

	
	
	private Vector<SemanticKeyword> selectTopSemanticKeywords(Vector<SemanticKeyword> detectedSW, int numTopWords) {
		Vector<SemanticKeyword> result = new Vector<SemanticKeyword>();
		if( detectedSW.size()<=numTopWords )
			result.addAll(detectedSW);
		else { // select only numTopWords
			// get unique SemanticKeyword list
			Map<String, SemanticKeyword> selectedSW = new HashMap<String, SemanticKeyword>();
			for( SemanticKeyword sw : detectedSW ) {
				SemanticKeyword test = selectedSW.get(sw.getKeyword());
				if( test == null )
					selectedSW.put(sw.getKeyword(), sw);
				else {
					test.setNumOccurrences(test.getNumOccurrences()+1);
					test.setConfidence((test.getConfidence()+sw.getConfidence())/2);
					selectedSW.put(test.getKeyword(), test);
				}
					
			}
			// convert Map into List
			Vector<SemanticKeyword> tempResult = new Vector<SemanticKeyword>();
			Iterator<Entry<String, SemanticKeyword>> it = selectedSW.entrySet().iterator();
			while (it.hasNext()) 
				tempResult.add(it.next().getValue());
			// reverse sort SemanticKeywords according to their frequency (from max to min)
			Collections.sort(tempResult, new Comparator<SemanticKeyword>() {
				public int compare(SemanticKeyword one, SemanticKeyword other) {
					if (one.getNumOccurrences() < other.getNumOccurrences())
						return 1;
					if (one.getNumOccurrences() == other.getNumOccurrences())
						return 0;
					return -1;
				}
			});
			int numWords = 0;
			// select most frequent SemanticKeyword
			for( SemanticKeyword sw : tempResult ) {
				if( (sw.getNumOccurrences() > 1) && (numWords<numTopWords) ) {
					result.add(sw);
					numWords++;
				}
			}
			// if there's need to select some more keyword
			if( (numTopWords-numWords)>0 ) {
				// reverse sort them according to confidence (from max to min)
				Collections.sort(tempResult, new Comparator<SemanticKeyword>() {
					public int compare(SemanticKeyword one, SemanticKeyword other) {
						if (one.getConfidence() < other.getConfidence())
							return 1;
						if (one.getConfidence() == other.getConfidence())
							return 0;
						return -1;
					}
				});
				// select most confident
				for( SemanticKeyword sw : tempResult ) {
					if( !result.contains(sw) && (numWords<numTopWords) ) {
						result.add(sw);
						numWords++;
					}
				}
			}
		}
		// reset number of occurrences (will be computed later)
		for( SemanticKeyword sw : result )
			sw.setNumOccurrences(0);
		return result;
	}

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.controller.IAnalyser#writeToResponseStream(javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void writeToResponseStream(HttpServletResponse response,
			String output) {
		// TODO Auto-generated method stub

	}

//	/* (non-Javadoc)
//	 * @see it.unifi.micc.homer.controller.IAnalyser#search(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	@Override
//	public Vector<SemanticKeyword> analyse(String host, String db, String user,
//			String pass, String[] words, String inputURL, String inputFilePath,
//			String inputFileName, String outputPath, String outputFile)
//			throws HomerException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
	
	protected String getDBpassword(HttpServletRequest request){
		try {
			String password_db = request.getParameter(HomerConstants.PROP_DBPASS);
			if (password_db.equals("")) {
				
				HomerPropertiesLDAModels homerPropertiesLDAModels;
				homerPropertiesLDAModels = HomerPropertiesLDAModels.getInstance(request.getSession().getServletContext().getRealPath("/"));
				password_db = (String) homerPropertiesLDAModels.getProperty( HomerConstants.PROP_DBPASS);
				}
			return password_db;
		}catch (Exception e) {
			return HomerConstants.DEFAULT_DBPWD;
		} catch (HomerException e) {
			e.printStackTrace();
			return HomerConstants.DEFAULT_DBPWD;
		}
	}
	
	protected String getDBuser(HttpServletRequest request){
		try {
			String db_user = request.getParameter(HomerConstants.PROP_DBUSER);
			if (db_user.equals("")){
				HomerPropertiesLDAModels homerPropertiesLDAModels;
				homerPropertiesLDAModels = HomerPropertiesLDAModels.getInstance(request.getSession().getServletContext().getRealPath("/"));
				db_user = (String) homerPropertiesLDAModels.getProperty( HomerConstants.PROP_DBUSER);
			}
			return db_user;
		}catch (Exception e) {
		return HomerConstants.DEFAULT_DBUSER;
		} catch (HomerException e) {
			e.printStackTrace();
			return HomerConstants.DEFAULT_DBUSER;
		}
	}
	
	protected String getDBURL(HttpServletRequest request){
		try {
			String url_db = request.getParameter(HomerConstants.PROP_DBHOST);
			if (url_db.equals("")) {
				HomerPropertiesLDAModels homerPropertiesLDAModels;
				homerPropertiesLDAModels = HomerPropertiesLDAModels.getInstance(request.getSession().getServletContext().getRealPath("/"));
				url_db = (String) homerPropertiesLDAModels.getProperty( HomerConstants.PROP_DBHOST);
			}
			return url_db;
		}catch (Exception e) {
		return HomerConstants.DEFAULT_DBURL;
		}catch (HomerException e) {
			e.printStackTrace();
			return HomerConstants.DEFAULT_DBURL;
		}
	}
	
	
	protected String getDBname(HttpServletRequest request){
		try {
			String db = request.getParameter(HomerConstants.PROP_DBNAME);
			if (db.equals("")) {
				HomerPropertiesLDAModels homerPropertiesLDAModels;
				homerPropertiesLDAModels = HomerPropertiesLDAModels.getInstance(request.getSession().getServletContext().getRealPath("/"));
				db = (String) homerPropertiesLDAModels.getProperty( HomerConstants.PROP_DBNAME);
			}
			return db;
		}catch (Exception e) {
		return HomerConstants.DEFAULT_DBNAME;
		}catch (HomerException e) {
			e.printStackTrace();
			return HomerConstants.DEFAULT_DBNAME;
		}
	}
	
	protected int getKeywordsNumParameter(HttpServletRequest request) {
		try {
			int keynum = Integer.parseInt(request.getParameter( HomerConstants.POSTPAR_NUMKEYWORDS ));
			
			return keynum;
		} catch (Exception e) {
			return HomerConstants.DEFAULT_NUMKEYWORDS;
		}
	}
	
	protected int getTopicsNumParameter(HttpServletRequest request) {
		try {
			int keynum = Integer.parseInt(request.getParameter( HomerConstants.POSTPAR_NUMTOPICS ));
			
			return keynum;
		} catch (Exception e) {
			return HomerConstants.DEFAULT_NUMTOPICS;
		}
	}
	private Vector<SemanticKeyword> convertTopicToSemanticKeyword(Topic topic) {
		Vector<SemanticKeyword> results = new Vector<SemanticKeyword>();
		for( TopicWord word : topic.getWords() ) {
			SemanticKeyword result = new SemanticKeyword(word.getWord(), (float) word.getWeight(), KeywordType.TOPIC, topic.getAlpha() );
			results.add(result);
		}
		return results;
	}
}
