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

	public Indexing(String indexPath) 
	{
		this.indexPath = indexPath;
	}

	public void createEmptyIndex() throws CorruptIndexException, LockObtainFailedException, IOException
	{
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
		iw.close();
	}

	public void createIndexCEDD(String imageFolderPath) throws IOException
	{
		long startTime = System.currentTimeMillis();
		// Getting all images from a directory and its sub directories.
		ArrayList<String> images = FileUtils.getAllImages(new File(imageFolderPath), true);

		// Creating a CEDD document builder and indexing al files.
		DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
		// Creating an Lucene IndexWriter
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
		// Iterating through images building the low level features
		for (Iterator<String> iterator = images.iterator(); iterator.hasNext(); ) {
			String imageFilePath = iterator.next();
			//System.out.println("Indexing " + imageFilePath);
			try {
				BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
				Document document = builder.createDocument(img, imageFilePath);
				iw.addDocument(document);
			} catch (Exception e) {
				System.err.println("Error reading image or indexing it.");
				e.printStackTrace();
			}
		}
		// closing the IndexWriter
		iw.close();
		logger.log(Level.FINE, "Index created in "+ (System.currentTimeMillis() - startTime) + " ms");
	}	

	public String updateIndexCEDDfromUrl(String URL) throws MalformedURLException, IOException 
	{
		InputStream is = (new java.net.URL(URL)).openStream();
		return updateIndexCEDD(is);
	}

	public String updateIndexCEDDfromPath(String imagePath) throws IOException
	{
		FileInputStream fis = new FileInputStream(imagePath);
		return updateIndexCEDD(fis);
	}

	public String updateIndexCEDD(InputStream is) throws IOException
	{
		long startTime = System.currentTimeMillis();
		String hash = Long.toString(startTime);
		// Creating an Lucene IndexWriter
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);

		// Creating a CEDD document builder and indexing al files.
		DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();

		try {
			BufferedImage img = ImageIO.read(is);
			Document document = builder.createDocument(img, hash);
			iw.addDocument(document);
		} catch (Exception e) {
			System.err.println("Error reading image or indexing it.");
			e.printStackTrace();
		}

		// closing the IndexWriter
		iw.close();
		logger.log(Level.FINE, "Index updated in "+ (System.currentTimeMillis() - startTime) + " ms");
		return hash;
	}

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
