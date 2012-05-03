package it.unifi.micc.stamat.visualSimilarity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class Indexing {
	private String indexPath;
	private String imageFolderPath;
	
	public Indexing(String indexPath, String imageFolderPath) {
		this.indexPath = indexPath;
		this.imageFolderPath = imageFolderPath;
	}
	
	public void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException{
		long starttime = System.currentTimeMillis();
		IndexWriter iw = new IndexWriter(FSDirectory.open(new File(indexPath)), new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
		// Create an appropriate DocumentBuilder
		// getExtensiveDocumentBuilder(): ColorLayout, EdgeHistogram and ScalableColor
		DocumentBuilder builder = DocumentBuilderFactory.getExtensiveDocumentBuilder();

		File dir = new File(imageFolderPath);
		int i = 0;
		int n = dir.listFiles().length;
		for(File file : dir.listFiles()){
			Document doc = builder.createDocument(new FileInputStream(file), file.getName());
			iw.addDocument(doc);
			System.out.println((++i)+"/"+n);
		}
		iw.optimize();
		iw.close();
		System.out.println("Indexing time: "+ (System.currentTimeMillis()-starttime) + " ms");
	}
}
