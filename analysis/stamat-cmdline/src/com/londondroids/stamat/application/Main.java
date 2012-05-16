/**
 * 
 */
package com.londondroids.stamat.application;

import it.unifi.micc.homer.Analyser;
import it.unifi.micc.stamat.visualSimilarity.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.*;
import org.json.JSONObject;


/**
 * @author alisi
 *
 */
public class Main {

	@SuppressWarnings("static-access")
	private static Options buildOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "print this help and exit");
		
		/******************************************************************
		 * main switch, mutual exclusion
		 */ 
		OptionGroup ogMain = new OptionGroup();

		// language detect
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("detect language of text input, requires options -t -lm -ls")
				.withLongOpt("language-detect")
				.create("L"));
		// topic extract
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("extract topics from text, requires options -t -n -nk -lm -ls")
				.withLongOpt("topic-extract")
				.create("Tx"));
		// image index create
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("create image index, requires options -iI and -iF")
				.withLongOpt("image-create-index")
				.create("Vc"));
		// image similarity query
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("query for image similarity, requires options -i, -iI and -n")
				.withLongOpt("image-query")
				.create("Vq"));
		options.addOptionGroup(ogMain);

		/******************************************************************
		 * other options, see requires above
		 */

		// image index path
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("imageIndexPath")
				.withDescription("full path of folder containing image index")
				.withLongOpt("image-index-path")
				.create("iI"));
		
		// image folder path
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("imageFolderPath")
				.withDescription("full path of folder containing images")
				.withLongOpt("image-folder-path")
				.create("iF"));

		// image path
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("imagePath")
				.withDescription("full image path")
				.withLongOpt("image-path")
				.create("i"));

		// number of outputs
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("num")
				.withDescription("number of outputs (topics / results)")
				.withLongOpt("num-outputs")
				.withType(Number.class)
				.create("n"));

		// number of keywords per topic
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("num")
				.withDescription("number of keywords per topic")
				.withLongOpt("num-keywords")
				.withType(Number.class)
				.create("nk"));

		// text input
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("textInput")
				.withDescription("file or folder containing text input")
				.withLongOpt("num-keywords")
				.create("t"));

		// language profiles folder
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("langModelsPath")
				.withDescription("full path of folder containing language models")
				.withLongOpt("lang-models")
				.create("lm"));

		// stopwords folder
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("langStopwordPath")
				.withDescription("full path of folder containing stopwords")
				.withLongOpt("lang-stopwords")
				.create("ls"));
		return options;
	}

	private static void cmdRunner(CommandLine line, Options options) {
		if( line.hasOption("h") ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(new PrintWriter(System.out, true), 80, "java -jar stamat-cmdline.jar", "options:", options, 2, 4, "", true);
			return;
		}

		// grab all the options available on the command line
		String text = line.getOptionValue("t");
		String langModels = line.getOptionValue("lm");
		String langStopwords = line.getOptionValue("ls");

		String imagePath = line.getOptionValue("i");
		String indexPath = line.getOptionValue("I");
		String imageFolderPath = line.getOptionValue("F");

		int numOutputs = 0;
		int numKeywords = 0;
		
		try {
			if( line.getOptionValue("n") != null ) {
				numOutputs = ((Number)line.getParsedOptionValue("n")).intValue();
			}
			if( line.getOptionValue("nk") != null ) {
				numKeywords = ((Number)line.getParsedOptionValue("nk")).intValue();
			}
		} catch (ParseException e) {
			// do nothing since values are initialized with 0 and result will make no sense
			System.out.println("Wrong input format! " + e.getMessage());
			return;
		}

		// cool, now run the main "switch" for command line options
		// image query
		if( line.hasOption("Vq")) {
			if(imagePath == null | indexPath == null | numOutputs == 0) {
				System.out.println("With -Vq use options: -i, -I, -n");
			} else {
				try {
					List<SearchResult> results = VisualSimilarity.queryImage(imagePath, indexPath, numOutputs);
					for( SearchResult res : results ) {
						System.out.println(res.getPosition() + " - " +  res.getResult() + " [" + res.getSimilarity() + "]");
					}
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}				
			}
			return;

		// image index create
		} else if( line.hasOption("Vc")) {
			if( indexPath == null | imageFolderPath == null ) {
				System.out.println("With -Vc use options: -I, -F");
			} else {
				try {
					VisualSimilarity.createIndex(indexPath, imageFolderPath);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}				
			}
			return;

		// topic extract
		} else if( line.hasOption("Tx")) {
			if( text == null | numOutputs == 0 | numKeywords == 0 | langModels == null | langStopwords == null ) {
				System.out.println("With -Tx use options: -t -n -nk -lm -ls");
			} else {			
				JSONObject result = Analyser.topicAnalysisJSON(text, numOutputs, numKeywords, langModels, langStopwords);
				System.out.println(result.toString());
			}
			return;

		// language detect
		} else if( line.hasOption("L")) {
			if( text == null | langModels == null | langStopwords == null ) {
				System.out.println("With -L use options: -t -lm -ls");
			} else {			
				JSONObject result = Analyser.languageDetectionJSON(text, langModels, langStopwords);
				System.out.println(result.toString());
			}
			return;
		} else {
			System.out.println("Type 'java -jar stamat-cmdline.jar -h' for help");			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = Main.buildOptions();

		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);
			Main.cmdRunner(line, options);
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

}
