package com.example.chargeuplogin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ResetpasswordActivity extends ActionBarActivity {

	EditText userName;
	EditText emailAddr;
	Button btnReset;
	Button btnCancel;
	TextView alert_reset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);

		userName = (EditText) findViewById(R.id.username_reset_e);
		emailAddr = (EditText) findViewById(R.id.email_reset_e);
		btnReset = (Button) findViewById(R.id.btnReset_reset);
		btnCancel = (Button) findViewById(R.id.btnCancel_reset);
		alert_reset = (TextView)findViewById(R.id.alert_reset);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(), MainActivity.class);
				startActivity(myIntent);
				finish();
			}
		});

		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!userName.getText().toString().equals("")&&!emailAddr.getText().toString().equals("")){
					NetAsync(v);
				}else{
					Toast.makeText(getApplicationContext(),
							"username and email cannot be empty",Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}

	private class NetCheck extends AsyncTask<String, String, Boolean>

	{
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(ResetpasswordActivity.this);
			nDialog.setMessage("Loading..");
			nDialog.setTitle("Checking Network");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				try {
					URL url = new URL("http://www.google.com");
					HttpURLConnection urlc = (HttpURLConnection) url
							.openConnection();
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
		protected void onPostExecute(Boolean th) {

			if (th == true) {
				nDialog.dismiss();
				new ProcessRegister().execute();
			} else {
				nDialog.dismiss();
				alert_reset.setText("Error in Network Connection");
			}
		}
	}

	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

		private ProgressDialog pDialog;

		String uname,email;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			userName = (EditText) findViewById(R.id.username_reset_e);
			emailAddr = (EditText) findViewById(R.id.email_reset_e);
			
			uname = userName.getText().toString();
			email = emailAddr.getText().toString();


			pDialog = new ProgressDialog(ResetpasswordActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			System.out.println("call userFunction.forPass()");
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.forPass(uname,email);
			return json;

		}

		@Override
		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks if the Password Change Process is sucesss
			 **/
			try {
				if (json.getString("status") != null) {
					String status = json.getString("status");
					if(status.equals("success")){
						pDialog.dismiss();
						alert_reset.setText("A recovery email is sent to you, see it for more details.");
					}else if (status.equals("error")) {
						pDialog.dismiss();
						alert_reset.setText(json.getString("error"));
					} else {
						pDialog.dismiss();
						alert_reset.setText("Error occured in reseting Password");
					}
				}
			} catch (JSONException e) {
				pDialog.dismiss();
				alert_reset.setText("json null exception");
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.resetpassword, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */
}
