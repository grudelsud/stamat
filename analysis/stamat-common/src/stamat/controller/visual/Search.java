package stamat.controller.visual;



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
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

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
	
	public List<SearchResult> searcherSift(File queryFile) throws CorruptIndexException, IOException{
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexPath)));
        String queryFileName = queryFile.getAbsolutePath();
        Query qFile;
		try {
			qFile = new QueryParser(Version.LUCENE_34, "descriptorImageIdentifier", new KeywordAnalyzer()).parse(queryFileName);
			IndexSearcher searcherIndexSift = new IndexSearcher(reader);
		    int hitsPerPage = 1;
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		    searcherIndexSift.search(qFile, collector);
		    ScoreDoc[] hitsIndex = collector.topDocs().scoreDocs;
		    int docId = hitsIndex[0].doc;
		    Document query = searcherIndexSift.doc(docId);
		    System.out.println((1) + ". " + query.get("descriptorImageIdentifier"));
		    
		    VisualWordsImageSearcher searcher = new VisualWordsImageSearcher(numberOfResults, 
	                 DocumentBuilder.FIELD_NAME_SIFT_LOCAL_FEATURE_HISTOGRAM_VISUAL_WORDS);
		    ImageSearchHits hits = searcher.search(query, reader);
	        // show or analyze your results ....
	        
			for (int i = 0; i < numberOfResults; i++) {
				String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
				results.add(new SearchResult(queryFile.getName(),fileName,i,hits.score(i)));
			    System.out.println(hits.score(i) + ": " + fileName);
			}
		    
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        
		return results;
		
	}
}
