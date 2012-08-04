package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Constants;
import models.Utils;
import models.requests.EntitiesExtract;
import net.semanticmetadata.lire.DocumentBuilder;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import stamat.main.Analyser;
import stamat.model.NamedEntity;
import stamat.util.StamatException;
import views.html.index;

public class Application extends Controller {

	public static Result index()
	{
		return ok(index.render("STAMAT - API Backend"));
	}

	public static Result entitiesExtract()
	{
		Form<EntitiesExtract> form = form(EntitiesExtract.class).bindFromRequest();
		
		if( form.hasErrors() ) {
			return badRequest(Utils.returnError("invalid request"));
		}
		EntitiesExtract entityExtractRequest = form.get();

		String classifierPath = Constants.getThreeClassifierPath();
		ArrayList<NamedEntity> namedEntityList = Analyser.ned.extractStanford(entityExtractRequest.text, classifierPath);
		JsonNode result = Utils.semanticKeywordList2JSON(namedEntityList);
		return ok(Utils.returnSuccess(result));
	}
	
	public static Result visualIndex()
	{
		return TODO;
	}		

	public static Result visualNewIndex()
	{
		DynamicForm form = form().bindFromRequest();
		String name = form.get("name");
		Pattern p = Pattern.compile("^\\w{1,50}$");
		Matcher m = p.matcher(name);
		if( !m.matches() ) {
			return badRequest(Utils.returnError("invalid request, name must be between 1 and 50 alphanumerical characters"));				
		}
		StringBuilder returnMsg = new StringBuilder();
		String indexPath = Constants.getIndicesFolderPath() + "/" + name;
		int returnCode = Analyser.visual.createEmptyIndex(indexPath, returnMsg);
		if( returnCode == Analyser.constants.SUCCESS ) {
			return ok(Utils.returnSuccess(returnMsg.toString()));			
		} else {
			return badRequest(Utils.returnError(returnMsg.toString()));
		}
	}		

	public static Result visualIndexImages()
	{
		JsonNode json = request().body().asJson();
//		Logger.info("visualIndexImages request - " + request().body().toString());
		if(json == null) {
			return badRequest(Utils.returnError("expecting JSON request. please check that content-type is set to \"application/json\" and request body is properly encoded (e.g. JSON.stringify(data))"));
		} else {
			String indexPath = Constants.getIndicesFolderPath() + "/test";
			JsonNode imageListJson = json.findValue("success");
			if( imageListJson != null ) {
				Iterator<JsonNode> imageListJsonIterator = imageListJson.getElements();
				String message = "indices added to " + indexPath + ": ";
				while(imageListJsonIterator.hasNext()) {
					JsonNode imageJson = imageListJsonIterator.next();
					String url = imageJson.get("url_cdn").asText();
					String id = imageJson.get("id").asText();
					HashMap<String, String> indexedFields = new HashMap<String, String>();
					indexedFields.put("url", url);
					indexedFields.put(DocumentBuilder.FIELD_NAME_IDENTIFIER, id);
					message += indexedFields.toString() + " ";
					Logger.info("indexing " + indexedFields.toString());
					try {
						Analyser.visual.updateIndexfromURL(indexPath, url, indexedFields);
					} catch (StamatException e) {
						Logger.error("error while indexing {id="+id+", url="+url+"} message: " + e.getMessage());
					}
				}
				return ok(Utils.returnSuccess(message));				
			} else {
				return badRequest(Utils.returnError("expecting json format: {success: [{}, {}, ...]}"));
			}
		}
	}

	public static Result visualSimilarity()
	{
		JsonNode json = request().body().asJson();
		Logger.info("visualSimilarity request - " + request().body().toString());
		if(json == null) {
			return badRequest(Utils.returnError("expecting JSON request. please check that content-type is set to \"application/json\" and request body is properly encoded (e.g. JSON.stringify(data))"));
		} else {
			return ok(Utils.returnSuccess("all good"));							
		}
	}

	public static Result asyncTest()
	{
		Promise<String> promiseOfInteger = Akka.future(
			new Callable<String>() {
				public String call() {
					String classifierPath = Constants.getThreeClassifierPath();
					return "cheshire cat hides under deep folds of cumbersome code <"+ classifierPath +">";
				}
			}
		);
		return async(
			promiseOfInteger.map(
				new Function<String, Result>() {
					public Result apply(String s) {
						return ok("all good, received: " + s);
					}
				}
			)
		);
	}
}