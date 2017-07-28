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
	
	@Get("/my_anchor")
	public static void my_anchor(Integer page, Integer size, Long b_id){
		boolean anchor = true;
		page = page==null?0:page;
		size = size==null?10:size;
		b_id = b_id==null?0:b_id;
		List<Anchor> anchors = Anchor.find("b_id=? order by createDate desc",b_id).fetch(page, size);
		render(anchor,anchors);
	}
	
	@Get("/my_income")
	public static void my_income(){
		boolean anchor = false;
		render(anchor);
	}
	
	@Get("/anchor/all")
    public static void anchorAll() {
		List<Anchor> anchors = Anchor.findAll();
        renderJSON(anchors);
    }

}