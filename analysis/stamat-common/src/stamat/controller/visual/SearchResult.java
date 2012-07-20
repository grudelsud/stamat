package stamat.controller.visual;


import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult implements Comparable<SearchResult>{
	private String query;
	private String result;
	private int position;
	private double similarity;
	
	public SearchResult(String query, String result, int position, double similarity) {
		super();
		this.query = query;
		this.result = result;
		this.position = position;
		this.similarity = similarity;
	}
	
	public JSONObject toJSONItem(){
			
		JSONObject oJson = new JSONObject();
		try {
			oJson.put("imageResult", result);
			oJson.put("position", position);
			oJson.put("similarity", similarity);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return oJson;
	}

	public String getQuery() {
		return query;
	}

	public String getResult() {
		return result;
	}

	public int getPosition() {
		return position;
	}

	public double getSimilarity() {
		return similarity;
	}

	@Override
	public int compareTo(SearchResult sr) {
		if(this.similarity < sr.getSimilarity()){
			return 1;
		}else if(this.similarity > sr.getSimilarity()){
			return -1;
		}else{
			return 0;
		}
	}
	
	@Override
	public String toString(){
		return this.result + " " + this.similarity;
	}
}
