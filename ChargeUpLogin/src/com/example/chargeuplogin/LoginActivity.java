package com.example.chargeuplogin;


import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.chargeuplogin.library.DatabaseHandler;
import com.example.chargeuplogin.library.UserFunctions;

import android.support.v7.app.ActionBarActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {
	
	Button btnLogout;
    Button changepas;
    Button btnUpdateinfo;
    Button btnRegisterCharger;
    Button btnDeleteCharger;
    Button btn_myaccount;
    Button btn_addtolist;
    TextView userPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		changepas = (Button) findViewById(R.id.btchangepass);
        btnLogout = (Button) findViewById(R.id.logout);
        btnUpdateinfo = (Button)findViewById(R.id.updateUserinfo);
        btnRegisterCharger = (Button)findViewById(R.id.btnRegisterCharger);
        btnDeleteCharger = (Button)findViewById(R.id.btnDeleteCharger);
        btn_myaccount = (Button)findViewById(R.id.myaccount);
        btn_addtolist = (Button)findViewById(R.id.addtolist);
        userPanel = (TextView)findViewById(R.id.userPanel);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String,String> user = new HashMap<String, String>(); //////
        user = db.getUserDetails();
        
        final TextView login = (TextView) findViewById(R.id.textwelcome);
//        if(user.get("lname")!=""&&user.get("fname")!=""){
        if(!user.get("lname").isEmpty()&&!user.get("fname").isEmpty()){
        	login.setText("Welcome: "+user.get("lname")+", "+user.get("fname")+"\nYou successfully login");
        }else{
        	login.setText("Welcome: "+user.get("uname")+"\nYou successfully login");
        }
        //login.setText("Welcome, you successfully login");

        
        //String sessionid = user.get("uid");
//        final TextView tmp = (TextView)findViewById(R.id.textView);
//        tmp.setText(user.get("uid"));
        
        btnLogout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserFunctions logout = new UserFunctions();
                logout.logoutUser(getApplicationContext());
                
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
			}
		});
        
        changepas.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				Intent myIntent = new Intent(getApplicationContext(), ChangepasswordActivity.class);
                startActivity(myIntent);
                finish();
			}
		});
        
        // update info: firstname + lastname
        btnUpdateinfo.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(),UpdateinfoActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
				finish();
			}
		});
        
        // Register charger
        btnRegisterCharger.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(),RegisterChargerActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
        
        // get chargers, then delete the charger from the list
        btnDeleteCharger.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(),DeleteChargerActivity.class);
				startActivity(myIntent);
				finish();				
			}
		});
        
        btn_myaccount.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetUserprofile();
			}
		});
        
        btn_addtolist.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(),AddAvailableChargerActivity.class);
				startActivity(myIntent);
				finish();				
			}
		});
        
	}
	// end of onCreate()
	
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
            
            pDialog = new ProgressDialog(LoginActivity.this);
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
            			
            			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            	        HashMap<String,String> user = new HashMap<String, String>(); //////
            	        user = db.getUserDetails();
            			
            			JSONObject userprofile = (JSONObject)json.getJSONObject("userprofile");
            			userPanel.setText("");
            			userPanel.setText("User's Info"+
            					          "\nUsername:\t"+user.get("uname")+
            					          "\nEmail:\t"+user.get("email")+
            					          "\nAvatar:\t"+userprofile.getString("avatar")+
            			                  "\nFirst name:\t"+userprofile.getString("firstname")+
            			                  "\nLast name:\t"+userprofile.getString("lastname")+
            			                  "\nBio:\t"+userprofile.getString("bio")+
            			                  "\nMajor:\t"+userprofile.getString("major")+
            			                  "\nScore:\t"+userprofile.getString("score"));
            			
            			
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
