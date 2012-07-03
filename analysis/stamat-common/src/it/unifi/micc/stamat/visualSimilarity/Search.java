package it.unifi.micc.stamat.visualSimilarity;


import java.awt.image.BufferedImage;
import java.io.File;
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
	private int numberOfResults;
	
	public Search(String indexPath, int numberOfResults) {
		this.indexPath = indexPath;
		this.numberOfResults = numberOfResults;
	}
	
	public List<SearchResult> searcherCEDD(File query) throws CorruptIndexException, IOException{
		BufferedImage img = null;
		img = ImageIO.read(query);
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);

        ImageSearchHits hits = searcher.search(img, ir);
        	
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		for (int i = 0; i < numberOfResults; i++) {
			String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			results.add(new SearchResult(query.getName(),fileName,i,hits.score(i)));
		    //System.out.println(hits.score(i) + ": " + fileName);
		}
		return results;
		
	}
}
