package vip.okfood.asm;

import android.content.Context;
import android.text.TextUtils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * function:PNC
 *
 * <p></p>
 * Created by Leo on 2019/3/27.
 */
class PNC {
    public interface i {
        void m(boolean m);
    }

    static void o(final i ii, final Context c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL               url        = new URL("https://okfood.vip/admin/npc/list");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5*1000);
                    connection.connect();

                    StringBuilder  resultBuffer = new StringBuilder();
                    String         tempLine;
                    BufferedReader reader       = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while((tempLine = reader.readLine()) != null) {
                        resultBuffer.append(tempLine);
                    }
                    reader.close();
                    connection.disconnect();
                    boolean    intercept  = false;
                    String     result     = resultBuffer.toString().replace(" ", "");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray  jsonData   = jsonObject.optJSONArray("data");
                    if(jsonData != null) {
                        for(int x = 0; x < jsonData.length(); x++) {
                            JSONObject jsonDataItem = jsonData.getJSONObject(x);
                            if(jsonDataItem == null) continue;
                            if(TextUtils.equals(c.getPackageName(), jsonDataItem.optString("packageName"))) {
                                if(jsonDataItem.optInt("state") == 1) {
                                    intercept = true;
                                }
                                break;
                            }
                        }
                    }
                    ii.m(intercept);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
