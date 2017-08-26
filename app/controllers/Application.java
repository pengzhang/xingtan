package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import models.Anchor;
import models.Broker;
import models.LangMa;
import models.Order;
import models.OrderItem;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.With;
import plugins.hcommon.router.Get;
import utils.Json;

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
		List<Anchor> anchors = Anchor.find("xingcard=? order by createDate desc", true).fetch();
        render(anchors);
	}
	
	@Get("/xingcard/buy")
	public static void buy_xingcard(long aid, int cardnum){
		Logger.info("aid:%s, cardnum:%s", aid, cardnum);
		//下订单
		long uid = Long.parseLong(session.get("uid"));
		String openid = session.get("openid");
		Order order = new Order(new Date(), openid);
		order.user_id = uid;
		order.save();
		
		Anchor anchor = Anchor.findById(aid);
		Logger.info("anchor json: %s", Json.toJson(anchor));
		
		OrderItem item = new OrderItem();
		item.order_id = order.id;
		item.quantity = cardnum;
		item.product_id = aid;
		item.product_name = anchor.nickname;
		item.product_image = anchor.photo;
		item.product_total = cardnum * 20;
		item.save();
		renderJSON("{\"status\":true}");
//		redirect("/my/xingcard");
	}
	
	@Get("/my/xingcard")
	public static void my_xingcard(){
		long uid = Long.parseLong(session.get("uid"));
		List<OrderItem> items = JPA.em().createNativeQuery("select * from order_item item left join order_pay pay on pay.id = item.order_id where pay.user_id=:uid", OrderItem.class).setParameter("uid", uid).getResultList();
        render(items);
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