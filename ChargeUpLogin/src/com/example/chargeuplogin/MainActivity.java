package com.example.chargeuplogin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.chargeuplogin.library.DatabaseHandler;
import com.example.chargeuplogin.library.UserFunctions;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	Button btnRegister;
	Button btnLogin;
	Button btnResetPassword;
	EditText inputUsername;
	EditText inputPassword;	
	private TextView loginErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inputUsername = (EditText)findViewById(R.id.username);
        inputPassword = (EditText)findViewById(R.id.password);
        loginErrorMsg = (TextView)findViewById(R.id.loginErrorMsg);        
        btnRegister = (Button)findViewById(R.id.register);
        btnLogin = (Button)findViewById(R.id.login);
        btnResetPassword = (Button)findViewById(R.id.resetpassword);        
        
        btnRegister.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(),RegisterActivity.class);
				startActivityForResult(myIntent,0);
				finish();
			}
		});
        
        
        btnLogin.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (  ( !inputUsername.getText().toString().equals("")) && 
					  ( !inputPassword.getText().toString().equals("")) )
                {
					new ProcessLogin().execute();
                }
                else if ( ( !inputUsername.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Username and Password field are empty", Toast.LENGTH_SHORT).show();
                }
			}
		});
        
        // forget password, then reset the password
        btnResetPassword.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(),ResetpasswordActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
        
    }
    
    
    /*
    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        // Gets current device state and checks for working Internet connection by trying Google.
        
        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessLogin().execute();
            }
            else{
                nDialog.dismiss();
                loginErrorMsg.setText("Error in Network Connection");
            }
        }
    }
    */

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String username,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.password);
            
            username = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            
            //System.out.println("jiang: "+username+", "+password);
            
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(username, password);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	if (json.getString("status") != null){
            		String status = json.getString("status"); 
            		if(status.equals("success")){
            			pDialog.setMessage("Loading User Space");
            			pDialog.setTitle("Getting Data");            			
            			
            			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());  
                        
                        //{"sessionid":"123456","user":{"username":"abcdabcd","email":"abb@bbb.ccc","firstname":"","lastname":""},"status":"success"}
                        JSONObject user = (JSONObject)json.getJSONObject("user");
                        
                        db.addUser("",//user.getString("firstname")
                        		   "",//user.getString("lastname")
                        		   user.getString("email"),
                        		   user.getString("username"),
                        		   json.getString("sessionid"),
                        		   "");
                        
                        // firstname, lastname, email, username, sessionid, created_at
//                        db.addUser("","", "",username,json.getString("sessionid"),"");
            			
                        //System.err.println("sessionid: "+json.getString("sessionid"));
                        pDialog.dismiss();
            			Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
            			//upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);            			
            			startActivity(myIntent);
            			finish();
            		}else{
            			pDialog.dismiss();
            			loginErrorMsg.setText("Incorrect username/password");
            		}
            	}
            	else{
                    pDialog.dismiss();
                    loginErrorMsg.setText("Error occured in login");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
//    public void NetAsync(View view){
//        new NetCheck().execute();
//    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
