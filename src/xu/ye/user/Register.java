package xu.ye.user;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import xu.ye.R;
import xu.ye.application.ConstantValue;
import xu.ye.bean.RegisterBean;
import xu.ye.uitl.Tools;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Register extends BaseActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private UserDataManager mUserDataManager;         //用户数据管理类
    HttpUtils 	httpUtils;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
     
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                   /* Intent intent_Register_to_Login = new Intent(Register.this,UserLogin.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);*/
                   finish();
                    break;
            }
        }
    };
    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
           
            //检查用户是不是手机号
            boolean isMobileNO= Tools.isMobileNO(userName);
            if(!isMobileNO){
                Toast.makeText(this, "请输入正确的手机号",Toast.LENGTH_SHORT).show();
                return ;
            }
           
            
            if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
                Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
                return ;
            } else {
            	UserData data=new UserData();
            	data.setUserName(userName);
            	data.setUserPwd(userPwd);
            	data.setDevice(Tools.getDevice(Register.this));
            	requestHttpPost(data);
            	
            //	UserData data=new UserData(userName, userPwdCheck)
            	
            	
            	/*
                UserData mUser = new UserData(userName, userPwd);
                mUserDataManager.openDataBase();
                long flag = mUserDataManager.insertUserData(mUser); //新建用户信息
                if (flag == -1) {
                    Toast.makeText(this, getString(R.string.register_fail),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, getString(R.string.register_success),Toast.LENGTH_SHORT).show();
                    Intent intent_Register_to_Login = new Intent(Register.this,UserLogin.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                }
            */
            	
            
            
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
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    
    
    /**
	 * Http pos请求
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void requestHttpPost(UserData device) {
		httpUtils=new HttpUtils();
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
		String url = ConstantValue.IP+ConstantValue.REGISTER;
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(Register.this, "请求失败",
								Toast.LENGTH_LONG).show();
						// 关闭提示框
						dialog.dismiss();
					}

					public void onSuccess(ResponseInfo<String> arg0) {
						RegisterBean registerBean=new RegisterBean();
						
						registerBean=gson.fromJson(arg0.result, RegisterBean.class);
						Toast.makeText(Register.this,
								registerBean.getMessage(), Toast.LENGTH_LONG).show();

						if (registerBean.getResult()==ConstantValue.REGISTER_SUCCEEDED) {
							finish();
						}
						// 关闭提示框
						dialog.dismiss();
					}
				});

	}
    
}
