package controllers;

import annotation.hcommon.For;
import annotation.hcommon.Login;
import models.Anchor;
import play.mvc.Before;
import play.mvc.With;

@Login
@For(Anchor.class)
@With(ActionIntercepter.class)
public class AdminBrokers extends CRUD {

	@Before
	static void menus(){
		renderArgs.put("nav", "user");
		renderArgs.put("menu", "Brokers");
		request.args.put("where", request.params.get("filter"));
	}
}
