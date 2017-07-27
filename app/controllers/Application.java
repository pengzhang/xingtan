package controllers;

import java.util.List;

import models.Anchor;
import play.mvc.Controller;
import play.mvc.With;
import plugins.hcommon.router.Get;

@With(ActionIntercepter.class)
public class Application extends Controller {

	@Get("/anchor")
    public static void index(Integer page, Integer size) {
		page = page==null?0:page;
		size = size==null?10:size;
		List<Anchor> anchors = Anchor.find("order by createDate desc").fetch(page, size);
        render(anchors);
    }

}