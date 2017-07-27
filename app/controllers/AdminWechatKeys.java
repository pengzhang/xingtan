package controllers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import annotation.hcommon.For;
import annotation.hcommon.Login;
import controllers.CRUD.ObjectType;
import models.WechatKey;
import play.cache.Cache;
import play.data.binding.Binder;
import play.data.validation.Password;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.With;

@Login
@For(WechatKey.class)
@With(ActionIntercepter.class)
public class AdminWechatKeys extends CRUD {
	
	@Before
	static void menus(){
		renderArgs.put("nav", "admin");
		renderArgs.put("menu", "AdminWechatKeys");
	}
	
	
	public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        
        //更新微信缓存
        Cache.delete("wechat_pay_key");
    	Cache.set("wechat_pay_key", object);
    	
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
	
	public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        //更新微信缓存
        Cache.delete("wechat_pay_key");
    	Cache.set("wechat_pay_key", object);
    	
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
	
	
	
}
