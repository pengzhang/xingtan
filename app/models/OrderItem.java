package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.RandomStringUtils;

import annotation.hcommon.Hidden;
import annotation.hcommon.Price;
import play.data.validation.Required;

@Entity
@Table(name = "order_item")
@org.hibernate.annotations.Table(comment = "订单详情", appliesTo = "order_item")
public class OrderItem extends BaseModel {
	
	//订单详情

	@Column(columnDefinition = "bigint comment '订单ID'")
	public long order_id;

	@Column(columnDefinition = "int comment '数量'")
	public int quantity;

	@Price
	@Column(columnDefinition = "int comment '产品总价(以分为单位)'")
	public int product_total;

	@Column(columnDefinition = "bigint comment '商品ID'")
	public long product_id;

	// 商品基本信息

	@Required(message = "填写商品编号")
	@Column(columnDefinition = "varchar(100) comment '商品编号'")
	public String product_num = RandomStringUtils.randomNumeric(10);

	@Required(message = "填写商品名称")
	@Column(columnDefinition = "varchar(100) comment '商品名称'")
	public String product_name;

	@Column(columnDefinition = "text comment '商品描述'")
	public String product_detail;

	@Hidden
	@Column(columnDefinition = "varchar(5000) comment '商品图片(最多10张)'")
	public String product_image;

	@Price
	@Required(message = "填写商品价格")
	@Column(columnDefinition = "int comment '商品价格(以分为单位)'")
	public int product_price;

}
