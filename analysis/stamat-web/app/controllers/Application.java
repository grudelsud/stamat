package controllers;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Constants;
import models.Utils;
import models.dbo.Feeditemmedia;
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
import stamat.controller.visual.SearchResult;
import stamat.main.Analyser;
import stamat.model.NamedEntity;
import stamat.util.StamatException;
import views.html.index;

public class Application extends Controller {

	public static Result index()
	{
		return ok(index.render("STAMAT - API Backend"));
	}

	public static Result entitiesExtractGATE()
	{
		Form<EntitiesExtract> form = form(EntitiesExtract.class).bindFromRequest();
		if( form.hasErrors() ) {
			return badRequest(Utils.returnError("invalid request"));
		}
		EntitiesExtract entityExtractRequest = form.get();

		final String text = entityExtractRequest.text;
		final String gateHomePath = play.Play.application().configuration().getString("stamat.gatehome");

		// right the slow code asynchronously
		Promise<ArrayList<NamedEntity>> promiseOfNamedentitylist = Akka.future(
			new Callable<ArrayList<NamedEntity>>() {
				public ArrayList<NamedEntity> call() {
					ArrayList<NamedEntity> list = Analyser.ned.exractAnnie(text, gateHomePath);

					// tracing memory right before returning the result
					String runtime = Application.memInfo() + "ned.GATE " + list.size();
					Logger.info(runtime);
					return list;
				}
			}
		);

		return async(
			promiseOfNamedentitylist.map(
				new Function<ArrayList<NamedEntity>, Result>() {
					public Result apply(ArrayList<NamedEntity> result) {
						JsonNode resultJson = Utils.semanticKeywordList2JSON(result);
						return ok(Utils.returnSuccess(resultJson));
					}
				}
			)
		);		
	}

	public static Result entitiesExtractSNER()
	{
		Form<EntitiesExtract> form = form(EntitiesExtract.class).bindFromRequest();
		
		if( form.hasErrors() ) {
			return badRequest(Utils.returnError("invalid request"));
		}
		EntitiesExtract entityExtractRequest = form.get();

		String classifierPath = Constants.FOLDER_3CLASS;
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
		String indexPath = Constants.FOLDER_INDICES + "/" + name;
		int returnCode = Analyser.visual.createEmptyIndex(indexPath, returnMsg);
		if( returnCode == Analyser.constants.SUCCESS ) {
			return ok(Utils.returnSuccess(returnMsg.toString()));			
		} else {
			return badRequest(Utils.returnError(returnMsg.toString()));
		}
	}		

	public static Result visualIndexImagesFromDB()
	{
		// read json post params
		JsonNode json = request().body().asJson();
		final String indexPath;
		final int num;
		if(json == null) {
			return badRequest(Utils.returnError("expecting JSON request. please check that content-type is set to \"application/json\" and request body is properly encoded (e.g. JSON.stringify(data))"));
		} else {
			try {
				num = json.get(Constants.json_fields.QUERY_FIELD_NUMOFRESULT).getIntValue();
				indexPath = Constants.FOLDER_INDICES + "/" + json.get(Constants.json_fields.INDEX_FIELD_INDEX).getTextValue();
			} catch(NullPointerException e) {
				return badRequest(Utils.returnError("Missing field '"+Constants.json_fields.QUERY_FIELD_NUMOFRESULT+"' or '"+Constants.json_fields.INDEX_FIELD_INDEX+"'"));						
			}			
		}

		// create the promise
		Promise<List<Feeditemmedia>> promiseOfFeeditemmediaList = Akka.future(
			new Callable<List<Feeditemmedia>>() 
			{
				public List<Feeditemmedia> call() 
				{
					// fetch 10 downloaded items (not already queued for indexing)
					List<Feeditemmedia> list = Feeditemmedia.find
							.where().eq("flags", Constants.db_fields.MEDIA_DOWNLOADED)
							.order().desc("id")
							.setMaxRows(num)
							.findList();

					// mark them as queued
					for(Feeditemmedia item : list) {
						item.flags = Constants.db_fields.MEDIA_DOWNLOADED | Constants.db_fields.MEDIA_QUEUEDFORINDEXING;
						item.update();
					}

					Logger.info("indexing " + num + " images");
					// start the indexing process
					for(Feeditemmedia item : list) {
						String id = Long.toString(item.id);
						String url = item.abs_path + item.hash;

						HashMap<String, String> indexedFields = new HashMap<String, String>();
						indexedFields.put(Analyser.constants.SEARCH_URL, url);
						indexedFields.put(DocumentBuilder.FIELD_NAME_IDENTIFIER, id);

						try {
							// if everything all right, mark them as indexed
							Analyser.visual.updateIndexfromURL(indexPath, url, indexedFields);
							item.flags = item.flags | Constants.db_fields.MEDIA_INDEXED;

							// tracing memory right before the next cycle
							String runtime = Application.memInfo() + "visual.indexDB "+id+" -> " + indexPath;
							Logger.info(runtime);
						} catch (StamatException e) {
							// if exception caught, mark them as unresolved
							item.flags = item.flags | Constants.db_fields.MEDIA_INDEXINGEXCEPTION;
							Logger.error("visualIndexImagesFromDB - error while indexing {id="+id+", url="+url+"} message: " + e.getMessage());
						}
						// save db item
						item.update();
					}
					return list;
				}
			}
		);
		
		// asynchronously return results
		return async(
			promiseOfFeeditemmediaList.map(
				new Function<List<Feeditemmedia>, Result>() 
				{
					public Result apply(List<Feeditemmedia> list) 
					{
						return ok(Utils.returnSuccess(Utils.feeditemmediaList2JSON(list)));
					}
				}
			)
		);
	}

