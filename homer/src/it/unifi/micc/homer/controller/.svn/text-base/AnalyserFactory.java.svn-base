/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.util.HomerConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bertini
 *
 */
public class AnalyserFactory {
	@SuppressWarnings("rawtypes")
	protected Map<String, Class> map = defaultMap();
	
	public AnalyserFactory() {
		super();
	}
	
	public AbstractAnalyser create(String analysisName) {
		@SuppressWarnings("rawtypes")
		Class klass = map.get(analysisName); 
		if (klass == null)
			throw new RuntimeException(getClass() + " was unable to find an analysis named '" + analysisName + "'.");
		
		AbstractAnalyser analyserInstance = null;
		try { 
			analyserInstance = (AbstractAnalyser) klass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return analyserInstance;	
	}
	
	@SuppressWarnings("rawtypes")
	protected Map<String, Class> defaultMap() {
		Map<String, Class> map = new HashMap<String, Class>();
		map.put(HomerConstants.ANALYSER_TOPIC, TopicDetectionAnalyser.class); 
		map.put(HomerConstants.ANALYSER_NED, NEDAnalyser.class); 
		map.put(HomerConstants.ANALYSER_LANGUAGE, LanguageAnalyser.class);
		map.put(HomerConstants.ANALYSER_TAGCLOUD, TagCloudAnalyser.class);
		map.put(HomerConstants.ANALYSER_TRAINLDAMODEL, TopicTrainModelAnalyser.class);
		
		return map;
	}

}
