package controllers;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import models.Constants;
import models.Utils;
import models.requests.EntitiesExtract;

import org.codehaus.jackson.JsonNode;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import stamat.main.Analyser;
import stamat.model.NamedEntity;
import views.html.index;

public class Application extends Controller {

	public static Result index()
	{
		return ok(index.render("STAMAT - API Backend"));
	}

	public static Result entitiesExtract()
	{
		Form<EntitiesExtract> form = form(EntitiesExtract.class).bindFromRequest();
		EntitiesExtract entityExtractRequest = form.get();

		String classifierPath = Constants.getThreeClassifierPath();
		ArrayList<NamedEntity> namedEntityList = Analyser.ned.extractStanford(entityExtractRequest.text, classifierPath);
		JsonNode result = Utils.semanticKeywordList2JSON(namedEntityList);
		return ok(Utils.returnSuccess(result));
	}
	
	public static class visual
	{
		public static Result index()
		{
			return TODO;
		}		

		public static Result newIndex()
		{
			DynamicForm form = form().bindFromRequest();
			return TODO;
		}		

		public static Result indexImages()
		{
			return TODO;
		}

		public static Result similarity()
		{
			return TODO;
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