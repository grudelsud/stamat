/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.tagcloud.TagCloud;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerProperties;
import it.unifi.micc.homer.util.WordCounter;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bertini
 * 
 */
public class TagCloudAnalyser extends AbstractAnalyser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unifi.micc.homer.controller.IAnalyser#process(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Vector<SemanticKeyword> process(HttpServletRequest request, HttpServletResponse response) {
		// get text
		String text = getAnyText(request);
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		int numTopWords = getKeywordsNumParameter(request);
		String langModelsPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_MODELS_PATH;
		String langStopwordPath = HomerProperties.getApplicationPath() + "/WEB-INF/" + HomerConstants.DEFAULT_REL_LANG_STOPWORD_PATH;
		
		if (numTopWords == HomerConstants.AUTODETECT_NUMKEYWORDS)
			numTopWords = estimateNumTopWords(textDocument);

		TagCloud tc = new TagCloud(langStopwordPath, langModelsPath);
		TreeMap<Integer, List<SemanticKeyword>> tags = tc.computeTagCloud(textDocument);
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>();
		int wordCounter = 0;
		Iterator<Entry<Integer, List<SemanticKeyword>>> it = tags.entrySet().iterator();
	    while (it.hasNext() && (wordCounter < numTopWords) ) {
	    	Entry<Integer, List<SemanticKeyword>> pairs = it.next();
	    	for( SemanticKeyword sk : pairs.getValue() ) {
	    		v.add(sk);
				wordCounter++;
	    	}
		}
		return v;
	}

	private int estimateNumTopWords(AsciiTextDocument textDocument) {
		int numWords = WordCounter.countWords(textDocument.getContent());
		if( (numWords / 15)<4 )
			numWords = (numWords % 15)*2;
		else if( (numWords % 15)<30)
			numWords = (numWords % 15)*3;
		else if( (numWords % 15)<60)
			numWords = (numWords % 15);
		else if( (numWords % 15)<200)
			numWords = (numWords % 15)/3;
		else if( (numWords % 15)<2000)
			numWords = (numWords % 15)/4;
		else if( (numWords % 15)<4000)
			numWords = (numWords % 15)/6;
		else
			numWords = (numWords % 15)/10;
		return numWords;
	}

	protected int getKeywordsNumParameter(HttpServletRequest request) {
		try {
			int keynum = Integer.parseInt(request.getParameter(HomerConstants.POSTPAR_NUMKEYWORDS));

			return keynum;
		} catch (Exception e) {
			return HomerConstants.DEFAULT_NUMKEYWORDS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unifi.micc.homer.controller.IAnalyser#writeToResponseStream(javax.
	 * servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void writeToResponseStream(HttpServletResponse response, String output) {
		// TODO Auto-generated method stub

	}

}