	public static Result visualIndexImagesFromJSON()
	{
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Utils.returnError("expecting JSON request. please check that content-type is set to \"application/json\" and request body is properly encoded (e.g. JSON.stringify(data))"));
		} else {
			String indexPath = "";
			try {
				indexPath = Constants.FOLDER_INDICES + "/" + json.get(Constants.json_fields.INDEX_FIELD_INDEX).getTextValue();				
			} catch(NullPointerException e) {
				return badRequest(Utils.returnError("Missing field '"+Constants.json_fields.INDEX_FIELD_INDEX+"'"));
			}
			JsonNode imageListJson = json.findValue(Constants.json_fields.INDEX_FIELD_IMAGES);
			if( imageListJson != null ) {
				Iterator<JsonNode> imageListJsonIterator = imageListJson.getElements();
				String message = "indices added to " + indexPath + ": ";
				while(imageListJsonIterator.hasNext()) {
					JsonNode imageJson = imageListJsonIterator.next();

					String id = "", url = "";
					try {
						// field id might be an integer, using "asText" to stay on a safe side
						id = imageJson.get(Constants.json_fields.INDEX_FIELD_IMAGE_ID).asText();
						url = imageJson.get(Constants.json_fields.INDEX_FIELD_IMAGE_URL).getTextValue();
					} catch(NullPointerException e) {
						return badRequest(Utils.returnError("Missing field '"+Constants.json_fields.INDEX_FIELD_IMAGE_ID+"' or '"+Constants.json_fields.INDEX_FIELD_IMAGE_URL+"'"));						
					}
					HashMap<String, String> indexedFields = new HashMap<String, String>();
					indexedFields.put(Analyser.constants.SEARCH_URL, url);
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
				return badRequest(Utils.returnError("expecting json format: {index: bla, images: [{}, {}, ...]}"));
			}
		}
	}

	public static Result visualSimilarity()
	{
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Utils.returnError("expecting JSON request. please check that content-type is set to 'application/json' and request body is properly encoded (e.g. JSON.stringify(data))"));
		}

		// request sanity checks first
		final String index, source, fileIdentifier, feature;
		final int numberOfResults;
		try {
			index = Constants.FOLDER_INDICES + "/" + json.get(Constants.json_fields.INDEX_FIELD_INDEX).getTextValue();
			source = json.get(Constants.json_fields.QUERY_FIELD_SOURCE).getTextValue();
			fileIdentifier = json.get(Constants.json_fields.QUERY_FIELD_FILEID).getTextValue();
			feature = json.get(Constants.json_fields.QUERY_FIELD_FEATURE).getTextValue();
			numberOfResults = json.get(Constants.json_fields.QUERY_FIELD_NUMOFRESULT).getIntValue();
		} catch( NullPointerException e) {
			String message = "Request, missing one of the fields: " +
				Constants.json_fields.INDEX_FIELD_INDEX + " " +
				Constants.json_fields.QUERY_FIELD_SOURCE + " " +
				Constants.json_fields.QUERY_FIELD_FILEID + " " +
				Constants.json_fields.QUERY_FIELD_FEATURE + " " +
				Constants.json_fields.QUERY_FIELD_NUMOFRESULT	+ " ";
			return badRequest(Utils.returnError(message));
		}

		if( !(source.equals(Analyser.constants.SEARCH_URL) || source.equals(Analyser.constants.SEARCH_INDEX)) ) {
			return badRequest("source must be either '"+Analyser.constants.SEARCH_URL+"' or '"+Analyser.constants.SEARCH_INDEX+"'");
		}

		String[] featureArray = {
			DocumentBuilder.FIELD_NAME_AUTOCOLORCORRELOGRAM,
			DocumentBuilder.FIELD_NAME_SCALABLECOLOR,
			DocumentBuilder.FIELD_NAME_CEDD,
			DocumentBuilder.FIELD_NAME_COLORHISTOGRAM,
			DocumentBuilder.FIELD_NAME_COLORLAYOUT,
			DocumentBuilder.FIELD_NAME_TAMURA,
			DocumentBuilder.FIELD_NAME_EDGEHISTOGRAM,
			DocumentBuilder.FIELD_NAME_FCTH,
			DocumentBuilder.FIELD_NAME_GABOR,
			DocumentBuilder.FIELD_NAME_JCD,
			DocumentBuilder.FIELD_NAME_JPEGCOEFFS,
			DocumentBuilder.FIELD_NAME_SIFT,
			DocumentBuilder.FIELD_NAME_SURF
		};

		if( !Arrays.asList(featureArray).contains(feature) ) {
			Logger.info("visualSimilarity - wrong feature descriptor");
			return badRequest(Utils.returnError("feature must be one of: " + Arrays.asList(featureArray).toString()));
		}

		// now run the search algorithm asynchronously
		Promise<List<SearchResult>> promiseOfSearchresultlist = Akka.future(
			new Callable<List<SearchResult>>() {
				public List<SearchResult> call() {
					List<SearchResult> result = null;
					if( source.equals(Analyser.constants.SEARCH_URL) ) {
						result = Analyser.visual.searchFromUrl(index, fileIdentifier, feature, numberOfResults);
					} else if( source.equals(Analyser.constants.SEARCH_INDEX) ) {
						result = Analyser.visual.searchFromIndex(index, fileIdentifier, feature, numberOfResults);
					}
					// tracing memory right before returning the result
					String runtime = Application.memInfo() + "visual.Sim " + fileIdentifier + " " + feature;
					Logger.info(runtime);
					return result;
				}
			}
		);
		return async(
			promiseOfSearchresultlist.map(
				new Function<List<SearchResult>, Result>() {
					public Result apply(List<SearchResult> result) {
						return ok(Utils.returnSuccess(Utils.searchResultList2JSON(result)));
					}
				}
			)
		);
	}

	/**
	 * useless function, used to copy and paste a template of async processing.
	 * presently used for entitiesExtractGate, visualIndexImagesFromDB, visualSimilary
	 * all other functions in this class are run synchronously
	 * 
	 * @return
	 */
	public static Result asyncCanvas()
	{
		Promise<String> promiseOfString = Akka.future(
			new Callable<String>() {
				public String call() {
					return "cheshire cat hides under deep folds of cumbersome code";
				}
			}
		);
		return async(
			promiseOfString.map(
				new Function<String, Result>() {
					public Result apply(String s) {
						return ok("all good, received: " + s);
					}
				}
			)
		);
	}
	
	private static String memInfo() {
		long tot = Runtime.getRuntime().totalMemory() / 1000000;
		long free = Runtime.getRuntime().freeMemory() / 1000000;
		long used = tot - free;

		long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
		int upSeconds = (int) (uptime / 1000) % 60 ;
		int upMinutes = (int) ((uptime / (1000*60)) % 60);
		int upHours   = (int) ((uptime / (1000*60*60)) % 24);
		
		return "[up "+upHours+"h:"+upMinutes+"m:"+upSeconds+"s - "+Runtime.getRuntime().availableProcessors() + "CPU - MEM Tot:"+tot+"M Used:"+used+"M Free:"+free+"M]\t";
	}
}