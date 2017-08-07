package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import models.Broker;
import models.WechatKey;
import play.Logger;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Finally;
import utils.HConstant;

public class ActionIntercepter extends Controller {

	/*
	 * Custom Configuration #Check User Login check.login=enabled
	 * login.url=http://user.hm55.cn/login #Check User Permission
	 * check.permission=enabled
	 */

	@Before()
	private static void actionBeforeProcess() {
		WechatKey wxkey = (WechatKey) Cache.get("wechat_pay_key");
		// 如果浏览器是微信进行授权操作
		String userAgentStr = StringUtils.defaultIfBlank(request.headers.get("user-agent").value(),
				HConstant.DEFAULTUSERAGENT);
		if (userAgentStr.contains("MicroMessenger")) {
			String returnUrl = request.getBase() + "/wechat/openid";
			Logger.info("get wechat opendid, request url:%s", returnUrl);

			// 记录用户请求的URL
			String state = RandomStringUtils.randomAlphanumeric(6);
			Cache.set(state, request.url, "30s");

			try {
				returnUrl = URLEncoder.encode(returnUrl, "utf-8");
			} catch (UnsupportedEncodingException e) {
				Logger.info("wechat auth redirect url encode error,%s", e.getMessage());
			}

			// 如果openid为空,要求用户授权
			if (StringUtils.isEmpty(session.get("openid"))) {
				Logger.info("wechat auth redirect url,%s", returnUrl);
				WechatAuthController.snsapi_userinfo(wxkey.wxpay_appid, returnUrl, state);
			} else {
				String openid = session.get("openid");
				Broker user = Broker.find("openid=?", openid).first();
				if (user == null) {
					WechatAuthController.snsapi_userinfo(wxkey.wxpay_appid, returnUrl, state);
				}

			}
		} 
//		else {
//			renderText("请用微信访问");
//		}
	}

	@After
	private static void actionAfterProcess() {
	}

	@Catch(value = Throwable.class, priority = 1)
	private static void actionExceptionProcess(Throwable throwable) {
		throwable.printStackTrace();
		Logger.error("exception %s", throwable.getMessage());
		error(throwable.getMessage());
	}

	@Finally
	static void log() {
		// Logger.info("Response contains : " + response.out);
	}

}
