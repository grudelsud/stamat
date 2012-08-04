package stamat.controller.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.imageanalysis.bovw.SurfFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import stamat.util.StamatException;



public class Indexer {
	private String indexPath;
	private static Logger logger = Logger.getLogger(Indexer.class.getName());

	/**
	 * Creates an instance of indexing, which basically is a dummy object where all the create / update index methods must be run explicitly.
	 * With the only exception of method createEmptyIndex, all the other create / update methods will try to open an existing indexPath,
	 * or create a new index if the path does not contain a valid Lucene 3.6 index.
	 * 
	 * @param indexPath
	 */
	public Indexer(String indexPath) 
	{
		this.indexPath = indexPath;
	}

	/**
	 * Creates an empty index
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public void createEmptyIndex() throws IOException
	{
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
		iw.close();
	}

	/**
	 * @param is
	 * @param indexedFields
	 * @throws StamatException
	 * @throws  
	 * @throws IOException
	 */
	public void updateIndex(InputStream is, Map<String, String> indexedFields) throws StamatException, IOException
	{
		long startTime = System.currentTimeMillis();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);

		ChainedDocumentBuilder docBuilder = new ChainedDocumentBuilder();
	
		docBuilder.addBuilder(DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getScalableColorBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getCEDDDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getColorHistogramDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getColorLayoutBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getTamuraDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getEdgeHistogramBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getFCTHDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getGaborDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getJCDDocumentBuilder());
		docBuilder.addBuilder(DocumentBuilderFactory.getJpegCoefficientHistogramDocumentBuilder());
	
		docBuilder.addBuilder(new SiftDocumentBuilder());
		docBuilder.addBuilder(new SurfDocumentBuilder());
	
