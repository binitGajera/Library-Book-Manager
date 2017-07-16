package com.example.castlesword.librarybooklocator;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by CastleSword on 17-11-2016.
 */
public class WebRequest {
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    private final static String TAG="SID";

    //Constructor with no parameter
    public WebRequest() {
        Log.d(TAG,"INSIDE WEBREQUEST =================================================================");
    }

    /**
     * Making web service call
     *
     * @url - url to make request
     * @requestmethod - http request method
     */
    public String makeWebServiceCall(String url, int reqmethod) {
        Log.d(TAG,"INSIDE makeWebService=================================================================");
        return this.makeWebServiceCall(url, reqmethod, null);

    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @requestmethod - http request method
     * @params - http request params
     */
    public String makeWebServiceCall(String urladdress, int reqmethod, LinkedHashMap<String, String> params) {
        URL url;
        HttpURLConnection conn = null;
        String response = "";
        try {
            url = new URL(urladdress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);

            if (reqmethod == POST)
            {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                if (params != null) {
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    StringBuilder result = new StringBuilder();
                    boolean first = true;
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (first)
                            first = false;
                        else
                            result.append("&");

                        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }

                    writer.write(result.toString());

                    writer.flush();
                    writer.close();
                    os.close();
                }
            }
            else if (reqmethod == GET) {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
            }

            int responseCode = conn.getResponseCode();

            Log.d(TAG,"-------------------------------------------------------Response code---------------------------- = "+responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                Log.d(TAG,"reponse from webrequest = "+response);
            } else {
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if(conn!=null)
                conn.disconnect();
        }

        return response;
    }
}
