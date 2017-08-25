package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import annotation.hcommon.Login;
import exceptions.ControllerException;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http;

public class AdminActionIntercepter extends Controller {

	/*
	 * Custom Configuration
	  #Check User Login
	  check.login=enabled
	  login.url=http://user.hm55.cn/login
	  #Check User Permission  
	  check.permission=enabled
	 */

	@Before()
	private static void actionBeforeProcess() {
		//TODO 设置默认权限
		checkLogin();
	}

	@After
	private static void actionAfterProcess() {
	}
	
	@Catch(value = ControllerException.class, priority = 2)
	private static void actionControllerExceptionProcess(ControllerException ce) {
		ce.printStackTrace();
		Logger.error("controller exception %s", ce.getMessage());
		error(ce.getMessage());
	}

	@Catch(value = Throwable.class, priority = 1)
	private static void actionExceptionProcess(Throwable throwable) {
		throwable.printStackTrace();
		Logger.error("exception %s", throwable.getMessage());
		error(throwable.getMessage());
	}
	
	@Finally
    static void log() {
        //Logger.info("Response contains : " + response.out);
    }
	
	private static void checkLogin(){
		String login = Play.configuration.getProperty("check.login", "disabled");
		if(login.equals("enabled") && (session.get("username") == null)){
			String login_url = Play.configuration.getProperty("login.url");
			if(StringUtils.isEmpty(login_url)){
				login_url = "/login";
			}
			try {
				Class controller = Class.forName("controllers." + request.action.substring(0, request.action.lastIndexOf(".")));
				if (controller.isAnnotationPresent(Login.class)) {
					boolean flag = true;
					String[] except = ((Login) controller.getAnnotation(Login.class)).unless();//排除的Action

					for(String action : except){
						if(request.actionMethod.equals(action)){
							flag = false;
						}
					}

					if(flag && StringUtils.isEmpty(session.get("uid"))){
						redirect(login_url);
					}
				}
			} catch (ClassNotFoundException e) {
				notFound();
			}

		}
	}
	
	

}