		String identifier = indexedFields.remove(DocumentBuilder.FIELD_NAME_IDENTIFIER);
		if( identifier == null ) {
			throw new StamatException("parameter indexedFields must contain at least one key with name " + DocumentBuilder.FIELD_NAME_IDENTIFIER);
		} else {
			try {
				BufferedImage img = ImageIO.read(is);
				Document doc = docBuilder.createDocument(img, identifier);
				for(String key : indexedFields.keySet()) {
					doc.add(new Field(key, indexedFields.get(key), Field.Store.YES, Field.Index.ANALYZED));
				}
				iw.addDocument(doc);				
			} catch(Exception e) {
				String msg = "id:" + identifier + " threw an exception during the indexing process, please check: " + e.getLocalizedMessage();
				logger.log(Level.SEVERE, msg);
				iw.close();
				throw new StamatException(msg);
			}
		}
		iw.close();
		logger.log(Level.INFO, "Index updated in "+ (System.currentTimeMillis() - startTime) + " ms");
	}

	/**
	 * @param URL
	 * @param indexedFields
	 * @throws StamatException
	 * @throws IOException
	 */
	public void updateIndexFromURL(String URL, Map<String, String> indexedFields) throws StamatException, IOException
	{
		InputStream is = (new java.net.URL(URL)).openStream();
		updateIndex(is, indexedFields);
	}

	/**
	 * @param imagePath
	 * @param indexedFields
	 * @throws StamatException
	 * @throws IOException
	 */
	public void updateIndexFromPath(String imagePath, Map<String, String> indexedFields) throws StamatException, IOException
	{
		FileInputStream fis = new FileInputStream(imagePath);
		updateIndex(fis, indexedFields);
	}

	public void createSIFTHistogram() throws IOException
	{
		// create the visual words.
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		// create a BoVW indexer
		int numDocsForVocabulary = 1500; // gives the number of documents for building the vocabulary (clusters).
		int numClusters = 8000;
		SiftFeatureHistogramBuilder sh = new SiftFeatureHistogramBuilder(ir, numDocsForVocabulary, numClusters);
		sh.index();		
	}

	public void createSURFHistogrm() throws IOException
	{
		// create the visual words.
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		// create a BoVW indexer
		int numDocsForVocabulary = 1500; // gives the number of documents for building the vocabulary (clusters).
		int numClusters = 8000;
		SurfFeatureHistogramBuilder sh = new SurfFeatureHistogramBuilder(ir, numDocsForVocabulary, numClusters);
		sh.index();				
	}

	@Deprecated
	public void updateIndexCEDD(InputStream is, String imageIdentifier) throws IOException
	{
		long startTime = System.currentTimeMillis();
		// Creating an Lucene IndexWriter
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
	
		// Creating a CEDD document builder and indexing al files.
		DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
	
		try {
			BufferedImage img = ImageIO.read(is);
			Document document = builder.createDocument(img, imageIdentifier);
			iw.addDocument(document);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	
		// closing the IndexWriter
		iw.close();
		logger.log(Level.INFO, "Index updated in "+ (System.currentTimeMillis() - startTime) + " ms");
	}

	/**
	 * Adds image from URL to this index instance. 
	 * Image Identifier is used to store a reference to this image on the index, URL will be used if set to null.
	 * 
	 * @param URL
	 * @param imageIdentifier
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Deprecated
	public void updateIndexCEDDfromUrl(String URL, String imageIdentifier) throws IOException 
	{
		if( imageIdentifier == null ) {
			imageIdentifier = URL;
		}
		InputStream is = (new java.net.URL(URL)).openStream();
		updateIndexCEDD(is, imageIdentifier);
	}

	/**
	 * Adds image from path to this index instance.
	 * Image Identifier is used to store a reference to this image on the index, imagePath will be used if set to null.
	 * 
	 * @param imagePath
	 * @param imageIdentifier
	 * @throws IOException
	 */
	@Deprecated
	public void updateIndexCEDDfromPath(String imagePath, String imageIdentifier) throws IOException
	{
		if( imageIdentifier == null ) {
			imageIdentifier = imagePath;
		}
		FileInputStream fis = new FileInputStream(imagePath);
		updateIndexCEDD(fis, imageIdentifier);
	}

	/**
	 * Add all files contained in folder to this index instance. Image identifiers will automatically set to filenames
	 * 
	 * @param imageFolderPath
	 * @throws IOException
	 */
	@Deprecated
	public void updateIndexCEDDfromFolder(String imageFolderPath) throws IOException
	{
		long startTime = System.currentTimeMillis();
		// Getting all images from a directory and its sub directories.
		ArrayList<String> images = FileUtils.getAllImages(new File(imageFolderPath), true);
	
		// Iterating through images building the low level features
		for (Iterator<String> iterator = images.iterator(); iterator.hasNext(); ) {
			String imageFilePath = iterator.next();
			updateIndexCEDDfromPath(imageFilePath, imageFilePath);
		}
		logger.log(Level.INFO, "Folder indexed in "+ (System.currentTimeMillis() - startTime) + " ms");
	}

	@Deprecated
	public void createIndexSIFT(String imageFolderPath) throws IOException 
	{
		// create the initial local features:
		ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
		builder.addBuilder(new SiftDocumentBuilder());
		// TODO: check IndexWriterConfig.OpenMode, should be set to CREATE_OR_APPEND for consistency
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath, true);
		ArrayList<String> images = FileUtils.getAllImages(new File(imageFolderPath), true);
		for (String identifier : images) {
			Document doc = builder.createDocument(new FileInputStream(identifier), identifier);
			iw.addDocument(doc);
		}
		iw.close();

		// create the visual words.
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		// create a BoVW indexer
		int numDocsForVocabulary = 1500; // gives the number of documents for building the vocabulary (clusters).
		int numClusters= 8000;
		SiftFeatureHistogramBuilder sh = new SiftFeatureHistogramBuilder(ir, numDocsForVocabulary, numClusters);
		// progress monitoring is optional and opens a window showing you the progress.
		sh.setProgressMonitor(new ProgressMonitor(null, "", "", 0, 100));  
		sh.index();
	}
}
