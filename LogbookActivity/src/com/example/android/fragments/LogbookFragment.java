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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class LogbookFragment extends ListFragment {
//    final String SERVICE_URL = "http://fluke.nsls2.bnl.gov/olog-service_2-2-1/resources/";
    final String SERVICE_URL = OlogConfig.SERVICE_URL;
    final Context main_act = getActivity();
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    OnLogbookSelectedListener mCallback;
    StableArrayAdapter metadapter;
    final 
    int current_id = 1;
    int max_id = 1;
    String logbook_name_string = "";
    String[] meta_values = null;
    //boolean stop_flag = false;
    int page = current_id/max_id;
    int logs_count;
    
    

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnLogbookSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onLogbookSelected(String name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
       // int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
        //        android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        //SET LIST ADAPTER
        //setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
      
        
        
        if(metadapter==null){
            new LoadLogTask().execute(new String[] {""});
        }else{
            setListAdapter(metadapter);
        }
        //buildList(new String[] {"TEST"});
    }
    
    
    public void refresh(){
        new LoadLogTask().execute(new String[] {""});
        //Toast.makeText(getActivity(), "pg:"+page+" id:"+current_id, Toast.LENGTH_LONG).show();
        
    }
    protected void next_cb(){
       
    }
    protected void prev_cb(){

    }
    

    @Override
    public void onStart() {
        super.onStart();
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnLogbookSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    public void buildList(String[] val){
        if(val==null)//TODO: THIS STOPS NULL POINTER EXCEPTION
            return;
        if(getActivity()==null)
            return;
        //final ListView listview = (ListView) findViewById(R.id.listView1);
        String[] values = val;
        meta_values = values;
        //values = new String[] {""+(values!=null)};
        
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
          list.add(values[i]);
        }
        
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(), layout, list);
        //listview.setAdapter(adapter);
        setListAdapter(adapter);
        /**
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                String[] id_ar = id_string.split("@#@");
                if(id_ar[position]!=""){
                    intent.putExtra("", ""+(Integer.parseInt(id_ar[position])));
                    //Toast.makeText(main_act, id_string, Toast.LENGTH_LONG).show();
                }else intent.putExtra("", ""+position);
                startActivity( intent );
            }    
        });**/
        
    }
    
    

    protected class LoadLogTask extends AsyncTask<String[], Integer, String[]> {
        HttpURLConnection con = null;
        @Override
        protected String[] doInBackground(String[]... arg0){
            return populate_list();
            
        }
        public void onPreExecute(){
            buildList(new String[]{"Loading..."});
            
        }
        
        public void onPostExecute(String[] val){
            if(val!=null){
                buildList(val);
            }else{
                buildList(new String[] {"Too slow."});
            }
        }

        public void onProgressUpdate(Integer... val){
            buildList(new String[]{""+val[0]+" of "+logs_count});
        }
        
        public void onCancelled(){
            buildList(new String[]{"Cancelled."});
        }
        
        public String[] populate_list(){
            String id_list = "";
            String[] values = new String[] { "1", "2", "3",
                    "4", "5", "6", "7", "8",
                    "9", "10", "11", "12", "13", "14",
                    "15", "16", "17", "18", "19", "20",
                    "21", "22", "23" };
            
            String col = "";
            String temp = xmltest();
            if (temp!="conn fail"){
                if(temp.contains("name=")){
                    col=parseName(temp);
                }
            }
            
            //col=col.replaceAll("connection failure >> connection failure@#@", "");
            col=col+"All@#@";
            values = col.split("@#@");
            logbook_name_string = col;
            return values;
        }
        protected String readStream(InputStream in) {
            BufferedReader reader = null;
            try {
              reader = new BufferedReader(new InputStreamReader(in));
              String line = "";
              String catter = "";
              while ((line = reader.readLine()) != null) {
                  catter=catter+"\n"+line;
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
        protected String xmltest(){
            if(isCancelled()){
                return "Connection Failure.";
            }
            try {
                URL url = new URL(SERVICE_URL+"logbooks");
                HttpsURLConnection con = (HttpsURLConnection) url
                  .openConnection();
                
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
                
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
//                con.setRequestMethod("GET");
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
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              return "conn fail";
        }
        private String parseName(String line){
    
            if(line.contains("name=\"")){
                int start = line.indexOf("name=\"")+"name=\"".length();
                int end = line.indexOf("\"",start+1);
                //return line.substring(start,end)+ "@#@";
                String str = line.substring(start,end)+"@#@";
                if(line.substring(end).contains("name=\"")){
                    return str+parseName(line.substring(end));
                }else return str;
       
            }
            else return "";
        }
        
    }

    
    

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        String[] id_ar = logbook_name_string.split("@#@");
        if(id_ar[position]!=""){
            //Toast.makeText(main_act, id_string, Toast.LENGTH_LONG).show();
        
        mCallback.onLogbookSelected(id_ar[position]);
        }else{
            mCallback.onLogbookSelected(""+position+1);
        }
        
        
        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
        //new LoadLogTask().execute(new String[] {""});
    }
}