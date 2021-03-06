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
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.widget.Toast;

public class HeadlinesFragment extends ListFragment {
//    final String SERVICE_URL = "http://fluke.nsls2.bnl.gov/olog-service_2-2-1/resources/";
//    final String SERVICE_URL = "https://fluke.nsls2.bnl.gov:80/Olog/resources/";
    final String SERVICE_URL = OlogConfig.SERVICE_URL;
    final int LOGS_PER_PAGE = 20;
    final int max_size = 100;
    final Context main_act = getActivity();
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    
    OnHeadlineSelectedListener mCallback;
    int position = -1;
    StableArrayAdapter metadapter;
    AsyncTask current_task = null;
    int current_id = 1;
    int max_id = 1;
    String id_string = "";
    String[] meta_values = null;
    int page = 1;
    String logbook = "*";
    public int logs_count;
    String FILTER = "";
    
    
    
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener { 
        /** Called by HeadlinesFragment when a list item is selected */
        public void onHeadlineSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        if(savedInstanceState!=null){
            page = savedInstanceState.getInt("PAGE");
        }
        
    }
    
    public void setLogbook(String str){
        if(str==""){
            logbook="";
        }else{
            logbook="&logbook="+str;
        }
       page=1;
    }
    public String getLogbook(){
        if(logbook==""){
            return logbook;
        }else{
            return logbook.replaceFirst("&logbook=", "");
        }
    }
    public void refresh(){
        KILL_TASK();
        current_task = new LoadLogbookTask().execute(new String[] {""});
        
        //Toast.makeText(getActivity(), "pg:"+page+" id:"+current_id, Toast.LENGTH_LONG).show();
        
    }
    protected void next_cb(){
       KILL_TASK();
       if(page <= logs_count/LOGS_PER_PAGE - 1){
           page++;
           current_task = new LoadLogbookTask().execute(new String[] {""});
       }
        if(getActivity()!=null)
            Toast.makeText(getActivity(), "Page: "+page+" of "+(logs_count/LOGS_PER_PAGE), Toast.LENGTH_SHORT).show();
    }
    protected void prev_cb(){
        KILL_TASK();
        if(page>1)
            page--;
        current_task = new LoadLogbookTask().execute(new String[] {""});
        if(getActivity()!=null)
            Toast.makeText(getActivity(), "Page: "+page+" of "+(logs_count/LOGS_PER_PAGE), Toast.LENGTH_SHORT).show();
        
    }

    @Override
    public void onStart() {
        super.onStart();
       // getListView().
        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if(getArguments()!=null){
            if(getArguments().getString("LOGBOOK")!=null){
                setLogbook(getArguments().getString("LOGBOOK"));
            }
            //Toast.makeText(getActivity(), logbook, Toast.LENGTH_LONG).show();
        }
        
        //Toast.makeText(getActivity(), "METADAPTER="+ (metadapter==null), Toast.LENGTH_LONG).show();
        if(meta_values==null){
            if (logbook!=""){
                current_task = new LoadLogbookTask().execute(new String[] {""});
            }else{
                current_task = new LoadLogTask().execute(new String[] {""});
            }
        }else{
            buildList(meta_values);
            
            //setListAdapter(metadapter);
        }
        //buildList(new String[] {"TEST"});
        //*if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setOnTouchListener(new OnSwipeTouchListener() {
                public boolean onSwipeTop() {
//                    Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
                    return true;
                }
                public boolean onSwipeRight() {
//                    Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                    prev_cb();
                    return true;
                }
                public boolean onSwipeLeft() {
//                    Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                    next_cb();
                    return true;
                }
                public boolean onSwipeBottom() {
//                    Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
       //* }
    }
    public void onStop(){
        super.onStop();
      //*  if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            //getListView().setSelected(false);
         //*   metadapter = (StableArrayAdapter) getListView().getAdapter();
       //* }
    }
   
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        
    }
 

    public void onResume(){
        super.onResume();
//        this.getListView().setSelection(position);
    }
    
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    public void buildList(String[] val){
        if(val==null)//TODO: THIS STOPS NULL POINTER EXCEPTION
            return;
        //final ListView listview = (ListView) findViewById(R.id.listView1);
        String[] values = val;
        meta_values = values;
        
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
          list.add(values[i]);
        }
//        if(FILTER!="")
//            Toast.makeText(getActivity(), ""+FILTER, Toast.LENGTH_LONG).show();
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//                 R.layout.list_text_layout : android.R.layout.simple_list_item_1;
                 R.layout.list_text_layout : R.layout.list_text_layout_small;
        if(getActivity()!=null){
        final StableArrayAdapter adapter2 = new StableArrayAdapter(getActivity(), layout, list);
        
        
        //listview.setAdapter(adapter);
        setListAdapter(adapter2);}
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
    
    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager  = (ConnectivityManager) main_act.getSystemService(main_act.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return true;
    }

