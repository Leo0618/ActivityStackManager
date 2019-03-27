package vip.okfood.asm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * function:PNC
 *
 * <p></p>
 * Created by Leo on 2019/3/27.
 */
class PNC {
    public interface i {
        void m(String m);
    }

    static void o(final i ii) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
                    URL               url        = new URL("https://okfood.vip/config_custom/black_list");
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
                    result = resultBuffer.toString().replace(" ", "");
                } catch(Exception e) {
                    e.printStackTrace();
                }
                ii.m(result);
            }
        }).start();
    }
}
