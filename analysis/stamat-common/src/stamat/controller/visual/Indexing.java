package stamat.controller.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;



public class Indexing {
	private String indexPath;
	private static Logger logger = Logger.getLogger(Indexing.class.getName());

	/**
	 * Creates an instance of indexing, which basically is a dummy object where all the create / update index methods must be run explicitly.
	 * With the only exception of method createEmptyIndex, all the other create / update methods will try to open an existing indexPath,
	 * or create a new index if the path does not contain a valid Lucene 3.6 index.
	 * 
	 * @param indexPath
	 */
	public Indexing(String indexPath) 
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
	public void createEmptyIndex() throws CorruptIndexException, LockObtainFailedException, IOException
	{
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
		iw.close();
	}

	/**
	 * Adds image from URL to this index instance. 
	 * Image Identifier is used to store a reference to this image on the index, URL will be used if set to null.
	 * 
	 * @param URL
	 * @param imageIdentifier
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String updateIndexCEDDfromUrl(String URL, String imageIdentifier) throws MalformedURLException, IOException 
	{
		if( imageIdentifier == null ) {
			imageIdentifier = URL;
		}
		InputStream is = (new java.net.URL(URL)).openStream();
		return updateIndexCEDD(is, imageIdentifier);
	}

	/**
	 * Adds image from path to this index instance.
	 * Image Identifier is used to store a reference to this image on the index, imagePath will be used if set to null.
	 * 
	 * @param imagePath
	 * @param imageIdentifier
	 * @return
	 * @throws IOException
	 */
	public String updateIndexCEDDfromPath(String imagePath, String imageIdentifier) throws IOException
	{
		if( imageIdentifier == null ) {
			imageIdentifier = imagePath;
		}
		FileInputStream fis = new FileInputStream(imagePath);
		return updateIndexCEDD(fis, imageIdentifier);
	}

	/**
	 * Add all files contained in folder to this index instance. Image identifiers will automatically set to filenames
	 * 
	 * @param imageFolderPath
	 * @throws IOException
	 */
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

	public String updateIndexCEDD(InputStream is, String imageIdentifier) throws IOException
	{
		long startTime = System.currentTimeMillis();
		String hash = Long.toString(startTime);
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
			System.err.println("Error reading image or indexing it.");
			e.printStackTrace();
		}

		// closing the IndexWriter
		iw.close();
		logger.log(Level.INFO, "Index updated in "+ (System.currentTimeMillis() - startTime) + " ms");
		return hash;
	}

	// TODO: check IndexWriterConfig.OpenMode, should be set to CREATE_OR_APPEND for consistency
	public void createIndexSIFT(String imageFolderPath) throws IOException 
	{
		// create the initial local features:
		ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
		builder.addBuilder(new SiftDocumentBuilder());
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
