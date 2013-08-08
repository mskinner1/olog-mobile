package com.example.android.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class PostActivity extends SherlockActivity {
//    final String SERVICE_URL = "http://fluke.nsls2.bnl.gov/olog-service_2-2-1/resources/";
//    final String SERVICE_URL = "https://fluke.nsls2.bnl.gov:80/Olog/resources/";
    final String SERVICE_URL = OlogConfig.SERVICE_URL;
//    final String AUTH_SCOPE = "fluke.nsls2.bnl.gov";
    final String AUTH_SCOPE =  OlogConfig.AUTH_SCOPE;
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    ProgressBar progress = null;
    TextView status = null;
    Context main_act;
    String emailaddr;
    String password;
    Dialog myDialog;
    String data = "";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_popup);
        main_act = this.getApplicationContext();
        final Button button = (Button) findViewById(R.id.submit_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });
        Spinner spin = (Spinner) findViewById(R.id.logbook_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        String[] temp = getIntent().getExtras().getString("LOGBOOKS").replace("All@#@","").split("@#@");
//        adapter.addAll();
        for(int i=0; i< temp.length;i++){
            adapter.add(temp[i]);
        }
        
        spin.setAdapter(adapter);
        
    }
    
    public boolean postData(String logbooks, String tags, String body) {
        // Create a new HttpClient and Post Header
        
        String logbook_owner = "";
        
        
        
        
        
        DefaultHttpClient httpclient = (DefaultHttpClient) getNewHttpClient();
//        httpclient.setCredentialsProvider(credsProvider);
        httpclient.getCredentialsProvider().setCredentials(new AuthScope(AUTH_SCOPE, AuthScope.ANY_PORT), 
            new UsernamePasswordCredentials(emailaddr, password));
//        HttpDelete httpdelete = new HttpDelete(SERVICE_URL+"logs");
        
//        httpdelete.setHeader("id", "3163");
        
        HttpPost httppost = new HttpPost(SERVICE_URL+"logs");
//        httppost.setHeader("Authorization", emailaddr+""+password);
        try {
//            // Add your data
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("level", "Info"));
//            nameValuePairs.add(new BasicNameValuePair("description", "POST test 1"));
//            nameValuePairs.add(new BasicNameValuePair("logbook", "Operations"));
//            nameValuePairs.add(new BasicNameValuePair("owner", "olog"));
//            
//            
            
            httppost.setHeader("content-type", "application/json");
            httppost.setHeader("accept", "application/json");
            
            httppost.setEntity(new StringEntity("[{\"description\": \""+body+"\", \"tags\": [], \"level\": \"Info\", \"logbooks\": [{\"owner\": \""+logbook_owner+"\", \"name\": \""+logbooks+"\"}], \"owner\": \""+logbook_owner+"\", \"properties\": []}]","UTF8"));
            
           
//            con.setHostnameVerifier(DO_NOT_VERIFY);
            // Execute HTTP Post Request
//            
//            List<String> authpref = new ArrayList<String>();
//            authpref.add(AuthPolicy.BASIC);
//            httpclient.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, authpref);
            
            
            HttpResponse response = httpclient.execute(httppost);
            data = _getResponseBody(response.getEntity());
//            Toast.makeText(getBaseContext(), "test: "+response.toString(), Toast.LENGTH_LONG).show();
            
        } catch (ClientProtocolException e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "test: clientprotocolfail - "+response.toString(), Toast.LENGTH_LONG).show();
            data = "CLIENT PROTOCOL EXCEPTION ";
            return false;
            
        } catch (IOException e) {
            e.printStackTrace();
            data = "IOException";
//            Toast.makeText(getBaseContext(), "test: IOexcept fail - "+response.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    } 
    
    private void submit(){
        callLoginDialog();
        
        //postData();
    }

    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {
        if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
        InputStream instream = entity.getContent();
        if (instream == null) { return ""; }
        if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(
                    "HTTP entity too large to be buffered in memory"); }
        String charset = getContentCharSet(entity);
        if (charset == null) {
            charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        Reader reader = new InputStreamReader(instream, charset);
        StringBuilder buffer = new StringBuilder();
        try {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return buffer.toString();
        }
         
        public static String getContentCharSet(final HttpEntity entity) throws ParseException {
            if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
            String charset = null;
            if (entity.getContentType() != null) {
                HeaderElement values[] = entity.getContentType().getElements();
                if (values.length > 0) {
                    NameValuePair param = values[0].getParameterByName("charset");
                    if (param != null) {
                        charset = param.getValue();
                    }
                }
            }
            return charset;
        }
    
        public HttpClient getNewHttpClient() {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                
                
//                SSLContext ctx = SSLContext.getInstance("TLS");
//                ctx.init(null, new TrustManager[] {
//                  new X509TrustManager() {
//                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
//                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
//                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
//                  }
//                }, null);
                
                
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[] {
                  new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                  }
                }, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
                
                
                
                
                SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 8181));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient();
            }
        }
    
