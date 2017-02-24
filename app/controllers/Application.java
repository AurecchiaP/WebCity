package controllers;

import play.mvc.*;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
//import com.github.javaparser.*;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Application extends Controller {

    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.testo()
                )
        ).as("text/javascript");
    }

}
