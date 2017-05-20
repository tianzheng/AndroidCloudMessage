package xu.ye.uitl;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class Tools {
/*
 * 
 * 　　移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188

　 * 　联通：130、131、132、152、155、156、185、186

　 * 　电信：133、153、180、189、（1349卫通）
 * */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	//根据Wifi信息获取本地Mac
    public static String getDevice(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress(); 
    }
	
}