private void callLoginDialog() 
{
    myDialog = new Dialog(this);
    myDialog.setContentView(R.layout.activity_login);
    myDialog.setCancelable(true);
    
    Button login = (Button) myDialog.findViewById(R.id.sign_in_button);

     EditText memailaddr = (EditText) myDialog.findViewById(R.id.email);
     EditText mpassword = (EditText) myDialog.findViewById(R.id.password);
     status = (TextView) myDialog.findViewById(R.id.login_status_message);
     progress = (ProgressBar) myDialog.findViewById(R.id.progress_bar);
     this.emailaddr = memailaddr.getText().toString();
     this.password = mpassword.getText().toString();

     Toast.makeText(main_act, emailaddr+""+password, Toast.LENGTH_LONG).show();
     
     myDialog.show();

    login.setOnClickListener(new OnClickListener()
    {

       @Override
       public void onClick(View v)
       {
           
           
           EditText memailaddr = (EditText) myDialog.findViewById(R.id.email);
           EditText mpassword = (EditText) myDialog.findViewById(R.id.password);
           emailaddr = memailaddr.getText().toString();
           password = mpassword.getText().toString();
           Toast.makeText(main_act, emailaddr+""+password, Toast.LENGTH_LONG).show();
           new UserLoginTask().execute();
       }
   });

    
    
    

}



public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    boolean flag = true;
    String logbooks = "";
    String tags = "";
    String body = "";
    String username = "";
    String pwd = "";
    @Override
    protected void onPreExecute(){
        EditText edittext1 = (EditText) findViewById(R.id.desc_txt);
        body = edittext1.getText().toString();
        
        Spinner edittext2 = (Spinner) findViewById(R.id.logbook_spinner);
        logbooks = edittext2.getSelectedItem().toString();
        
        EditText edittext3 = (EditText) findViewById(R.id.tag_txt);
        tags = edittext3.getText().toString();
        
        progress.setVisibility(ProgressBar.VISIBLE);
        status.setVisibility(TextView.VISIBLE);
        status.setText("Signing in...");
        
    }
    
    @Override
    protected Boolean doInBackground(Void... params) {

//        try {
//            // Simulate network access.
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
        
        
        
        flag = postData(logbooks,tags,body);
//        flag = deleteData();
        
        
//            return false;
//        }
//
//        for (String credential : DUMMY_CREDENTIALS) {
//            String[] pieces = credential.split(":");
//            if (pieces[0].equals(mEmail)) {
//                // Account exists, return true if the password matches.
//                return pieces[1].equals(mPassword);
//            }
//        }

        return true;
    }

    private boolean deleteData() {
        
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
//        mAuthTask = null;
//        showProgress(false);
//          Toast.makeText(main_act, data, Toast.LENGTH_LONG).show();
        progress.setVisibility(ProgressBar.GONE);
        if(status!=null){
            if(data.contains("Error report") && data.contains("This request requires HTTP authentication")){
                status.setText("Login Failed, try again.");
            }else{
                status.setText("Post Successful.");
            }
        }
        
        
//        if (success) {
//            finish();
//        } else {
//            mPasswordView.setError(getString(R.string.error_incorrect_password));
//            mPasswordView.requestFocus();
//        }
    }

    @Override
    protected void onCancelled() {
//        mAuthTask = null;
//        showProgress(false);
//    }
    }
}

}