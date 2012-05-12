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

	private static Options buildOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "print this help and exit");
		
		OptionGroup ogMain = new OptionGroup();
		ogMain.addOption( OptionBuilder.hasArg(false).withDescription("extract topics from text, requires options -t -n -nk -lm -ls").withLongOpt("topic-extract").create("Tx"));
		ogMain.addOption( OptionBuilder.hasArg(false).withDescription("create image index, requires options -iI and -iF").withLongOpt("image-create-index").create("Vc"));
		ogMain.addOption( OptionBuilder.hasArg(false).withDescription("query for image similarity, requires options -i, -iI and -n").withLongOpt("image-query").create("Vq"));
		options.addOptionGroup(ogMain);

		options.addOption( OptionBuilder.hasArg().withArgName("imageIndexPath").withDescription("full path of folder containing image index").withLongOpt("image-index-path").create("iI"));
		options.addOption( OptionBuilder.hasArg().withArgName("imageFolderPath").withDescription("full path of folder containing images").withLongOpt("image-folder-path").create("iF"));
		options.addOption( OptionBuilder.hasArg().withArgName("imagePath").withDescription("full image path").withLongOpt("image-path").create("i"));
		options.addOption( OptionBuilder.hasArg().withArgName("num").withDescription("number of outputs (topics / results)").withLongOpt("num-outputs").create("n"));
		options.addOption( OptionBuilder.hasArg().withArgName("num").withDescription("number of keywords per topic").withLongOpt("num-keywords").create("nk"));

		options.addOption( OptionBuilder.hasArg().withArgName("textInput").withDescription("file or folder containing text input").withLongOpt("num-keywords").create("t"));
		options.addOption( OptionBuilder.hasArg().withArgName("langModelsPath").withDescription("full path of folder containing language models").withLongOpt("lang-models").create("lm"));
		options.addOption( OptionBuilder.hasArg().withArgName("langStopwordPath").withDescription("full path of folder containing stopwords").withLongOpt("lang-stopwords").create("ls"));
		return options;
	}

	private static void cmdRunner(CommandLine line, Options options) {
		if( line.hasOption("h") ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(new PrintWriter(System.out, true), 80, "java -jar stamat-cmdline.jar", "options:", options, 2, 4, "", true);
			return;
		}

		// query
		if( line.hasOption("Vq")) {
			String imagePath = line.getOptionValue("i");
			String indexPath = line.getOptionValue("I");
			String resultsNum = line.getOptionValue("n");
			if(imagePath == null | indexPath == null | resultsNum == null) {
				System.out.println("With -Vq use options: -i, -I, -n");
			}
			try {
				List<SearchResult> results = VisualSimilarity.queryImage(imagePath, indexPath, Integer.parseInt(resultsNum));
				for( SearchResult res : results ) {
					System.out.println(res.getPosition() + " - " +  res.getResult() + " [" + res.getSimilarity() + "]");
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			return;
		// index create
		} else if( line.hasOption("Vc")) {
			String indexPath = line.getOptionValue("I");
			String imageFolderPath = line.getOptionValue("F");
			if( indexPath == null | imageFolderPath == null ) {
				System.out.println("With -Vc use options: -I, -F");
			}
			try {
				VisualSimilarity.createIndex(indexPath, imageFolderPath);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			return;
		} else if( line.hasOption("Tx")) {
			// t -n -nk -lm -ls
			String text = line.getOptionValue("t");
			String numTopics = line.getOptionValue("n");
			String numKeywords = line.getOptionValue("nk");
			String langModels = line.getOptionValue("lm");
			String langStopwords = line.getOptionValue("ls");
			
			JSONObject result = Analyser.topicAnalysisJSON(text, Integer.parseInt(numTopics), Integer.parseInt(numKeywords), langModels, langStopwords);
			System.out.println(result.toString());
		}

		System.out.println("Type 'java -jar stamat-cmdline.jar -h' for help");
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
			System.err.println( e.getMessage() );
		}
	}

}
