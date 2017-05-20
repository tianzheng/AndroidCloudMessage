package xu.ye.view;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.StringEntity;

import xu.ye.R;
import xu.ye.application.ConstantValue;
import xu.ye.bean.ConstactInfoBean;
import xu.ye.bean.LoginBean;
import xu.ye.bean.SMSBean;
import xu.ye.bean.SmsListBean;
import xu.ye.uitl.ContactUtils;
import xu.ye.uitl.RexseeSMS;
import xu.ye.uitl.Tools;
import xu.ye.user.BaseActivity;
import xu.ye.user.Register;
import xu.ye.user.Resetpwd;
import xu.ye.user.UserData;
import xu.ye.user.UserDataManager;
import xu.ye.view.inter.CallBackInterface;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("NewApi")
public class HomeSettintActivity extends BaseActivity implements
		CallBackInterface {

	public int pwdresetFlag = 0;
	private EditText mAccount; // 用户名编辑
	private EditText mPwd; // 密码编辑
	private Button mRegisterButton; // 注册按钮
	private Button mLoginButton; // 登录按钮
	private Button mCancleButton; // 注销按钮
	private CheckBox mRememberCheck;
	private Button cloudBackups, cloudRestore;
	private SharedPreferences login_sp;
	private String userNameValue, passwordValue;

	private View loginView; // 登录
	private View loginSuccessView;
	private TextView loginSuccessShow;
	private TextView mChangepwdText;
	private UserDataManager mUserDataManager; // 用户数据管理类
	private RelativeLayout layoutLogin, layoutUser;
	private String loginName;
	// 获取联系人
	ContactUtils contactUtils;
	// 短信
	private RexseeSMS rsms;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_setting_page);

	

		rsms = new RexseeSMS(HomeSettintActivity.this);
		// 通过id找到相应的控件
		mAccount = (EditText) findViewById(R.id.login_edit_account);
		mPwd = (EditText) findViewById(R.id.login_edit_pwd);
		mRegisterButton = (Button) findViewById(R.id.login_btn_register);
		mLoginButton = (Button) findViewById(R.id.login_btn_login);
		mCancleButton = (Button) findViewById(R.id.login_btn_cancle);
		loginView = findViewById(R.id.login_view);
		loginSuccessView = findViewById(R.id.login_success_view);
		loginSuccessShow = (TextView) findViewById(R.id.login_success_show);

		mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);

		mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);
		layoutLogin = (RelativeLayout) findViewById(R.id.layout_login);
		layoutUser = (RelativeLayout) findViewById(R.id.layout_user);

		login_sp = getSharedPreferences("userInfo", 0);
		String name = login_sp.getString("USER_NAME", "");
		String pwd = login_sp.getString("PASSWORD", "");
		boolean choseRemember = login_sp.getBoolean("mRememberCheck", false);
		boolean choseAutoLogin = login_sp.getBoolean("mAutologinCheck", false);
		// 如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
		if (choseRemember) {
			mAccount.setText(name);
			mPwd.setText(pwd);
			mRememberCheck.setChecked(true);
		}

		// 获取联系人相关信息
		contactUtils = new ContactUtils(HomeSettintActivity.this);

		// 个人中心
		// 备份
		cloudBackups = (Button) findViewById(R.id.cloud_backups);
		// 还原
		cloudRestore = (Button) findViewById(R.id.cloud_restore);

		cloudBackups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 启动异步查询
				contactUtils.getContactInfo(getContentResolver());
			}
		});

		cloudRestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				insertData();

			}
		});

		mRegisterButton.setOnClickListener(mListener); // 采用OnClickListener方法设置不同按钮按下之后的监听事件
		mLoginButton.setOnClickListener(mListener);
		mCancleButton.setOnClickListener(mListener);
		mChangepwdText.setOnClickListener(mListener);

		ImageView image = (ImageView) findViewById(R.id.logo); // 使用ImageView显示logo
		image.setImageResource(R.drawable.ic_launcher);

		if (mUserDataManager == null) {
			mUserDataManager = new UserDataManager(this);
			mUserDataManager.openDataBase(); // 建立本地数据库
		}
	}

	OnClickListener mListener = new OnClickListener() { // 不同按钮按下的监听事件选择
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_btn_register: // 登录界面的注册按钮
				Intent intent_Login_to_Register = new Intent(
						HomeSettintActivity.this, Register.class); // 切换Login
																	// Activity至User
																	// Activity
				startActivity(intent_Login_to_Register);
				// finish();
				break;
			case R.id.login_btn_login: // 登录界面的登录按钮
				login();
				break;
			case R.id.login_btn_cancle: // 登录界面的注销按钮
				cancel();
				break;
			case R.id.login_text_change_pwd: // 登录界面的注销按钮
				Intent intent_Login_to_reset = new Intent(
						HomeSettintActivity.this, Resetpwd.class); // 切换Login
																	// Activity至User
																	// Activity
				startActivity(intent_Login_to_reset);
				// finish();
				break;
			}
		}
	};

	public void login() { // 登录按钮监听事件
		if (isUserNameAndPwdValid()) {
			String userName = mAccount.getText().toString().trim(); // 获取当前输入的用户名和密码信息
			String userPwd = mPwd.getText().toString().trim();
			SharedPreferences.Editor editor = login_sp.edit();
			// int result=mUserDataManager.findUserByNameAndPwd(userName,
			// userPwd);
			/* if(result==1){ */// 返回1说明用户名和密码均正确
			// 保存用户名和密码
			editor.putString("USER_NAME", userName);
			editor.putString("PASSWORD", userPwd);

			// 是否记住密码
			if (mRememberCheck.isChecked()) {
				editor.putBoolean("mRememberCheck", true);
			} else {
				editor.putBoolean("mRememberCheck", false);
			}
			editor.commit();

			UserData data = new UserData();
			data.setUserName(userName);
			loginName = userName;
			data.setUserPwd(userPwd);
			data.setDevice(Tools.getDevice(HomeSettintActivity.this));
			requestHttpPost(data);

			/*
			 * 
			 * Toast.makeText(this,
			 * getString(R.string.login_success),Toast.LENGTH_SHORT
			 * ).show();//登录成功提示 }else if(result==0){ Toast.makeText(this,
			 * getString(R.string.login_fail),Toast.LENGTH_SHORT).show();
			 * //登录失败提示 }
			 */
		}
	}

	public void cancel() { // 注销
		if (isUserNameAndPwdValid()) {
			String userName = mAccount.getText().toString().trim(); // 获取当前输入的用户名和密码信息
			String userPwd = mPwd.getText().toString().trim();
			int result = mUserDataManager.findUserByNameAndPwd(userName,
					userPwd);
			if (result == 1) { // 返回1说明用户名和密码均正确
			// Intent intent = new Intent(Login.this,User.class) ; //切换Login
			// Activity至User Activity
			// startActivity(intent);
				Toast.makeText(this, getString(R.string.cancel_success),
						Toast.LENGTH_SHORT).show();// 登录成功提示
				mPwd.setText("");
				mAccount.setText("");
				mUserDataManager.deleteUserDatabyname(userName);
			} else if (result == 0) {
				Toast.makeText(this, getString(R.string.cancel_fail),
						Toast.LENGTH_SHORT).show(); // 登录失败提示
			}
		}

	}

	public boolean isUserNameAndPwdValid() {
		if (mAccount.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.account_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mPwd.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.pwd_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		if (mUserDataManager == null) {
			mUserDataManager = new UserDataManager(this);
			mUserDataManager.openDataBase();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mUserDataManager != null) {
			mUserDataManager.closeDataBase();
			mUserDataManager = null;
		}
		super.onPause();
	}

	/**
	 * Http pos登陆请求
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void requestHttpPost(UserData device) {
		httpUtils = new HttpUtils();
		// 提示框显示
		dialog.show();
		// 生成Json
		String requsetJson = gson.toJson(device);
		// 声明，实例化对象
		RequestParams params = new RequestParams("UTF-8");
		try {
			params.setBodyEntity(new StringEntity(requsetJson, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.setContentType("applicatin/json");
		String url = ConstantValue.IP + ConstantValue.LOGIN;
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(HomeSettintActivity.this, "请求失败",
								Toast.LENGTH_LONG).show();
						// 关闭提示框
						dialog.dismiss();
					}

					public void onSuccess(ResponseInfo<String> arg0) {
						LoginBean loginBean = new LoginBean();
						System.out.println("=" + arg0.result);
						loginBean = gson.fromJson(arg0.result, LoginBean.class);

						Toast.makeText(HomeSettintActivity.this,
								loginBean.getMessage(), Toast.LENGTH_LONG)
								.show();

						if (loginBean.getResult() == ConstantValue.LOGIN_SUCCEEDED) {
							// finish();
							layoutLogin.setVisibility(View.GONE);
							layoutUser.setVisibility(View.VISIBLE);
						}
						// 关闭提示框
						dialog.dismiss();
					}
				});

	}

	/**
	 * Http pos备份请求
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void requestHttpPost(ConstactInfoBean bean) {
		httpUtils = new HttpUtils();
		// 提示框显示
		dialog.show();
		// 生成Json
		String requsetJson = gson.toJson(bean);
		System.out.println("requsetJson=" + requsetJson);
		// 声明，实例化对象
		RequestParams params = new RequestParams("UTF-8");
		try {
			params.setBodyEntity(new StringEntity(requsetJson, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.setContentType("applicatin/json");
		String url = ConstantValue.IP + ConstantValue.CLOUD_BACKUPS;
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(HomeSettintActivity.this, "请求失败",
								Toast.LENGTH_LONG).show();
						// 关闭提示框
						dialog.dismiss();
					}

					public void onSuccess(ResponseInfo<String> arg0) {
						LoginBean loginBean = new LoginBean();

						loginBean = gson.fromJson(arg0.result, LoginBean.class);

						Toast.makeText(HomeSettintActivity.this,
								loginBean.getMessage(), Toast.LENGTH_LONG)
								.show();

						// 关闭提示框
						dialog.dismiss();
					}
				});

	}

	@Override
	public void execute() {

		ConstactInfoBean bean = new ConstactInfoBean();
		bean.setCount(contactUtils.getContactList().size());
		bean.setContactList(contactUtils.getContactList());
		bean.setUsername(loginName);
		// bean.setList_mmt(list_mmt);
		bean.setListBean(getSmsInPhone());
		requestHttpPost(bean);
	}

	/*
	 * 
	 * 
	 * 
	 * sms主要结构： _id：短信序号，如100 thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
	 * address：发件人地址，即手机号，如+8613811810000 person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
	 * date：日期，long型，如1256539465022，可以对日期显示格式进行设置
	 * protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信 read：是否阅读0未读，1已读
	 * status：短信状态-1接收，0complete,64pending,128failed type：短信类型1是接收到的，2是已发出
	 * body：短信具体内容 service_center：短信服务中心号码编号，如+8613800755500
	 */

	private List<SmsListBean> getSmsInPhone() {
		List<SmsListBean> listBean = new ArrayList<SmsListBean>();
		final String SMS_URI_ALL = "content://sms/";
		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type", "thread_id" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");

			if (cur.moveToFirst()) {

				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;
				String thread_id;
				String _id;
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				int thread_idColumn = cur.getColumnIndex("thread_id");
				int _idColumn = cur.getColumnIndex("_id");
				do {
					_id = cur.getString(_idColumn);
					thread_id = cur.getString(thread_idColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					// name = cur.getString(nameColumn); 这样获取的联系认为空，所以我改用下面的方法获取
					name = getPeopleNameFromPerson(phoneNumber);
					smsbody = cur.getString(smsbodyColumn);

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);

					int typeId = cur.getInt(typeColumn);
					/*
					 * if(typeId == 1){ type = "接收"; } else if(typeId == 2){
					 * type = "发送"; } else { type = "草稿"; }
					 */
					SmsListBean smsListBean = new SmsListBean();
					smsListBean.set_id(_id);
					smsListBean.setThread_id(thread_id);
					smsListBean.setAddress(phoneNumber);
					smsListBean.setPerson(name);
					smsListBean.setBody(smsbody);
					smsListBean.setDate(date);
					smsListBean.setType(typeId + "");
					listBean.add(smsListBean);

				} while (cur.moveToNext());
			}
			cur.close();
			cur = null;

		} catch (SQLiteException ex) {
			Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		return listBean;

	}

	/**
	 * 通过address手机号关联Contacts联系人的显示名字
	 * 
	 * @param address
	 * @return
	 */
	private String getPeopleNameFromPerson(String address) {
		if (address == null || address == "") {
			return null;
		}

		String strPerson = "null";
		String[] projection = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };

		Uri uri_Person = Uri.withAppendedPath(
				ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
				address); // address 手机号过滤
		Cursor cursor = getContentResolver().query(uri_Person, projection,
				null, null, null);

		if (cursor.moveToFirst()) {
			int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			String strPeopleName = cursor.getString(index_PeopleName);
			strPerson = strPeopleName;
		} else {
			strPerson = address;
		}
		cursor.close();
		cursor = null;
		return strPerson;
	}

	private String defaultSmsPkg;
	private String mySmsPkg;

	private void insertData() {

		/*ConstactInfoBean bean = new ConstactInfoBean();
		bean.setCount(contactUtils.getContactList().size());
		bean.setContactList(contactUtils.getContactList());
		bean.setUsername(loginName);
		// bean.setList_mmt(list_mmt);
		bean.setListBean(getSmsInPhone());
		requestHttpPost(bean);*/
	
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			dialog.show();
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dialog.dismiss();
		}
	}).start();
	}

}
