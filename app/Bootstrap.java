import models.AdminUser;
import models.WechatKey;
import play.cache.Cache;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.Crypto;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob(){
		initWechatKeys();
		initAdmin();
	}

	/**
	 * 初始化微信支付秘钥
	 */
	public static void initWechatKeys(){
		if(WechatKey.count() == 0){
			new WechatKey().save();
		}else{
			WechatKey key = WechatKey.findById(1L);
			if(key != null){
				Cache.set("wechat_pay_key", key);
			}
		}
	}

	/**
	 * 初始化超级管理员	
	 */
	public static void initAdmin(){
		AdminUser admin = AdminUser.find("username", "admin").first();
		if(admin == null){
			new AdminUser("admin", Crypto.passwordHash("admin")).save();
		}
	}

}