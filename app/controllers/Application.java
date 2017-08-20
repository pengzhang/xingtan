package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import models.Anchor;
import models.Broker;
import models.LangMa;
import play.Logger;
import play.libs.Codec;
import play.libs.WS;
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
	
	@Get("/xingcard")
	public static void xingcard(){
		List<Anchor> anchors = Anchor.find("order by createDate desc").fetch();
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
	
	@Get("/langma")
	public static void langma(){
		String redirect_url = "http://xt.hm55.cn/langma/user";
		String appid="jmCguJZedFLjPahF";
		try {
			redirect_url = URLEncoder.encode(redirect_url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://link.pangaobox.com/oauth?redirect_uri="+redirect_url+"&appid="+appid;
		Logger.info("langma url:%s", url);
		redirect(url);
	}
	
	@Get("/langma/user")
	public static void langma_user(String code){
		String appid="jmCguJZedFLjPahF";
		String appsercet="ktxzPfifzHZS104ZM36fbUKdiCX09LXW";
		String time = System.currentTimeMillis()+"";
		String sign = Codec.hexMD5(appid+code+time+appsercet);
		String url= "http://link.pangaobox.com/oauth/info?appid="+appid+"&code="+code+"&time="+time+"&sign="+sign;
		Logger.info("langma user url:%s", url);
		JsonElement json = WS.url(url).get().getJson();
		Logger.info("langma return json:%s", json.toString());
		LangMa langma = new Gson().fromJson(json.getAsJsonObject().get("data"), LangMa.class);
		String uid = session.get("uid");
		Logger.info("langma user session uid:%s", uid);
		Broker.langma(uid, langma);
        redirect("/my_assist");
	}
	
	@Get("/my_assist")
    public static void my_assist() {
		String uid = session.get("uid");
		String langMaUserId = Broker.getLangMaUser(uid);
		if(StringUtils.isNotEmpty(langMaUserId)){
			List<Broker> brokers = Broker.find("recommendId=?", langMaUserId).fetch();
			boolean assist = false;
			render(assist,brokers);
		}else{
			redirect("/langma");
		}
    }

}