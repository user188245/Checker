package com.user.checker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.*;

public class HttpManager extends AsyncTask<String,Void,String>{
    public final static String webServer = "http://118.36.72.159/Myphp/";
    public final static String privateKey = "e509186dab3609982e91afec24e94dea";

    private static String result(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine();
        s.close();
        return data;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String urlString = params[0];
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            JSONObject jsonObject = new JSONObject();
            for(int i=1; i<params.length; i+=2)
                jsonObject.put(params[i],params[i+1]);
            outputStream.write(("data=" + URLEncoder.encode(jsonObject.toString(),"UTF-8")).getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream;
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                inputStream = httpURLConnection.getInputStream();
            else
                inputStream = httpURLConnection.getErrorStream();
            String result = result(inputStream);
            inputStream.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return "error";
    }

    public static String post(String... params){
        try {
            return (new HttpManager()).execute(params).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "{}";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "{}";
        }
    }

}
