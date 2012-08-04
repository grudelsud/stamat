package stamat.controller.visual;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import stamat.util.StamatException;

public class Searcher {

	private String indexPath;
	private int numberOfResults;

	/**
	 * @param indexPath
	 * @param numberOfResults
	 */
	public Searcher(String indexPath, int numberOfResults) {
		this.indexPath = indexPath;
		this.numberOfResults = numberOfResults;
	}

	/**
	 * @param fileIdentifier
	 * @param fieldName
	 * @param numberOfResults
	 * @return
	 * @throws StamatException
	 * @throws IOException
	 */
	public List<SearchResult> search(String fileIdentifier, String fieldName, int numberOfResults) throws StamatException, IOException
	{
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		
		IndexSearcher luceneSearcher = new IndexSearcher(ir);
		Query query = new TermQuery(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER, fileIdentifier));
		TopDocs rs = luceneSearcher.search(query, 1);
		Document doc = luceneSearcher.doc(rs.scoreDocs[0].doc);
		ImageSearcher lireSearcher = Searcher.getSearcherFromFieldName(fieldName, numberOfResults);
		ImageSearchHits hits = lireSearcher.search(doc, ir);
		return getSearchResultListFromHits(hits);
	}

	/**
	 * @see stamat.controller.visual.Indexer.updateIndex for complete list of documentbuilders
	 * 
	 * @param is
	 * @param fieldName
	 * @return
	 * @throws StamatException 
	 */
	public List<SearchResult> search(InputStream is, String fieldName, int numberOfResults) throws IOException, StamatException
	{
		BufferedImage img = ImageIO.read(is);
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
	
		ImageSearcher searcher = Searcher.getSearcherFromFieldName(fieldName, numberOfResults);
		ImageSearchHits hits = searcher.search(img, ir);
		return getSearchResultListFromHits(hits);
	}

	/**
	 * @param fieldName
	 * @param numberOfResults
	 * @return
	 * @throws StamatException
	 */
	private static ImageSearcher getSearcherFromFieldName(String fieldName, int numberOfResults) throws StamatException
	{
		ImageSearcher searcher;
		if( fieldName == DocumentBuilder.FIELD_NAME_AUTOCOLORCORRELOGRAM) {
			searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_SCALABLECOLOR ) {
			searcher = ImageSearcherFactory.createScalableColorImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_CEDD ) {
			searcher = ImageSearcherFactory.createCEDDImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_COLORHISTOGRAM ) {
			searcher = ImageSearcherFactory.createColorHistogramImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_COLORLAYOUT ) {
			searcher = ImageSearcherFactory.createColorLayoutImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_TAMURA ) {
			searcher = ImageSearcherFactory.createTamuraImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_EDGEHISTOGRAM ) {
			searcher = ImageSearcherFactory.createEdgeHistogramImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_FCTH ) {
			searcher = ImageSearcherFactory.createFCTHImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_GABOR ) {
			searcher = ImageSearcherFactory.createGaborImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_JCD ) {
			searcher = ImageSearcherFactory.createJCDImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_JPEGCOEFFS ) {
			searcher = ImageSearcherFactory.createJpegCoefficientHistogramImageSearcher(numberOfResults);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_SIFT ) {
			searcher = new VisualWordsImageSearcher(numberOfResults, DocumentBuilder.FIELD_NAME_SIFT_LOCAL_FEATURE_HISTOGRAM_VISUAL_WORDS);
		} else if( fieldName == DocumentBuilder.FIELD_NAME_SURF ) {
			searcher = new VisualWordsImageSearcher(numberOfResults, DocumentBuilder.FIELD_NAME_SURF_LOCAL_FEATURE_HISTOGRAM_VISUAL_WORDS);
		} else {
			throw new StamatException("wrong query field");
		}
		return searcher;
	}

	/**
	 * @param hits
	 * @return
	 */
	private List<SearchResult> getSearchResultListFromHits(ImageSearchHits hits) {
		List<SearchResult> results = new ArrayList<SearchResult>();

		for (int i = 0; i < numberOfResults; i++) {
			String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			results.add(new SearchResult(fileName,i,hits.score(i)));
		}
		return results;
	}
	/**
	 * @param imagePath
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
	public List<SearchResult> searcherCEDDfromPath(String imagePath) throws IOException {
		FileInputStream fis = new FileInputStream(imagePath);
		return searcherCEDD(fis);
	}

	/**
	 * @param URL
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
	public List<SearchResult> searcherCEDDfromUrl(String URL) throws IOException {
		InputStream is = (new java.net.URL(URL)).openStream();
		return searcherCEDD(is);
	}

	/**
	 * @param isImageQuery
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
	public List<SearchResult> searcherCEDD(InputStream isImageQuery) throws IOException {
		BufferedImage img = ImageIO.read(isImageQuery);
		IndexReader ir = IndexReader.open(FSDirectory.open(new File(indexPath)));
		ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);

		ImageSearchHits hits = searcher.search(img, ir);
		List<SearchResult> results = new ArrayList<SearchResult>();

		for (int i = 0; i < numberOfResults; i++) {
			String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			results.add(new SearchResult(fileName,i,hits.score(i)));
			//System.out.println(hits.score(i) + ": " + fileName);
		}
		return results;
	}

	/**
	 * @param queryFile
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
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
				results.add(new SearchResult(fileName,i,hits.score(i)));
				System.out.println(hits.score(i) + ": " + fileName);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;

	}
}
