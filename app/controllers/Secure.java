package controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.service.spi.ServiceException;

import annotation.hcommon.Check;
import annotation.hcommon.Login;
import models.AdminUser;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.Java;
import plugins.hcommon.router.Get;
import plugins.hcommon.router.Post;

public class Secure extends Controller {

    @Before()
    static void checkAccess() throws Throwable {
    	String[] unless = {"login", "authenticate", "logout"};
    	boolean isCheck = true;
    	
    	//检查标示Login注解的类
    	Class controller = Class.forName("controllers." + request.action.substring(0, request.action.lastIndexOf(".")));
    	if(controller.isAnnotationPresent(Login.class)){
    		unless = (String[]) ArrayUtils.addAll(unless, ((Login) controller.getAnnotation(Login.class)).unless());
    	}
		//是否在排除名单中
		if(Arrays.asList(unless).contains(request.actionMethod)) {
			isCheck = false;
		}
		
    	if(isCheck){
    		// Authent
    		if(!session.contains("username")) {
    			flash.put("url", "GET".equals(request.method) ? request.url : Play.ctxPath + "/"); // seems a good default
    			login();
    		}
    		// Checks
    		Check check = getActionAnnotation(Check.class);
    		if(check != null) {
    			check(check);
    		}
    		check = getControllerInheritedAnnotation(Check.class);
    		if(check != null) {
    			check(check);
    		}
    	}
    }

    private static void check(Check check) throws Throwable {
        boolean skip = false;
        for(String profile : check.value()) {
            boolean hasProfile = (Boolean)Security.invoke("check", profile);
            if (hasProfile) {
                skip = true;
                break;
            }
        }
        if (!skip) {
        	Security.invoke("onCheckFailed", "");
        }
    }

    // ~~~ Login
    @Get("/login")
    public static void login() throws Throwable {
        Http.Cookie remember = request.cookies.get("rememberme");
        if(remember != null) {
            int firstIndex = remember.value.indexOf("-");
            int lastIndex = remember.value.lastIndexOf("-");
            if (lastIndex > firstIndex) {
                String sign = remember.value.substring(0, firstIndex);
                String restOfCookie = remember.value.substring(firstIndex + 1);
                String username = remember.value.substring(firstIndex + 1, lastIndex);
                String time = remember.value.substring(lastIndex + 1);
                Date expirationDate = new Date(Long.parseLong(time)); // surround with try/catch?
                Date now = new Date();
                if (expirationDate == null || expirationDate.before(now)) {
                    logout();
                }
                if(Crypto.sign(restOfCookie).equals(sign)) {
                    session.put("username", username);
                    redirectToOriginalURL();
                }
            }
        }
        flash.keep("url");
        render();
    }
    