    protected class LoadLogTask extends AsyncTask<String[], Integer, String[]> {
        HttpsURLConnection con= null;
        int count = 0;
        @Override
        protected String[] doInBackground(String[]... arg0) {
            return populate_list(Integer.parseInt(xml_get_id()));
        }
        public void onPreExecute(){
            buildList(new String[]{"Loading..."});
        }
        public void onPostExecute(String[] val){
            buildList(val);
        }

        public void onProgressUpdate(Integer... val){
            buildList(new String[]{""+val[0]+" of "+logs_count});
        }
        
        public void onCancelled(){
            buildList(new String[]{"Cancelled."});
        }
        
        
        public String desc_parse(String s){
            int start=0,end=0;
            
           // int width = getWindowManager().getDefaultDisplay().getWidth();
           // int height = getWindowManager().getDefaultDisplay().getHeight();
            
            
            
            boolean flag = false;
            if(s.contains("<description>")){
                start = s.indexOf("<description>")+"<description>".length();
            }
            if(s.contains("</description>")){
                end = s.indexOf("</description>");    
            }
            //if(s.substring(start).contains("\n")){
            //    end = s.indexOf("\n");
             //   flag=true;
            //}
            if (end>(start+max_size)){
                end=start+max_size;
                flag=true;
            }
            if(start== -1)
               return "connection failure";
            if (end == -1)
                end=start;
            if (flag==true)
                return s.substring(start,end)+"";
            return s.substring(start,end);
        }
        public String user_parse(String s){
            int start=0,end=0;
            if(s.contains("owner=\"")){
                start = s.indexOf("owner=\"")+"owner=\"".length();
                end = s.indexOf("\"",start);
                if (end>(start+15))
                    end=start+15;
                return s.substring(start, end);
            }else{
                return "connection failure";
            }
        }
        
