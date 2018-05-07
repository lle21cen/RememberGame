package org.uiproject.remembergame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

class phpDown2 extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... urls) {
       StringBuilder jsonHtml = new StringBuilder();
       try {
          URL url = new URL(urls[0]);
          HttpURLConnection conn = (HttpURLConnection) url
                .openConnection();
          if (conn != null) {
             conn.setConnectTimeout(10000);
             conn.setUseCaches(false);
             if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                      new InputStreamReader(conn.getInputStream(),
                            "UTF-8"));
                for (;;) {
                   String line = br.readLine();
                   Log.d("line",line);
                   if (line == null)
                      break;
                   jsonHtml.append(line + "\n");
                }
                br.close();
             }

             conn.disconnect();
          } 
       } catch (Exception ex) {
          ex.printStackTrace();
       }
       return jsonHtml.toString();

    }

    protected void onPostExecute(String str) {
    }
 }