    @Post("/login")
    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        // Check tokens
        Boolean allowed = false;
        try {
            // This is the deprecated method name
            allowed = (Boolean)Security.invoke("authentify", username, password);
        } catch (UnsupportedOperationException e ) {
            // This is the official method name
            allowed = (Boolean)Security.invoke("authenticate", username, password);
        }
        if(validation.hasErrors() || !allowed) {
            flash.keep("url");
            flash.error("secure.error");
            params.flash();
            login();
        }
        // Mark user as connected
        session.put("username", username);
        // Remember if needed
        if(remember) {
            Date expiration = new Date();
            String duration = Play.configuration.getProperty("secure.rememberme.duration","30d"); 
            expiration.setTime(expiration.getTime() + ((long)Time.parseDuration(duration)) * 1000L );
            response.setCookie("rememberme", Crypto.sign(username + "-" + expiration.getTime()) + "-" + username + "-" + expiration.getTime(), duration);

        }
        // Redirect to the original URL (or /)
        redirectToOriginalURL();
    }
    
    @Get("/logout")
    public static void logout() throws Throwable {
        Security.invoke("onDisconnect");
        session.clear();
        response.removeCookie("rememberme");
        Security.invoke("onDisconnected");
        flash.success("secure.logout");
        login();
    }
    
    @Get("/register")
	public static void register(){
		render();
	}
	
	@Post("/register")
	public static void registerAction(String username, String password){
		//密码加密
		password = Crypto.passwordHash(password);
		new AdminUser(username, password).save();
		String login_url = Play.configuration.getProperty("login.url");
		if(StringUtils.isEmpty(login_url)){
			login_url = "/login";
		}
		redirect(login_url);
		
	}

    // ~~~ Utils

    static void redirectToOriginalURL() throws Throwable {
        Security.invoke("onAuthenticated");
        String url = flash.get("url");
        if(url == null) {
            url = Play.ctxPath + "/admin";
        }
        redirect(url);
    }

    public static class Security extends Controller {

        /**
         * @Deprecated
         * 
         * @param username
         * @param password
         * @return
         */
        static boolean authentify(String username, String password) {
            throw new UnsupportedOperationException();
        }

        /**
         * This method is called during the authentication process. This is where you check if
         * the user is allowed to log in into the system. This is the actual authentication process
         * against a third party system (most of the time a DB).
         *
         * @param username
         * @param password
         * @return true if the authentication process succeeded
         */
        static boolean authenticate(String username, String password) {
        	try {
        		AdminUser user = AdminUser.find("username=? and password=? and status=?", username, Crypto.passwordHash(password), false).first();
    			if (user != null) {
    				session.put("user_id", user.id);
    				session.put("avatar", user.avatar);
    				return true;
    			}
    			return false;
    		} catch (ServiceException e) {
    			Logger.info("exception message : %s", e.getMessage());
    			return false;
    		}
        }

        /**
         * This method checks that a profile is allowed to view this page/method. This method is called prior
         * to the method's controller annotated with the @Check method. 
         *
         * @param profile
         * @return true if you are allowed to execute this controller method.
         */
        static boolean check(String profile) {
        	boolean flag = true;
    		try{
    			//通过role_id获取缓存中的权限列表
    			String rids = session.get("rids");
    			for(String rid : rids.split(",")){
    				List permissions = (List) Cache.get("rid:"+rid);
    				//权限列表为空,认为没有任何权限
    				if(permissions != null && permissions.size()>0){
    					for(Object p : permissions){
    						Field action = p.getClass().getField("action");
    						Field action_url = p.getClass().getField("action_url");
    						//只有action和action_url全部匹配时通过
    						if(request.actionMethod.equals((String)action.get(p)) && request.path.equals((String)action_url.get(p))){
    							flag = false;
    							break;
    						}
    					}
    				}
    			}
    			if(flag){
    				return false;
    			}
    		}catch(Exception e){
    			return false;
    		}
    		return true;
        }

        /**
         * This method returns the current connected username
         * @return
         */
        static String connected() {
            return session.get("username");
        }

        /**
         * Indicate if a user is currently connected
         * @return  true if the user is connected
         */
        static boolean isConnected() {
            return session.contains("username");
        }

        /**
         * This method is called after a successful authentication.
         * You need to override this method if you with to perform specific actions (eg. Record the time the user signed in)
         */
        static void onAuthenticated() {
        }

         /**
         * This method is called before a user tries to sign off.
         * You need to override this method if you wish to perform specific actions (eg. Record the name of the user who signed off)
         */
        static void onDisconnect() {
        }

         /**
         * This method is called after a successful sign off.
         * You need to override this method if you wish to perform specific actions (eg. Record the time the user signed off)
         */
        static void onDisconnected() {
        }

        /**
         * This method is called if a check does not succeed. By default it shows the not allowed page (the controller forbidden method).
         * @param profile
         */
        static void onCheckFailed(String profile) {
            forbidden();
        }

        private static Object invoke(String m, Object... args) throws Throwable {

            try {
                return Java.invokeChildOrStatic(Security.class, m, args);       
            } catch(InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

}
