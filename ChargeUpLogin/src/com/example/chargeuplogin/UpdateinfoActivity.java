package com.example.chargeuplogin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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

public class UpdateinfoActivity extends ActionBarActivity {
	
	EditText firstName;
	EditText lastName;
	EditText avatar;
	EditText bio;
	EditText major;
	Button btnUpdate;
	Button btnCancel;
	TextView updateErrorMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updateinfo);
		
		firstName = (EditText)findViewById(R.id.fname_update_e);
		lastName  = (EditText)findViewById(R.id.lname_update_e);
		avatar = (EditText)findViewById(R.id.avatar_update_e);
		bio = (EditText)findViewById(R.id.bio_update_e);
		major = (EditText)findViewById(R.id.major_update_e);
		btnUpdate = (Button)findViewById(R.id.btnUpdate_update);
		btnCancel = (Button)findViewById(R.id.btnCancel_update);
		updateErrorMsg = (TextView)findViewById(R.id.errormsg_update);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(),LoginActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
		
		GetUserprofile();
		
		btnUpdate.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//NetAsync(v);
				new ProcessUpdate().execute();
			}
		});
	}
	// end of onCreate()
	
	
    /*private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(UpdateinfoActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        //Gets current device state and checks for working Internet connection by trying Google.        
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
                new ProcessUpdate().execute();
            }
            else{
                nDialog.dismiss();
                updateErrorMsg.setText("Error in Network Connection");
            }
        }
    }
    */

    
    // Async Task to get and send data to My Sql database through JSON respone.    
    private class ProcessUpdate extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String sessionId, fName, lName, str_avatar, str_bio, str_major;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            firstName = (EditText)findViewById(R.id.fname_update_e);
            fName = firstName.getText().toString();
            
            lastName  = (EditText)findViewById(R.id.lname_update_e);
            lName = lastName.getText().toString();
            
            str_avatar = avatar.getText().toString();
            str_bio = bio.getText().toString();
            str_major = major.getText().toString();
            
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            HashMap<String,String> user = new HashMap<String, String>(); //////
            user = db.getUserDetails();
            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(UpdateinfoActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.UpdateUser(sessionId,str_avatar, fName, lName,str_bio,str_major); //////////
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	if (json.getString("status") != null){
            		String status = json.getString("status"); 
            		if(status.equals("success")){
            			//pDialog.setMessage("Loading...");
            			//pDialog.setTitle("Getting Data");
            			
            			pDialog.dismiss();
            			//updateErrorMsg.setText("Update Success\nNew Info:\n"+"First name:"+fName+"\nLast name:"+lName);
            			updateErrorMsg.setText("Update Success");
            		}else{
            			pDialog.dismiss();
            			updateErrorMsg.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    updateErrorMsg.setText("Error occured in Update");
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
		getMenuInflater().inflate(R.menu.updateinfo, menu);
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
    
    public void GetUserprofile(){
        new process_getUserprofile().execute();
    }
	
	private class process_getUserprofile extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

        String sessionId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            HashMap<String,String> user = new HashMap<String, String>(); //////
            user = db.getUserDetails();
            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(UpdateinfoActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.getUserprofile(sessionId); //////////
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	if (json.getString("status") != null){
            		String status = json.getString("status"); 
            		if(status.equals("success")){
            			//pDialog.setMessage("Loading...");
            			//pDialog.setTitle("Getting Data");            			
            			pDialog.dismiss();
            			
            			JSONObject userprofile = (JSONObject)json.getJSONObject("userprofile");
            			firstName.setText(userprofile.getString("firstname"));
            			lastName.setText(userprofile.getString("lastname"));
            			avatar.setText(userprofile.getString("avatar"));
            			bio.setText(userprofile.getString("bio"));
            			major.setText(userprofile.getString("major"));
            			
            		}else{
            			pDialog.dismiss();
//            			errormsg.setText(json.getString("error"));
            			Toast.makeText(getApplicationContext(),json.getString("error"),
            					Toast.LENGTH_SHORT).show();
            		}
            	}
            	else{
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Error occured in geting user's profile",
        					Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
	}
}
