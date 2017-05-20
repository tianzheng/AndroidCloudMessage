package xu.ye.bean;

public class SmsListBean {
	/**
	 * sms主要结构：
_id：短信序号，如100
thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
address：发件人地址，即手机号，如+8613811810000
person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
date：日期，long型，如1256539465022，可以对日期显示格式进行设置
protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
read：是否阅读0未读，1已读
status：短信状态-1接收，0complete,64pending,128failed
type：短信类型1是接收到的，2是已发出
body：短信具体内容
service_center：短信服务中心号码编号，如+8613800755500
	 * 
	 * 
	 * 
	 * **/
	
	

	 public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getService_center() {
		return service_center;
	}
	public void setService_center(String service_center) {
		this.service_center = service_center;
	}
	 private  String 	_id;
	 private  String 	thread_id;	
	 private  String 	address; 
	 private  String 	person; 
	 private   String 	date; 
	 private  String 	protocol;
	 private  String 	read;
	 private  String 	status;
	 private  String 	type;
	 private  String 	body;
	 private  String 	service_center;

}