        protected String readStreamForID(InputStream in) {
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
                      String temp = line.substring(start,end);
                      logs_count = Integer.parseInt(temp);
                      count=count++;
                      update_progress(count);
                  }
                  if(line.contains("id")){
                      int start = line.indexOf("id=\"")+"id=\"".length();
                      int end = line.indexOf("\"",start);
                      String temp = line.substring(start,end);
                      max_id = Integer.parseInt(temp);
                      current_id=max_id;
                      return temp;
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
        
        public String[] populate_list(int start_pos){
            int current_id = start_pos;
            if(current_id > max_id)
                max_id = current_id;
            //page = 0;
            String id_list = "";
            String[] values = new String[] { "1", "2", "3",
                    "4", "5", "6", "7", "8",
                    "9", "10", "11", "12", "13", "14",
                    "15", "16", "17", "18", "19", "20",
                    "21", "22", "23" };
            
            String col = "";
            for(int i = start_pos; i>=start_pos-LOGS_PER_PAGE && !isCancelled(); i--){
                //update_progress(i);
                String temp = xmltest(i);
                 if (temp!="conn fail"){
                     col=col+user_parse(temp)+" >> "+desc_parse(temp)+"@#@";
                     if(temp.contains("id")){
                         id_list=id_list+parseID(temp);
                     }
                 }
            }
            id_string = id_list;
            //col=col.replaceAll("connection failure >> connection failure@#@", "");
//            col=col+"headlinetemp"+current_id+"@#@";
            values = col.split("@#@");
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
        protected String xmltest(int num){
            if(isCancelled()){
                return "Connection Failure.";
            }
            try {
                //http://fluke.nsls2.bnl.gov:8080/olog-service_2-2-1/resources/logs
                //http://www.bnl.gov
                //http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml
                
                URL url = new URL(SERVICE_URL+"logs/"+num);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              return "conn fail";
        }
        private String xml_get_id(){
            if(isCancelled()){
                return "Connection Failure.";
            }
            try {
                //http://fluke.nsls2.bnl.gov:8080/olog-service_2-2-1/resources/logs
                //http://www.bnl.gov
                //http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml
                
                URL url = new URL(SERVICE_URL+"logs/");
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              return "conn fail";
        }

        private String parseID(String line){
            
            if(line.contains("id=\"")){
                int start = line.indexOf("id=\"")+"id=\"".length();
                int end = line.indexOf("\"",start+1);
                //return line.substring(start,end)+ "@#@";
                String str = line.substring(start,end)+"@#@";
                /**if(line.substring(end).contains("id=\"")){
                    return str+parseID(line.substring(end));
                }else**/ return str;
               
            }
            else return "";
        }
        
        public void update_progress(int i){
            publishProgress(i+(page-1)*LOGS_PER_PAGE);
        }
        
    }
    
    protected class LoadLogbookTask extends LoadLogTask {
        HttpsURLConnection con= null;
        
        @Override
        protected String[] doInBackground(String[]... arg0) {
            
            return populate_list(xml_get_search_IDs(arg0[0][0]));
        }
        private String readStreamForIDs(InputStream in) {
            BufferedReader reader = null;
            try {
              reader = new BufferedReader(new InputStreamReader(in));
              String line = "";
              String id_list = "";
              while ((line = reader.readLine()) != null && !isCancelled()) {
                  if(line.contains("id")){
                      id_list=id_list+parseID(line);
                  }
                  if(line.contains("logs count=\"")){ //get LOGs COUNT
                      int start = line.indexOf("logs count=\"")+"logs count=\"".length();
                      int end = line.indexOf("\"",start);
                      String temp = line.substring(start,end);
                      logs_count = Integer.parseInt(temp);
                  }
              }
              id_string = id_list;
              return id_list;
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
        public String[] populate_list(String[] id_list){
            //current_id = start_pos;
            int start_pos = 0;
            String[] values = new String[] { "1", "2", "3",
                    "4", "5", "6", "7", "8",
                    "9", "10", "11", "12", "13", "14",
                    "15", "16", "17", "18", "19", "20",
                    "21", "22", "23" };
            values=id_list;
            
            String col = "";
            for(int i = start_pos; i<id_list.length && i <+LOGS_PER_PAGE && !isCancelled(); i++){
                String temp = "conn fail";
                update_progress(i);
                try{
                    temp = xmltest(Integer.parseInt(id_list[i]));
                }catch(NumberFormatException e){
                    temp = "conn fail";
                }
                //col=col+temp+"@#@";
                if (temp!="conn fail")
                     col=col+user_parse(temp)+" >> "+desc_parse(temp)+"@#@";
            }
            //col=col.replaceAll("connection failure >> connection failure@#@", "");
//            col=col+"headlinetemp"+current_id+"@#@";
            values = col.split("@#@");
            return values;
        }
        
        private String[] xml_get_search_IDs(String search_string){
            try {
                //http://fluke.nsls2.bnl.gov:8080/olog-service_2-2-1/resources/logs
                //http://www.bnl.gov
                //http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml
                if(con!=null){
                    con.disconnect();
                    con=null;
                }
                URL url = new URL(SERVICE_URL+"logs?search=*"+logbook+FILTER+"&limit="+LOGS_PER_PAGE+"&page="+page);
                con = (HttpsURLConnection) url.openConnection();
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
                
                //return new String[] {url.toString()};
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                return readStreamForIDs(new DoneHandlerInputStream(con.getInputStream())).replace("|","").split("@#@");
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
            return new String[]{"conn fail","http://fluke.nsls2.bnl.gov:8080/olog-service_2-2-1/resources/logs?search=*"+logbook};
        }
        
        private String parseID(String line){
            
            if(line.contains("id=\"")){
                int start = line.indexOf("id=\"")+"id=\"".length();
                int end = line.indexOf("\"",start+1);
                count++;
                update_progress(count);
                //return line.substring(start,end)+ "@#@";
                String str = line.substring(start,end)+"@#@";
                if(line.substring(end).contains("id=\"")){
                    return str+parseID(line.substring(end));
                }else return str;
               
            }
            else return "";
        }
        
    }
    
    public String getIdString(){
        return id_string;
    }
    public void setIdString(String idstring){
        id_string = idstring;
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        //ODO: probably not a good idea to use this.position, but meh.
        //Toast.makeText(main_act, "Network: "+isNetworkAvailable(), Toast.LENGTH_LONG).show();
        //String[] id_ar = id_string.split("@#@");
        this.position = position;
        mCallback.onHeadlineSelected(page*20-20+position+1);//Integer.parseInt(id_ar[position]));
         // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
        
        //new LoadLogTask().execute(new String[] {""});
    }

    public int getPage() {
        return page;
    }
    public void setPage(int p){
        page = p;
    }
    
    public int getPosition(){
        return position;
    }
    public String[] getMeta_Values(){
        return meta_values;
    }

    public void toast(String str){
        Toast.makeText(main_act, str, Toast.LENGTH_SHORT).show();
    }
    
    final protected void KILL_TASK(){
        if(current_task!=null)
            current_task.cancel(true);
    }
    public void setFilter(String f){
        FILTER = f;
    }

}


final class DoneHandlerInputStream extends FilterInputStream {
    private boolean done;

    public DoneHandlerInputStream(InputStream stream) {
        super(stream);
    }

    @Override public int read(byte[] bytes, int offset, int count) throws IOException {
        if (!done) {
            int result = super.read(bytes, offset, count);
            if (result != -1) {
                return result;
            }
        }
        done = true;
        return -1;
    }
}
