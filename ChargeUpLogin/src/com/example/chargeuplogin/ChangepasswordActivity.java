package com.example.chargeuplogin;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.chargeuplogin.library.DatabaseHandler;
import com.example.chargeuplogin.library.UserFunctions;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;

public class ChangepasswordActivity extends ActionBarActivity {
	
	Button btnGoback_chpass;
	Button btnChangePassword;
	EditText oldps_e;
	EditText newps_e;
	EditText newps2_e;
	TextView errormsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);
		
		oldps_e = (EditText)findViewById(R.id.oldps_e);
		newps_e = (EditText)findViewById(R.id.newps_e);
		newps2_e= (EditText)findViewById(R.id.newps2_e);
		
		btnGoback_chpass = (Button)findViewById(R.id.btnGoback_chpass);
		btnChangePassword = (Button)findViewById(R.id.btnChpassword_chpassActivity);
		errormsg = (TextView)findViewById(R.id.errmsg_chPs);
		
		btnGoback_chpass.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);
                finish();
			}
		});
		
		btnChangePassword.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!oldps_e.getText().toString().isEmpty()&&
				   !newps_e.getText().toString().isEmpty()&&
				   !newps2_e.getText().toString().isEmpty()){
					if(!newps_e.getText().toString().equals(newps2_e.getText().toString())){
						errormsg.setText("error: you entered two different new passwords");
					}else{
						changePassword();
					}					
				}else{
					Toast.makeText(getApplicationContext(),"one or more fileds are empty", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	public void changePassword(){
        new process_changePassword().execute();
    }
	
	private class process_changePassword extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

        String sessionId, oldPs, newPs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            oldPs = oldps_e.getText().toString();
            newPs = newps_e.getText().toString();
            
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            HashMap<String,String> user = new HashMap<String, String>(); //////
            user = db.getUserDetails();
            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(ChangepasswordActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.chgPass(sessionId, oldPs, newPs); //////////
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
            			
            			//errormsg.setText("Password is successfully changed");
            			String str = "Password is successfully changed";
            			Toast.makeText(getApplicationContext(),str, Toast.LENGTH_SHORT).show();
            			Intent myIntent = new Intent(getApplicationContext(),LoginActivity.class);
            			startActivity(myIntent);
            			finish();
            			
            		}else{
            			pDialog.dismiss();
            			errormsg.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    errormsg.setText("Error occured in changing password");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.changepassword, menu);
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
