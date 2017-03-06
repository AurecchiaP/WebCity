package controllers;

import com.google.gson.Gson;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import play.twirl.api.Content;
import utils.BasicParser;
import utils.JavaPackage;
import utils.CubePacking;

public class HomeController extends Controller {

    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.getClasses()
                )
        ).as("text/javascript");
    }

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result getClasses() {

        JavaPackage pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/commons-math");

        Gson gson = new Gson();
        new CubePacking(pkg);
        String json = gson.toJson(pkg);



        return ok(json);
    }

    public Result visualization() {
        redirect("visualization");
        return ok();
    }
}
