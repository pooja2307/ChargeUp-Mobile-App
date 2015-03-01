package com.example.chargeuplogin;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.chargeuplogin.library.DatabaseHandler;
import com.example.chargeuplogin.library.UserFunctions;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.Toast;
//import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;

public class DeleteChargerActivity extends ActionBarActivity {
	
	TextView errormsg_getChargers;
	Button btnCancel_deleteCh;
	ListView chargersListView;
	private ArrayAdapter<String> chargersAdapter;
	
	private JSONArray devices;
	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer,String> chargersid = new HashMap<Integer,String>();
	String sessionId,chargerId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_charger);
		
		errormsg_getChargers = (TextView)findViewById(R.id.errormsg_getChargers);
		btnCancel_deleteCh = (Button)findViewById(R.id.btnCancel_deleteCh);
		chargersListView = (ListView)findViewById(R.id.chargesListView);
		chargersAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		chargersListView.setAdapter(chargersAdapter);
		
		
		
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String,String> user = new HashMap<String, String>(); //////
        user = db.getUserDetails();
        sessionId = user.get("uid");
		
		// get devices from server
		new ProcessGetDevices().execute();
		
		// Get Chargers
		getChargers();
		
		//
		chargersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				// delete selected item
				chargerId = chargersid.get(position);
				new ProcessDeleteCharger().execute();
				
				// update after deleting a charger
				getChargers();
			}
		});		
		

		btnCancel_deleteCh.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(),LoginActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
		
		
		
	}
	
	public void getChargers(){
        new process_getChargers().execute();
    }
	
	private class process_getChargers extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

//        String sessionId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
//            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
//            HashMap<String,String> user = new HashMap<String, String>(); //////
//            user = db.getUserDetails();
//            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(DeleteChargerActivity.this);
            //pDialog.setTitle("Contacting Servers");
            //pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.getChargers(sessionId); //////////
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
            			
            			
            			HashMap<String,Integer> map = new HashMap<String,Integer>();
            			for(int i=0; i<devices.length(); i++){
            				JSONObject dev = (JSONObject)devices.get(i);
            				String key = dev.getString("deviceid");
            				if(!map.containsKey(key)){
            					map.put(key, i);
            				}
            			}
            			
            			chargersAdapter.clear();
            			chargersid.clear();
            			for(int i=0; i<chargers.length(); i++){
            				JSONObject ch = (JSONObject)chargers.get(i);
            				if(!chargersid.containsKey(i)){
            					chargersid.put(i, ch.getString("chargerid"));
            				}
            				//System.err.println("ch "+i+": "+ch.getString("deviceid"));
            				int idx = map.get(ch.getString("deviceid"));
            				JSONObject dev = (JSONObject)devices.get(idx);
            				String type;
            				if(dev.getString("type").equals("1")){
            					type = "smartphone";
            				}else if(dev.getString("type").equals("2")){
            					type = "tablet";
            				}else{
            					type = "laptop";
            				}
            				chargersAdapter.add("Charger "+(i+1)+": \n"+type+","+
            						dev.getString("brand")+","+dev.getString("model"));
//            				chargersAdapter.add("charger "+i+": "+ch.getString("deviceid"));
            				chargersAdapter.notifyDataSetChanged();
            			}
            			
            			errormsg_getChargers.setText("You have "+chargers.length()+" kinds of Chargers");
            			
            		}else{
            			pDialog.dismiss();
            			errormsg_getChargers.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    errormsg_getChargers.setText("Error occured in getChargers");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
	}
	
	
    // getDevices
    private class ProcessGetDevices extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        String sessionId, type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            type = "0";
            
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            HashMap<String,String> user = new HashMap<String, String>(); //////
            user = db.getUserDetails();
            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(DeleteChargerActivity.this);
//            pDialog.setTitle("Contacting Servers");
//            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
//            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.getDevices(sessionId, type); //////////
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	if (json.getString("status") != null){
            		String status = json.getString("status"); 
            		if(status.equals("success")){
            			pDialog.setMessage("Loading...");
            			pDialog.setTitle("Getting Data");
            			
            			pDialog.dismiss();
            			
            			errormsg_getChargers.setText("get devices successfully!");
            			
            			devices = json.getJSONArray("devices");
            			
            			
            			
            		}else{
            			pDialog.dismiss();
            			errormsg_getChargers.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    errormsg_getChargers.setText("Error occured in getting devices");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
    
    // delete charger
    private class ProcessDeleteCharger extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
//            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
//            HashMap<String,String> user = new HashMap<String, String>(); //////
//            user = db.getUserDetails();
//            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(DeleteChargerActivity.this);
//            pDialog.setTitle("Contacting Servers");
//            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
//            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.deleteCharger(sessionId, chargerId);//////////
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	if (json.getString("status") != null){
            		String status = json.getString("status"); 
            		if(status.equals("success")){
//            			pDialog.setMessage("Loading...");
//            			pDialog.setTitle("Getting Data");
            			
            			pDialog.dismiss();
            			Toast.makeText(getApplicationContext(), "delete one charger successfully", Toast.LENGTH_SHORT).show();
            			
            		}else{
            			pDialog.dismiss();
            			Toast.makeText(getApplicationContext(), json.getString("error"), Toast.LENGTH_SHORT).show();
            		}
            	}
            	else{
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error occured in deleting a charger", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
}
