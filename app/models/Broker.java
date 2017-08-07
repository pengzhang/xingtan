package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import annotation.hcommon.Exclude;
import annotation.hcommon.Hidden;
import play.data.validation.Password;

@Entity
@Table(name="broker")
@org.hibernate.annotations.Table(comment="经纪人管理", appliesTo = "broker")
public class Broker extends BaseModel implements Serializable {

	@Column(columnDefinition="varchar(100) comment '用户名'")
	public String username;
	
	@Password
	@Column(columnDefinition="varchar(100) comment '密码'")
	public String password;
	
	@Column(columnDefinition="varchar(100) comment '用户邮箱'")
	public String email;
	
	@Column(columnDefinition="varchar(30) comment '手机号'")
	public String mobile;
	
	@Column(columnDefinition="varchar(1000) comment '用户头像'")
	public String avatar;
	
	@Column(columnDefinition="varchar(1000) comment '用户照片'")
	public String photo;
	
	@Column(columnDefinition="varchar(255) comment '昵称'")
	public String nickname;
	
	@Column(columnDefinition="varchar(255) comment '性别'")
	public String sex;
	
	@Column(columnDefinition="varchar(255) comment '省份'")
	public String province;
	
	@Column(columnDefinition="varchar(255) comment '城市'")
	public String city;
	
	@Column(columnDefinition="varchar(255) comment '国家'")
	public String country;
	
	@Column(columnDefinition="varchar(255) comment 'openid'")
	public String openid;
	
	@Column(columnDefinition="varchar(255) comment 'unionid'")
	public String unionid;
	
	@Column(columnDefinition="varchar(255) comment '公司'")
	public String company;
	
	@Column(columnDefinition="tinyint default 0 comment '状态:0-未激活,1-已激活'")
	public boolean status = false;
	
	@Column(name="bankcode", columnDefinition="varchar(255) comment '朗玛银行卡号'")
	public String bankCode;
	
	@Column(name="bankname", columnDefinition="varchar(255) comment '朗玛银行名称'")
	public String bankName;
	
	@Column(name="realname", columnDefinition="varchar(255) comment '朗玛真实姓名'")
	public String realName;
	
	@Column(name="recommendid", columnDefinition="varchar(255) comment '朗玛推荐ID'")
	public String recommendId;
	
	@Column(name="phone", columnDefinition="varchar(255) comment '朗玛电话号码'")
	public String phone;
	
	@Column(name="idcode", columnDefinition="varchar(255) comment '朗玛身份证号码'")
	public String idCode;
	
	@Column(name="userid", columnDefinition="varchar(255) comment '朗玛用户ID'")
	public String userId;
	
	public Broker() {
		super();
	}

	public Broker(String avatar, String nickname, String sex, String province, String city, String country, String openid,
			String unionid) {
		super();
		this.avatar = avatar;
		this.nickname = nickname;
		this.sex = sex;
		this.province = province;
		this.city = city;
		this.country = country;
		this.openid = openid;
		this.unionid = unionid;
	}
	
	public static void langma(String id, LangMa langma){
		Broker broker = Broker.findById(Long.parseLong(id));
		if(broker != null){
			broker.bankCode = langma.bankCode;
			broker.bankName = langma.bankName;
			broker.realName = langma.realName;
			broker.recommendId = langma.recommendId;
			broker.phone = langma.phone;
			broker.idCode = langma.idCode;
			broker.userId = langma.userId;
			broker.save();
		}
	}
	
	public static String getLangMaUser(String id) {
		Broker broker = Broker.findById(Long.parseLong(id));
		if(broker != null){
			if(StringUtils.isNotEmpty(broker.userId)){
				return broker.userId;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.nickname;
	}

	
}
