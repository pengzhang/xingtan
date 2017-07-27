import models.WechatKey;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {
	
	public void doJob(){
		initWechatKeys();
	}
	
	/**
	 * 初始化微信支付秘钥
	 */
	public static void initWechatKeys(){
		if(WechatKey.count() == 0){
			new WechatKey().save();
		}
	}
	
}