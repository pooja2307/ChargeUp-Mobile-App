package com.example.chargeuplogin;

import java.util.HashMap;

import org.json.JSONArray;
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

public class AddAvailableChargerActivity extends ActionBarActivity {
	
	String sessionid,chargerid,longitude,latitude;
	String chargerid2;
	
	Button btnCancel;
	Button btnAdd;
	TextView availableId;
	EditText inputID;
	EditText inputLogitude;
	EditText inputLatitude;
	EditText inputID2;
	Button btnRemove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_available_charger);
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String,String> user = new HashMap<String, String>(); //////
        user = db.getUserDetails();
        sessionid = user.get("uid");
        
        btnAdd = (Button)findViewById(R.id.addCh);
        btnCancel = (Button)findViewById(R.id.cancelCh);
        availableId = (TextView)findViewById(R.id.chargerid_);
        inputID = (EditText)findViewById(R.id.chargerid_add_e);
        inputLogitude = (EditText)findViewById(R.id.log_add_e);
        inputLatitude = (EditText)findViewById(R.id.lat_add_e);
        inputID2 = (EditText)findViewById(R.id.chargerid2_add_e);
        btnRemove = (Button)findViewById(R.id.removeCh);
        
        getChargers();
        
        btnCancel.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				Intent myIntent = new Intent(v.getContext(),LoginActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
        
        btnAdd.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addAvailableCharger();
			}
		});
        
        btnRemove.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				removeAvailableCharger();
			}
		});
	}
	
	// add available charger
	public void addAvailableCharger(){
		new ProcessAddAvailableCharger().execute();
	}
	private class ProcessAddAvailableCharger extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            chargerid = inputID.getText().toString();
            longitude = inputLogitude.getText().toString();
            latitude  = inputLatitude.getText().toString();
            
            pDialog = new ProgressDialog(AddAvailableChargerActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.addAvailableCharger(sessionid,chargerid,longitude,latitude);
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
            			Toast.makeText(getApplicationContext(), 
            					"add charger to availableCharger successfully", Toast.LENGTH_SHORT).show();            			
            		}else{
            			pDialog.dismiss();
            			Toast.makeText(getApplicationContext(), 
            					json.getString("error"), Toast.LENGTH_SHORT).show();
            		}
            	}
            	else{
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), 
                    		"Error occured in adding a charger to availableCharger", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
	
	public void getChargers(){
        new process_getChargers().execute();
    }	
	private class process_getChargers extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();            
            pDialog = new ProgressDialog(AddAvailableChargerActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.getChargers(sessionid); //////////
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
            			
            			JSONArray chargers = json.getJSONArray("chargers");
            			//{"chargers":[{"chargerid":"15","deviceid":"1"},{"chargerid":"16","deviceid":"1"}],"status":"success"}
            			
            			for(int i=0; i<chargers.length(); i++){
            				JSONObject ch = (JSONObject)chargers.get(i);
            				availableId.append(ch.getString("chargerid")+" ");
            			}
            			
            			//errormsg_getChargers.setText("You have "+chargers.length()+" kinds of Chargers");
            			
            		}else{
            			pDialog.dismiss();
            			//errormsg_getChargers.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    //errormsg_getChargers.setText("Error occured in getChargers");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
	}
	
	// remove available charger
	public void removeAvailableCharger(){
			new ProcessRemoveAvailableCharger().execute();
		}
		private class ProcessRemoveAvailableCharger extends AsyncTask<String, String, JSONObject> {

	        private ProgressDialog pDialog;
	        
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            
	            chargerid2 = inputID2.getText().toString();
	            
	            pDialog = new ProgressDialog(AddAvailableChargerActivity.this);
	            //pDialog.setTitle("Contacting Servers");
	            //pDialog.setMessage("Logging in ...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            //pDialog.show();
	        }

	        @Override
	        protected JSONObject doInBackground(String... args) {

	            UserFunctions userFunction = new UserFunctions();
	            JSONObject json = userFunction.removeAvailableCharger(sessionid,chargerid2);
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
	            			Toast.makeText(getApplicationContext(), 
	            					"remove charger from availableCharger list successfully", Toast.LENGTH_SHORT).show();            			
	            		}else{
	            			pDialog.dismiss();
	            			Toast.makeText(getApplicationContext(), 
	            					json.getString("error"), Toast.LENGTH_SHORT).show();
	            		}
	            	}
	            	else{
	                    pDialog.dismiss();
	                    Toast.makeText(getApplicationContext(), 
	                    		"Error occured in removing a charger from availableCharger", Toast.LENGTH_SHORT).show();
	                }
	            }
	            catch (JSONException e) {
	                e.printStackTrace();
	            }
	       }
	    }
	
	
}
