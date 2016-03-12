package com.socialtv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DistributeAPI {

	public static void activateDevice(final Context context) {
		try {
//			final InputStream stream = DistributeAPI.class.getResourceAsStream("/assets/marketHouse.properties");
			InputStream stream = context.getAssets().open("marketHouse.properties");
			final Properties properties = new Properties();
			if(stream != null) {
				properties.load(stream);
				stream.close();
			}
			final String marketHouseId = properties.getProperty("marketHouseId");
			final String packageName = properties.getProperty("packageName");
			final String versionName = properties.getProperty("versionName");
            submitChannelVersion(context, marketHouseId, packageName, versionName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void activateDevice(final Context context, String marketHouseId, String packageName, String versionName) {
        submitChannelVersion(context, marketHouseId, packageName, versionName);
    }

    private static void submitChannelVersion(Context context, final String marketHouseId, final String packageName, final String versionName) {
        final String phone_brand = Build.MANUFACTURER;
        final String phone_model = Build.MODEL;
        final String platform_version = Build.VERSION.RELEASE;
        final SharedPreferences preferences = context.getSharedPreferences("httpComplete", Context.MODE_PRIVATE);
        final String isPostComplete = preferences.getString("isPostComplete", "no");
        new Thread(){

            @Override
            public void run() {
                if(!versionName.equals(isPostComplete)) {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://210.14.68.164:8080/obd/service/client/activate");
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    parameters.add(new BasicNameValuePair("id", getMacAddress()));
                    parameters.add(new BasicNameValuePair("app_id", packageName));
                    parameters.add(new BasicNameValuePair("build_id", marketHouseId));
                    parameters.add(new BasicNameValuePair("phone_brand", phone_brand));
                    parameters.add(new BasicNameValuePair("phone_model", phone_model));
                    parameters.add(new BasicNameValuePair("platform_name", "android"));
                    parameters.add(new BasicNameValuePair("platform_version", platform_version));
                    parameters.add(new BasicNameValuePair("client_version", versionName));
                    UrlEncodedFormEntity entity = null;
                    try {
                        entity = new UrlEncodedFormEntity(parameters, "UTF-8");
                        post.setEntity(entity);
                        client.execute(post);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("isPostComplete", versionName);
                        editor.commit();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
    }
	
	public static String getMacAddress() {
		try {
			Process process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
			InputStreamReader reader = new InputStreamReader(process.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			return "NA";
		}
	}
}
