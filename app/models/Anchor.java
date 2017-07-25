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
	
	@Column(columnDefinition="varchar(255) comment '经纪人'")
	public String broker;
	
	@Exclude
	@Column(columnDefinition="bigint comment '经纪人ID'")
	public String b_id;

	@Override
	public String toString() {
		return this.nickname;
	}
}
