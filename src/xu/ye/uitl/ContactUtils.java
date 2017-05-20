package xu.ye.uitl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xu.ye.bean.ConstactInfoBean;
import xu.ye.bean.ContactBean;
import xu.ye.view.HomeSettintActivity;
import xu.ye.view.inter.CallBackInterface;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactUtils {

	private CallBackInterface callBack;
	public ContactUtils(CallBackInterface callBack){
		this.callBack=callBack;
	}
	
	
	private AsyncQueryHandler asyncQuery;
	private List<ContactBean> contactList;
	public List<ContactBean> getContactList() {
		return contactList;
	}

	/*public void setContactList(List<ContactBean> contactList) {
		this.contactList = contactList;
	}*/

	private Map<Integer, ContactBean> contactIdMap = null;
	public  void getContactInfo(ContentResolver cr){
		asyncQuery = new AsyncQueryHandlerContact(cr);
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
			String[] projection = { 
					ContactsContract.CommonDataKinds.Phone._ID,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.DATA1,
					ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
					ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
			}; // 查询的列
			asyncQuery.startQuery(0, null, uri, projection, null, null,
					"sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
		
		
	}
	
	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class AsyncQueryHandlerContact extends AsyncQueryHandler {

		AsyncQueryHandlerContact(ContentResolver cr) {
			super(cr);
		}

		/**
		 * 查询结束的回调函数
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				
				contactIdMap = new HashMap<Integer, ContactBean>();
				
				contactList = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);
					if (contactIdMap.containsKey(contactId)) {
						
					}else{
						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
						cb.setPhoneNum(number);
						cb.setSortKey(sortKey);
						cb.setContactId(contactId);
						cb.setPhotoId(photoId);
						cb.setLookUpKey(lookUpKey);
						contactList.add(cb);
						contactIdMap.put(contactId, cb);
					}
				}
				//启动回掉
				System.out.println("启动手机通讯录更新");
				callBack.execute();
				
			}
		}

	}
}
