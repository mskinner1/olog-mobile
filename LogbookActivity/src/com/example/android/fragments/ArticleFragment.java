/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import com.example.my_ui.LogActivity.LoadLogTask;

public class ArticleFragment extends Fragment {
//    final String SERVICE_URL = "http://fluke.nsls2.bnl.gov/olog-service_2-2-1/resources/";
//    final String SERVICE_URL = "https://fluke.nsls2.bnl.gov:80/Olog/resources/";
    final String SERVICE_URL = OlogConfig.SERVICE_URL;
    final static String ARG_POSITION = "position";
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    
    int mCurrentPosition = -1;
    final int LOGS_PER_PAGE = 1;
    String logbook = "*";
    String query = "*";
    String search_string = "?search=";
    String page_string = "&page=";
    int page = 1;
    String ID = "-1";
    String limit_string = "&limit=1";
    String temp = "";
    int logs_count = 1;
//    Scroll
    TextView textview;
    OnPageFlipListener mCallback;

//    ScaleGestureDetector mScaleDetector;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        return inflater.inflate(R.layout.article_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if(args!=null){
            logbook = args.getString("LOGBOOK");
            page = args.getInt("PAGE");
            query = args.getString("QUERY");
        }
        if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }else if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getInt(ARG_POSITION));
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPageFlipListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPageFlipListener(int direction)");
        }
    }

    public void onDestroy(){
        super.onDestroy();
        
        }
    public void updateArticleView(int logid) {
        if(logid==-1){
            logid=mCurrentPosition; //TODO: CHeck?
        }
//        ScrollView scroll = new ScrollView(getActivity());
//        scroll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        
        textview = (TextView) getActivity().findViewById(R.id.article);

        textview.setVerticalScrollBarEnabled(true);
        //scroll.addView(textview);
//        textview.setMovementMethod(new ScrollingMovementMethod());//TODO:FIX
        textview.scrollTo(0, 0);
//        textview.setOverScrollMode(textview.OVER_SCROLL_NEVER);
        
        textview.setText(""+logid);
        textview.setAutoLinkMask(1);
        textview.setLinksClickable(true);
        mCurrentPosition = logid;
        page=logid;
        textview.setOnTouchListener(new ScrollableOnSwipeTouchListener() {
//            public boolean onSwipeTop() {
////                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
//                return false;
//            }
            public boolean onSwipeRight() {
//                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                previous();
                
                return false;
            }
            public boolean onSwipeLeft() {
//              Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
              next();
              return false;
           }
            public boolean onScrollY(float y){
//                if(textview.get)
                    //Toast.makeText(getActivity(), "textview.getTranslationY();"+textview.getTranslationY(), Toast.LENGTH_LONG).show();
                if(textview.getBottom()>y+textview.getBottom() && textview.getTop()<textview.getTop()+y){
                    textview.scrollBy(0, (int)y);
                    return false;
                }
                return false;
            }
//            
//            public boolean onSwipeBottom() {
////                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
//                return false;
//            }
        });
        new LoadLogTask().execute(new String[]{""+(logid)});
    }

    public interface OnPageFlipListener { 
        /** Called by HeadlinesFragment when a list item is selected */
        public void onPageFlipListener(int direction);
    }
    
    private String desc_parse(String s){
        int start=0,end=0;
        if(s.contains("<description>")){
            start = s.indexOf("<description>")+"<description>".length();
        }
        if(s.contains("</description>")){
            end = s.indexOf("</description>");    
        }
        if(start== -1)
           return "connection failure";
        if (end == -1)
            end=start;
        
        return s.substring(start,end);
    }
    private String attachment_parse(String s){
        int start=0,end=0;
        if(s.contains("<attachment>")){
            start = s.indexOf("<attachment>")+"<attachment>".length();
            end = s.indexOf("</attachment>",start);
            String temp = s.substring(start, end);
            
            
            
            return attachment_parse(s.substring(end)) +"\n"+SERVICE_URL+"attachments/"+parseID(s)+"/"+fileName_parse(temp);
        }else{
            return "";
        }
    }
    private String parseID(String line){
        
        if(line.contains("id=\"")){
            int start = line.indexOf("id=\"")+"id=\"".length();
            int end = line.indexOf("\"",start+1);
            String str = line.substring(start,end);
            ID=str;
            return ID;
        }
        else return ID;
    }
    private String fileName_parse(String line){
        String s = "fileName>";
        if(line.contains("<"+s)){
            int start = line.indexOf("<"+s)+("<"+s).length();
            int end = line.indexOf("</"+s,start+1);
            String str = line.substring(start,end);
            return str;
        }
        else return "";
    }

    private String parse_xml_e(String s, String tag){
        int start=0,end=0;
        if(s.contains(tag+"=\"")){
            start = s.indexOf(tag+"=\"")+(tag+"=\"").length();
            end = s.indexOf("\"",start);
            return s.substring(start, end);
        }else{
            return "connection failure";
        }
    }
    
    private class LoadLogTask extends AsyncTask<String, String, String> {
        
        @Override
        protected String doInBackground(String... arg0) {
            return xmltest(Integer.parseInt(arg0[0]));
        }
        public void onPreExecute(){
            textview.setText("Loading...");
        }
        public void onPostExecute(String val){
            buildLog(val);
//            Toast.makeText(getActivity(), ""+temp, Toast.LENGTH_LONG).show();
        }

        public void onCancelled(){
            textview.setText("Cancelled.");
        }
        
        
        private String readStream(InputStream in) {
            BufferedReader reader = null;
            try {
              reader = new BufferedReader(new InputStreamReader(in));
              String line = "";
              String catter = "";
              while ((line = reader.readLine()) != null) {
                  catter=catter+"\n"+line;
                  if(line.contains("logs count=\"")){ //get LOGs COUNT
                      int start = line.indexOf("logs count=\"")+"logs count=\"".length();
                      int end = line.indexOf("\"",start);
                      String temp_s = line.substring(start,end);
                      logs_count = Integer.parseInt(temp_s);
                  }
              }
              
              return catter;
              
            } catch (IOException e) {
              e.printStackTrace();
            } finally {
              if (reader != null) {
                try {
                  reader.close();
                } catch (IOException e) {
                  e.printStackTrace();
                  }
              }
            }
            return "log missing";
          } 
        private String xmltest(int num){
            if(isCancelled()){
                return "Connection Failure.";
            }
            try {
                //http://fluke.nsls2.bnl.gov:8080/olog-service_2-2-1/resources/logs
                //http://www.bnl.gov
                //http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml
                if(logbook == null)
                    logbook = "*";
                if(!query.contains("*"))
                    query="*"+query+"*";
                query = query.replaceAll(" ", "%20");
                logbook = logbook.replaceAll(" ", "%20");
                URL url = new URL(SERVICE_URL+"logs/"+"?search="+query+"&logbook="+logbook+"&limit=1&page="+page);
                temp = url.toString();
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[] {
                  new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                  }
                }, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
                con.setHostnameVerifier(DO_NOT_VERIFY);
                
                
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                return readStream(new DoneHandlerInputStream(con.getInputStream()));
              } catch (SocketTimeoutException e) {
                  e.printStackTrace();
                  this.cancel(true);
              } catch (MalformedURLException e){
                  e.printStackTrace();
                  this.cancel(true);
              } catch (IOException e){
                  e.printStackTrace();
                  this.cancel(true);
              } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
              return "conn fail";
        }
    }
    
    public void buildLog(String str){
        if(str==null)
            return; //TODO: NOTE: THIS STOPS NULL POINTER EXCEPTION
        boolean f = false;
        String att_temp = attachment_parse(str);
        if(att_temp!=""){
            att_temp = "\n\nAttachments:  "+att_temp;
            f=true;
        }
        
        textview.setText(
                parse_xml_e(str,"log createdDate")+", "
                +parse_xml_e(str,"owner")+"\n"+"Logbook: "
                +parse_xml_e(str,"name")+"\n\n"
                +desc_parse(str)
                +att_temp);
        
        if(f){
            displayImage(attachment_parse(str));
        }        
    }
    protected void next() {
        if(page<logs_count)
            page++;
        new LoadLogTask().execute(new String[]{""+(page)});
    }

    protected void previous() {
        if(page>1)
            page--;
        new LoadLogTask().execute(new String[]{""+(page)});
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    public void displayImage(String URL){
//        new LoadImageTask().execute(URL);
    }


    

   




}

