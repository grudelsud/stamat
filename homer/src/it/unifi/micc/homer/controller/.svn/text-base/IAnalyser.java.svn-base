/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.SemanticKeyword;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bertini
 *
 */
public interface IAnalyser {
	
	public Vector<SemanticKeyword> process(HttpServletRequest request, HttpServletResponse response);
	public void writeToResponseStream(HttpServletResponse response, String output);
	
//	public Vector<SemanticKeyword> analyse( 
//			String host, String db, String user, String pass,
//			String[] words, String inputURL, String inputFilePath, 
//			String inputFileName, String outputPath, String outputFile) throws HomerException;

}
