package xu.ye.bean;

import java.util.List;

import xu.ye.view.inter.CallBackInterface;

public class ConstactInfoBean {
	private List<SmsListBean>	 listBean;
	
	private List<ContactBean> contactList;
	private int count;
	private String username;
	public List<ContactBean> getContactList() {
		return contactList;
	}
	public void setContactList(List<ContactBean> contactList) {
		this.contactList = contactList;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<SmsListBean> getListBean() {
		return listBean;
	}
	public void setListBean(List<SmsListBean> listBean) {
		this.listBean = listBean;
	}
	
}
