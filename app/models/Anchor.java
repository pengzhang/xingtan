package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import annotation.hcommon.Exclude;
import annotation.hcommon.Hidden;
import play.data.validation.Password;

@Entity
@Table(name="anchor")
@org.hibernate.annotations.Table(comment="主播管理", appliesTo = "anchor")
public class Anchor extends BaseModel implements Serializable {

	@Exclude
	@Column(columnDefinition="varchar(100) comment '用户名'")
	public String username;
	
	@Exclude
	@Password
	@Column(columnDefinition="varchar(100) comment '密码'")
	public String password;
	
	@Exclude
	@Column(columnDefinition="varchar(100) comment '用户邮箱'")
	public String email;
	
	@Exclude
	@Column(columnDefinition="varchar(30) comment '手机号'")
	public String mobile;
	
	@Exclude
	@Column(columnDefinition="varchar(1000) comment '用户头像'")
	public String avatar;
	
	@Column(columnDefinition="varchar(1000) comment '用户照片'")
	public String photo;
	
	@Column(columnDefinition="varchar(255) comment '昵称'")
	public String nickname;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment '性别'")
	public String sex;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment '省份'")
	public String province;
	
	@Column(columnDefinition="varchar(255) comment '出生日期'")
	public String birthdate;
	
	@Column(columnDefinition="varchar(255) comment '身高'")
	public String bodyheight;
	
	@Column(columnDefinition="varchar(255) comment '体重'")
	public String bodyweight;
	
	@Column(columnDefinition="varchar(255) comment '地址'")
	public String address;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment '城市'")
	public String city;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment '国家'")
	public String country;
	
	@Column(columnDefinition="varchar(255) comment '爱好'")
	public String hobby;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment 'openid'")
	public String openid;
	
	@Exclude
	@Column(columnDefinition="varchar(255) comment 'unionid'")
	public String unionid;
	
	@Column(columnDefinition="varchar(255) comment '公司'")
	public String company;
	
	@Column(columnDefinition="varchar(255) comment '经纪人'")
	public String broker;
	
	@Column(columnDefinition="bigint default 0 comment '经纪人ID'")
	public long b_id;
	
	@Column(columnDefinition="tinyint default 0 comment '星卡'")
	public boolean xingcard;
	
	public Anchor() {
		super();
	}

	public Anchor(String avatar, String nickname, String sex, String province, String city, String country, String openid,
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

	@Override
	public String toString() {
		return this.nickname;
	}
}
