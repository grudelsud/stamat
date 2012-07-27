package models;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import stamat.model.SemanticKeyword;

public class Utils {

	public static JsonNode semanticKeywordList2JSON(List<? extends SemanticKeyword> semanticKeywordList)
	{
		ObjectNode dummyObject = Json.newObject();
		ArrayNode result = dummyObject.putArray("dummyKey");
		for(SemanticKeyword semanticKeyword : semanticKeywordList) {
			ObjectNode semKeywordNode = result.addObject();

			semKeywordNode.put("keyword", semanticKeyword.getKeyword());
			semKeywordNode.put("type", semanticKeyword.getType().toString());
			semKeywordNode.put("confidence", semanticKeyword.getConfidence());
			semKeywordNode.put("num_occurences", semanticKeyword.getNumOccurrences());
			semKeywordNode.put("tf", semanticKeyword.getTf());
		}
		return result;
	}

	public static ObjectNode returnSuccess(String message)
	{
		ObjectNode result = Json.newObject();
		result.put("success", message);
		return result;
	}

	public static ObjectNode returnSuccess(JsonNode data)
	{
		ObjectNode result = Json.newObject();
		result.put("success", data);
		return result;
	}

	public static ObjectNode returnError(String message)
	{
		ObjectNode result = Json.newObject();
		result.put("error", message);
		return result;
	}
}
