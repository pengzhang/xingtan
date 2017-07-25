package controllers;

import play.Logger;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Finally;

public class ActionIntercepter extends Controller {

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
        //Logger.info("Response contains : " + response.out);
    }
	
	

}
