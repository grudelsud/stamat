/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.FakeServletRequest;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.util.CmdLineParser;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import java.util.Vector;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;

/**
 * @author bertini
 *
 */
public class HomerApp {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// parse command line
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option analysisOpt = parser.addStringOption('a',
				"analysis");
		CmdLineParser.Option extractOpt = parser.addStringOption('E',
				"extract-type");
		CmdLineParser.Option docOpt = parser.addStringOption('d', "docurl");
		CmdLineParser.Option textTypeOpt = parser.addStringOption('f',
				"texttype");
		CmdLineParser.Option numTopicOpt = parser.addIntegerOption('t',
				"numtopics");
		CmdLineParser.Option numKeyOpt = parser.addIntegerOption('k',
				"numkeywords");
		CmdLineParser.Option entTypeOpt = parser.addStringOption('e',
				"entitytypes");
		CmdLineParser.Option fileOutOpt = parser.addStringOption('o',
				"outputXMLFile");
		
		CmdLineParser.Option inputPathOpt = parser.addStringOption('p', "infilepath" );
		CmdLineParser.Option inputFilenameOpt = parser.addStringOption('i', "infilename");
		
		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		// set default values if they are not available
		String analysisValue = (String) parser.getOptionValue(analysisOpt,
				new String("ned"));
		String docValue = (String) parser
				.getOptionValue(docOpt, new String(""));
		String inputPathValue = (String) parser
				.getOptionValue(inputPathOpt);
		String inputFilenameValue = (String) parser
				.getOptionValue(inputFilenameOpt);
		String textTypeValue = (String) parser.getOptionValue(textTypeOpt,
				HomerConstants.TEXTTYPE_HTML);
		String textExtractValue = (String) parser.getOptionValue(extractOpt, HomerConstants.HTMLEXTRACT_ARTICLE);
		Integer numTopicValue = (Integer) parser.getOptionValue(numTopicOpt,
				new Integer(3));
		Integer numKeyValue = (Integer) parser.getOptionValue(numKeyOpt,
				new Integer(7));
		String entTypeValue = (String) parser.getOptionValue(entTypeOpt,
				new String("Allents"));
		String outputXMLFile = (String) parser.getOptionValue(fileOutOpt,
				new String("homerapp-output.xml"));
		process(analysisValue, docValue, inputPathValue, inputFilenameValue,
				textTypeValue, textExtractValue, numTopicValue, numKeyValue, entTypeValue,
				outputXMLFile);
	}

	/**
	 * @param analysisValue	ned = NED; topic = topic detection, language = language detection, tagcloud = tag histogram
	 * @param docValue URL of the document to be analyzed
	 * @param inputPathValue path of the document to be analyzed
	 * @param inputFilenameValue name of the file to be analyzed
	 * @param textTypeValue HTML or ASCII
	 * @param textExtractValue text extraction: ARTICLE
	 * @param numTopicValue number of topics (used for topic or tagcloud)
	 * @param numKeyValue number of keywords (used for topic or tagcloud)
	 * @param entTypeValue type of entities detected in NED: Allents to detect all types of known entities, Person, Location...
	 * @param outputXMLFile name of XML output file containing results
	 * @throws TransformerFactoryConfigurationError
	 */
	public static Document process(String analysisValue, String docValue,
			String inputPathValue, String inputFilenameValue,
			String textTypeValue, String textExtractValue, Integer numTopicValue, Integer numKeyValue,
			String entTypeValue, String outputXMLFile)
			throws TransformerFactoryConfigurationError {
		FakeServletRequest request = new FakeServletRequest();
		request.setPostparAnalysis(analysisValue);
		request.setPostparDocUrl(docValue);
		request.setPostparInFilePath(inputPathValue);
		request.setPostparInFilename(inputFilenameValue);
		if (textTypeValue.equals("HTML"))
			request.setPostparText(textTypeValue);
		else
			request.setPostparText("ASCII");
		request.setPostparTextExtraction(textExtractValue);
		request.setPostparNumTopics(numTopicValue.toString());
		request.setPostparNumKeywords(numKeyValue.toString());
		request.setPostparEntityType(entTypeValue);

		AnalyserFactory analyserFactory = new AnalyserFactory();
		AbstractAnalyser homerAnalyser = analyserFactory.create(analysisValue);
		Vector<SemanticKeyword> anResult = homerAnalyser.process(request, null);

		Document result = null;
		try {
			result = AbstractAnalyser.toRSSSearchResult("HomerApp", anResult,
					0, anResult.size());
		} catch (HomerException e) {
			e.printStackTrace();
		}
		try {
			AbstractAnalyser.writeXMLFileResponse(outputXMLFile, homerAnalyser,
					result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void printUsage() {
		System.err
				.println("Usage: HomerApp [-d,--docurl <doc URL>] [-a,--analysis <analysis type>] [-f,--texttype <format type>]\n"
						+ " [-t,--numtopics <num>] [-k,--numkeywords <num>] [-e,--entitytypes <types>] [-o,--outputXMLFile <file name>]");
	}

}
