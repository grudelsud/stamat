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
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
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
	 * @see stamat.controller.visual.Indexer.updateIndex for complete list, reported below
	 * 
	 * docBuilder.addBuilder(DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getScalableColorBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getCEDDDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getColorHistogramDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getColorLayoutBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getTamuraDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getEdgeHistogramBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getFCTHDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getGaborDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getJCDDocumentBuilder());
	 * docBuilder.addBuilder(DocumentBuilderFactory.getJpegCoefficientHistogramDocumentBuilder());
	 * docBuilder.addBuilder(new SiftDocumentBuilder());
	 *
	 * @param is
	 * @param field
	 * @return
	 */
	public List<SearchResult> search(InputStream is, String field)
	{
		// TODO: implement
		if( field == DocumentBuilder.FIELD_NAME_AUTOCOLORCORRELOGRAM) {
			
		} else if( field == DocumentBuilder.FIELD_NAME_SCALABLECOLOR ) {
			
		} else if( field == DocumentBuilder.FIELD_NAME_CEDD ) {

		} else if( field == DocumentBuilder.FIELD_NAME_COLORHISTOGRAM ) {

		} else if( field == DocumentBuilder.FIELD_NAME_COLORLAYOUT ) {

		} else if( field == DocumentBuilder.FIELD_NAME_TAMURA ) {

		} else if( field == DocumentBuilder.FIELD_NAME_EDGEHISTOGRAM ) {

		} else if( field == DocumentBuilder.FIELD_NAME_FCTH ) {

		} else if( field == DocumentBuilder.FIELD_NAME_GABOR ) {

		} else if( field == DocumentBuilder.FIELD_NAME_JCD ) {

		} else if( field == DocumentBuilder.FIELD_NAME_JPEGCOEFFS ) {
		}
		return null;
	}
	/**
	 * @param imagePath
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
	public List<SearchResult> searcherCEDDfromPath(String imagePath) throws CorruptIndexException, IOException {
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
	public List<SearchResult> searcherCEDDfromUrl(String URL) throws CorruptIndexException, IOException {
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
	public List<SearchResult> searcherCEDD(InputStream isImageQuery) throws CorruptIndexException, IOException {
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
