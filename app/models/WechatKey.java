package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import annotation.hcommon.Hidden;

@Entity
@Table(name="wechat_key")
@org.hibernate.annotations.Table(comment="微信秘钥信息管理", appliesTo = "wechat_key")
public class WechatKey extends BaseModel{

	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_appid;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_mchid;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_key;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_appsecret;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_curl_proxy_host;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_curl_proxy_port;
	
	@Hidden
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_sslcert_path;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_sslkey_path;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_sslrootca_path;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_private_key;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_notify_url;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_report_levenl;
	
	@Column(columnDefinition="varchar(255) comment '微信支付'")
	public String wxpay_domain;

	@Override
	public String toString() {
		return "微信秘钥";
	}
	
	
}
