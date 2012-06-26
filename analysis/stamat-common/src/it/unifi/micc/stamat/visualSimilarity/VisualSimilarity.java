package it.unifi.micc.stamat.visualSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * class VisualSimilarity
 * 
 * performs analysis on images and retrieves a list of similar images
 * 
 * commento beppe: righe di codice da riutilizzare
 * List<SearchResult> currentResultSCD = searchSCD.search(new File("ucid.v2-png/" + query));
 * List<SearchResult> currentResultCLD = searchCLD.search(new File("ucid.v2-png/" + query));
 * List<SearchResult> currentResultEHD = searchEHD.search(new File("ucid.v2-png/" + query));
 * 
 * RankFusion rankFusion = new RankFusion(currentResultSCD, currentResultCLD, currentResultEHD);
 * List<SearchResult> mergedWithBorda = rankFusion.mergeWithBORDACount();
 * List<SearchResult> mergedWithRankProduct = rankFusion.mergeWithRankProduct();
 * List<SearchResult> mergedWithInvertedRankPosition = rankFusion.mergeWithInvertedRankPosition();
 *
 */
public class VisualSimilarity {

	/**
	 * @param indexPath
	 * @param imageFolderPath
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public static void createIndex(String indexPath, String imageFolderPath) throws IOException {
		Indexing indexing = new Indexing(indexPath);
		indexing.createIndex(imageFolderPath);
	}

	public static void updateIndex(String indexPath, String imagePath) throws IOException {
		Indexing indexing = new Indexing(indexPath);
		indexing.updateIndex(imagePath);
	}
	/**
	 * @param imagePath
	 * @param numberOfResults
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public static List<SearchResult> queryImage(String imagePath, String indexPath, int numberOfResults) throws IOException {
		return VisualSimilarity.getQueryImageResults(new File(imagePath), indexPath, 1f, 1f, 1f, numberOfResults);
	}

	/**
	 * @param imagePath
	 * @param numberOfResults
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public static JSONObject queryImageJSON(String imagePath, String indexPath, int numberOfResults) throws IOException {
		return VisualSimilarity.outputJSON(VisualSimilarity.queryImage(imagePath, indexPath, numberOfResults));		
	}

	/**
	 * @param queryImage
	 * @param weightSCD
	 * @param weightCLD
	 * @param weightEHD
	 * @param numberOfResults
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private static List<SearchResult> getQueryImageResults(File queryImage,String indexPath, float weightSCD, float weightCLD, float weightEHD, int numberOfResults) throws CorruptIndexException, IOException {
		Search search = new Search(indexPath, weightSCD, weightCLD, weightEHD, numberOfResults+1);
		List<SearchResult> currentResult = search.search(queryImage);
		return currentResult;
	}

	/**
	 * @param result
	 * @return
	 */
	private static JSONObject outputJSON(List<SearchResult> result) {
		JSONObject jsonDoc = new JSONObject();
		try {
			int size = result != null ? result.size() : 0;
			JSONArray elementsArray = new JSONArray();
			if (size > 0) {
				for (SearchResult element : result){
					elementsArray.put(element.toJSONItem());
					jsonDoc.put("results", elementsArray);
				}

			} else {
				jsonDoc.put("result", elementsArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonDoc;
	}
}
