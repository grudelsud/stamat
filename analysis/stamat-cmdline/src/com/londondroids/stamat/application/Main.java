/**
 * 
 */
package com.londondroids.stamat.application;

import it.unifi.micc.homer.Analyser;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.stamat.visualSimilarity.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
				.withDescription("detect language of text input, requires options (-t | -tp) -lm -ls")
				.withLongOpt("language-detect")
				.create("L"));
		// entity extract
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("entity extraction, requires options (-t | -tp) -ep")
				.withLongOpt("entity-extract")
				.create("E"));
		// topic model train
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("train model, requires options (-t | -tp) -n -nk -lm -ls -m")
				.withLongOpt("topic-train")
				.create("Tt"));
		// topic infer
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("infer topics from text using a reference model, requires options (-t | -tp) -n -lm -ls -m")
				.withLongOpt("topic-infer")
				.create("Ti"));
		// topic extract
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("extract topics from text, requires options (-t | -tp) -n -nk -lm -ls")
				.withLongOpt("topic-extract")
				.create("Tx"));
		// image index create
		ogMain.addOption( OptionBuilder
				.hasArg(false)
				.withDescription("create image index, requires options -iI, -iF, -i")
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

		// model path
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("modelPath")
				.withDescription("lda model path")
				.withLongOpt("model-path")
				.create("m"));

		// entity classifier path
		options.addOption( OptionBuilder
				.hasArg()
				.withArgName("entityClassPath")
				.withDescription("entity classifier path")
				.withLongOpt("entity-class-path")
				.create("ep"));

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
				.withDescription("text input from command line, can be used in combination with -tp")
				.withLongOpt("text")
				.create("t"));

		// text input
		options.addOption( OptionBuilder
				.hasOptionalArgs()
				.withArgName("f1 ... fn d1 ... dn")
				.withDescription("space separated sequence of files and/or directories containing text input, can be used in combination with -t")
				.withLongOpt("text-path")
				.create("tp"));

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
		String[] textPaths = line.getOptionValues("tp");
		String modelPath = line.getOptionValue("m");
		String langModels = line.getOptionValue("lm");
		String langStopwords = line.getOptionValue("ls");
		String entityClassifierPath = line.getOptionValue("ep");
		String imagePath = line.getOptionValue("i");
		String indexPath = line.getOptionValue("iI");
		String imageFolderPath = line.getOptionValue("iF");

		int numOutputs = 0;
		int numKeywords = 0;

		List<String> texts = new ArrayList<String>();

		if( text != null ) {
			texts.add(text);			
		}
		if( textPaths != null ) {
			try {
				for(String textPath : textPaths) {
					File file = new File(textPath);
					if( file.isDirectory() ) {
						File[] files = file.listFiles();
						for(File input : files) {
							texts.add(Main.fileReader(input));
						}
					} else if( file.isFile() ) {
						texts.add(Main.fileReader(file));
					}
				}
			} catch(Exception e) {
				System.out.println("Check text input!");
				return;
			}			
		}

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
				System.out.println("With -Vq use options: -i, -iI, -n");
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
			if( indexPath == null) {
				System.out.println("With -Vc use options: -iI");
			} else if(line.hasOption("i")) {
					try {
						VisualSimilarity.updateIndex(indexPath, imagePath);
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}				
				else{
					try {
						VisualSimilarity.createIndex(indexPath, imageFolderPath);
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}				
			}
			return;

		// topic extract
		} else if( line.hasOption("Tx")) {
			if( texts.size() < 1 | numOutputs == 0 | numKeywords == 0 | langModels == null | langStopwords == null ) {
				System.out.println("With -Tx use options: (-t | -tp) -n -nk -lm -ls");
			} else {
				JSONObject result = Analyser.topicExtractJSON(texts, numOutputs, numKeywords, langModels, langStopwords);
				System.out.println(result.toString());
			}
			return;

		// topic infer
		} else if( line.hasOption("Ti")) {
			if( texts.size() < 1 | numOutputs == 0 | modelPath == null | langModels == null | langStopwords == null ) {
				System.out.println("With -Ti use options: (-t | -tp) -n -lm -ls -m");
			} else {
				// TODO: check topic infer function, not sure it's working properly
				JSONObject result = Analyser.topicInferJSON(texts, langModels, langStopwords, numOutputs, modelPath);
				System.out.println(result.toString());
			}
			return;

		// topic model train
		} else if( line.hasOption("Tt")) {
			if( texts.size() < 1 | numOutputs == 0 | langModels == null | langStopwords == null | modelPath == null ) {
				System.out.println("With -Tt use options: (-t | -tp) -n -lm -ls -m");
			} else {
				JSONObject result = Analyser.trainModelJSON(texts, numOutputs, langModels, langStopwords, modelPath);
				System.out.println(result.toString());
			}
			return;

		// entity extract
		} else if( line.hasOption("E")) {
			if( texts.size() < 1 | entityClassifierPath == null ) {
				System.out.println("With -E use options: (-t | -tp) -ep");
			} else {
				JSONObject result = Analyser.entityExtractStanfordJSON(texts.toString(), entityClassifierPath);
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

	private static String fileReader(File file) throws HomerException
	{
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(file);
			int ch;
			while((ch = fis.read()) != -1) {
				sb.append((char)ch);
			}
			
		} catch(Exception e) {
			throw new HomerException(e);
		}
		return sb.toString();
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
