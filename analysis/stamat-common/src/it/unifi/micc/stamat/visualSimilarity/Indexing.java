package it.unifi.micc.stamat.visualSimilarity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.utils.FileUtils;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;



public class Indexing {
	private String indexPath;
	
	
	public Indexing(String indexPath) {
		this.indexPath = indexPath;
	}
	
	
	public void createIndexCEDD(String imageFolderPath) throws IOException{
		
		//long starttime = System.currentTimeMillis();
		
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
        //System.out.println("Indexing time: "+ (System.currentTimeMillis()-starttime) + " ms");
   	
	}	
	
	
	public void updateIndexCEDD(String imagePath) throws IOException{
		//long starttime = System.currentTimeMillis();
		
		// Creating an Lucene IndexWriter
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
        IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), conf);
     
        // Creating a CEDD document builder and indexing al files.
        DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
        
        try {
            BufferedImage img = ImageIO.read(new FileInputStream(imagePath));
            Document document = builder.createDocument(img, imagePath);
            iw.addDocument(document);
        } catch (Exception e) {
            System.err.println("Error reading image or indexing it.");
            e.printStackTrace();
        }
    
    	// closing the IndexWriter
    	iw.close();
		//System.out.println("Indexing time: "+ (System.currentTimeMillis()-starttime) + " ms");
	}
	
}
