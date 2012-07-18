package controllers;

import org.codehaus.jackson.node.ObjectNode;

import play.*;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	public static Result index()
	{
		ObjectNode result = Json.newObject();
		result.put("success", "all good");
		return ok(result);
	}

	public static Result entitiesExtract()
	{
		
	}
	
	public static Result visualSimilarity()
	{
		
	}
}