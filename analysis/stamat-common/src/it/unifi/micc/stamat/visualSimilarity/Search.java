package it.unifi.micc.stamat.visualSimilarity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class Search {

	private String indexPath;
	private float CLDWeight, SCDWeight, EHDWeight;
	private int numberOfResults;
	
	public Search(String indexPath, float CLDWeight, float SCDWeight, float EHDWeight, int numberOfResults) {
		this.indexPath = indexPath;
		this.CLDWeight = CLDWeight;
		this.SCDWeight = SCDWeight;
		this.EHDWeight = EHDWeight;
		this.numberOfResults = numberOfResults;
	}
	
	public List<SearchResult> search(File query) throws CorruptIndexException, IOException{
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexPath)));
		ImageSearcher searcher = ImageSearcherFactory.createWeightedSearcher(numberOfResults, SCDWeight, CLDWeight, EHDWeight);
		
		FileInputStream imageStream = new FileInputStream(query);
		BufferedImage bimg = ImageIO.read(imageStream);
		ImageSearchHits hits = searcher.search(bimg, reader);
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		//System.out.println("Query: " + query.getName() +" ...");
		for (int i = 1; i < numberOfResults; i++) {
			results.add(new SearchResult(query.getName(),hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue(),i-1,hits.score(i)));
		    //System.out.println(hits.score(i) + ": " + hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());
		}
		return results;
	}
}
