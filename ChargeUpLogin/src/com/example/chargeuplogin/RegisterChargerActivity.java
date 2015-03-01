package com.example.chargeuplogin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;

public class RegisterChargerActivity extends ActionBarActivity {
	
	Button btnCancel_RegisterCh;
	Button btnRegisterCh;
	EditText deviceid;
	TextView errormsg_registerCh;
	
	private Spinner mySpinner1;	
	private ArrayAdapter<String> myAdapter1;
	
	private Spinner mySpinner2;	
	private ArrayAdapter<String> myAdapter2;
	
	private Spinner mySpinner3;	
	private ArrayAdapter<String> myAdapter3;
	
	private JSONArray devices;
	
	String deviceId = "-1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_charger);
		
		btnCancel_RegisterCh = (Button)findViewById(R.id.btnCancel_RegisterCh);
		btnRegisterCh = (Button)findViewById(R.id.btnRegisterCh);
//		deviceid = (EditText)findViewById(R.id.deviceid_registerCh_e);
		errormsg_registerCh = (TextView)findViewById(R.id.errormsg_registerCh);
		
		mySpinner1 = (Spinner)findViewById(R.id.spinner1);
		myAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		mySpinner1.setAdapter(myAdapter1);
		
		mySpinner2 = (Spinner)findViewById(R.id.spinner2);
		myAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		mySpinner2.setAdapter(myAdapter2);
		final Map<String,ArrayList<Integer>> map2 = new HashMap<String,ArrayList<Integer>>();
		
		mySpinner3 = (Spinner)findViewById(R.id.spinner3);
		myAdapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);		
		mySpinner3.setAdapter(myAdapter3);
		final Map<String,Integer> map3 = new HashMap<String,Integer>();
		
		// get devices from server
		new ProcessGetDevices().execute();				
				
		
		mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String str = (String) parent.getAdapter().getItem(position);
				String type="";
				if(str.equals("smartphone")){
					type="1";
				}else if(str.equals("tablet")){
					type="2";
				}else if(str.equals("laptop")){
					type="3";
				}else{
					type="";
				}	
				
				map2.clear();
    			for(int i=0; i<devices.length(); i++){
    				try {
						JSONObject dev = (JSONObject)devices.get(i);
						if(dev.getString("type").equals(type)){
							String tmp = dev.getString("brand");
							if(map2.containsKey(tmp)){
								ArrayList<Integer> tmp_value = map2.get(tmp);
								tmp_value.add(i);
								map2.put(tmp, tmp_value);
							}else{
								ArrayList<Integer> tmp_value = new ArrayList<Integer>();
								tmp_value.add(i);
								map2.put(tmp, tmp_value);
							}
						}						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
//    			myAdapter2.clear();
//    			myAdapter2.add("choose a brand");
//    			myAdapter2.notifyDataSetChanged();
    			myAdapter2.clear();
    			myAdapter2.add("choose a brand");
    			for(Entry<String,ArrayList<Integer>>entry:map2.entrySet()){
    				String key = entry.getKey();
    				myAdapter2.add(key);
    			}
    			myAdapter2.notifyDataSetChanged();
    			mySpinner2.setSelection(0);
    			mySpinner3.setSelection(0);
    			
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				String brand = (String) parent.getAdapter().getItem(position);
				
				map3.clear();
				if(brand.equals("choose a brand")){
					;
				}else{
					ArrayList<Integer> model = map2.get(brand);
//					myAdapter3.clear();
//					myAdapter3.add("choose a model");
//					myAdapter3.notifyDataSetChanged();
					myAdapter3.clear();
					myAdapter3.add("choose a model");
					for(int i=0; i<model.size(); i++){
						int idx = model.get(i);
						
						JSONObject dev;
						try {
							dev = (JSONObject)devices.get(idx);
							String model_i = dev.getString("model");
							myAdapter3.add(model_i);
							//System.err.println("jiang: "+model_i+", "+idx);
							map3.put(model_i, idx);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
					myAdapter3.notifyDataSetChanged();
					mySpinner3.setSelection(0); // this line is super important
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				String model = (String) parent.getAdapter().getItem(position);
				if(model.equals("choose a model")){
					//errormsg_registerCh.setText("");
					deviceId = "-1";
				}else{
					if(map3.containsKey(model)){
						int idx = map3.get(model);
						try {
							JSONObject dev = (JSONObject)devices.get(idx);
							deviceId = dev.getString("deviceid");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						deviceId = map3.get(model).toString();
						//errormsg_registerCh.setText(map3.get(model).toString());
					}
					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub				
			}
			
		});
		
		
		btnCancel_RegisterCh.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
		
		btnRegisterCh.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//NetAsync(v);
				new ProcessRegisterCh().execute();
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
            nDialog = new ProgressDialog(RegisterChargerActivity.this);
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
                new ProcessRegisterCh().execute();
            }
            else{
                nDialog.dismiss();
                errormsg_registerCh.setText("Error in Network Connection");
            }
        }
    }*/

    
    // Async Task to get and send data to My Sql database through JSON respone.    
    private class ProcessRegisterCh extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        String sessionId;//, deviceId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            //deviceId = deviceid.getText().toString();
            
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            HashMap<String,String> user = new HashMap<String, String>(); //////
            user = db.getUserDetails();
            sessionId = user.get("uid");
            
            pDialog = new ProgressDialog(RegisterChargerActivity.this);
//            pDialog.setTitle("Contacting Servers");
//            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
//            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.registerCharger(sessionId, deviceId); //////////
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
            			//errormsg_registerCh.setText("Charger is successfully registered!");
            			Toast.makeText(getApplicationContext(),"Charger is successfully registered!",Toast.LENGTH_SHORT).show();
            			
            		}else{
            			pDialog.dismiss();
//            			errormsg_registerCh.setText(json.getString("error"));
            			Toast.makeText(getApplicationContext(),json.getString("error"),Toast.LENGTH_SHORT).show();
            		}
            	}
            	else{
                    pDialog.dismiss();
//                    errormsg_registerCh.setText("Error occured in regestering a charger");
                    Toast.makeText(getApplicationContext(),"Error occured in regestering a charger",Toast.LENGTH_SHORT).show();
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
            
            pDialog = new ProgressDialog(RegisterChargerActivity.this);
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
            			
            			errormsg_registerCh.setText("get devices successfully!");
            			
//            			//myAdapter1.clear();
//            			// define it as global variable
//            			//JSONArray devices= json.getJSONArray("devices");
            			devices = json.getJSONArray("devices");
//            			for(int i=0; i<devices.length(); i++){
//            				JSONObject dev = (JSONObject)devices.get(i);            				
//            				//myAdapter1.add("deviceid: "+dev.getString("deviceid"));
//            			}
//            			//myAdapter1.notifyDataSetChanged();
            			
            			myAdapter1.clear();
            			myAdapter1.add("choose a type");
            			myAdapter1.add("smartphone");
            			myAdapter1.add("tablet");
            			myAdapter1.add("laptop");            			
            			myAdapter1.notifyDataSetChanged();
            			
            			myAdapter2.clear();
            			myAdapter2.add("choose a brand");
            			myAdapter2.notifyDataSetChanged();
            			
            			myAdapter3.clear();
            			myAdapter3.add("choose a model");
            			myAdapter3.notifyDataSetChanged();
            			
            			
            		}else{
            			pDialog.dismiss();
            			errormsg_registerCh.setText(json.getString("error"));
            		}
            	}
            	else{
                    pDialog.dismiss();
                    errormsg_registerCh.setText("Error occured in Update");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
}
