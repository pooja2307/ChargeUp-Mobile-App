package com.example.chargeuplogin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.chargeuplogin.library.DatabaseHandler;
import com.example.chargeuplogin.library.UserFunctions;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	Button btnBacktologin;
	Button btnRegister;
	EditText inputUsername;
	EditText inputPassword;
	EditText inputPassword2;
	EditText inputEmail;
	EditText inputEmail2;
	TextView registerErrorMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		inputUsername = (EditText) findViewById(R.id.uname);
		inputPassword = (EditText) findViewById(R.id.pword);
		inputPassword2 = (EditText) findViewById(R.id.repeat_ps_e);
		inputEmail = (EditText) findViewById(R.id.email);
		inputEmail2 = (EditText) findViewById(R.id.repeat_email_e);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		btnBacktologin = (Button) findViewById(R.id.bktologin);
		btnBacktologin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(), MainActivity.class);
				startActivityForResult(myIntent, 0);
				finish();
			}
		});

		btnRegister = (Button) findViewById(R.id.Register);

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((!inputUsername.getText().toString().equals(""))
						&& (!inputPassword.getText().toString().equals(""))
						&& (!inputPassword2.getText().toString().equals(""))
						&& (!inputEmail.getText().toString().equals(""))
						&& (!inputEmail2.getText().toString().equals(""))) {
					if (inputUsername.getText().toString().length() >= 7
							&& inputUsername.getText().toString().length() <= 15
							&& inputPassword.getText().toString().equals(inputPassword2.getText().toString())
							&& inputEmail.getText().toString().equals(inputEmail2.getText().toString())) {
						NetAsync(v);
//						new ProcessRegister().execute();
					} else {
						if (!inputPassword.getText().toString().equals(inputPassword2.getText().toString())) {
							Toast.makeText(getApplicationContext(),
									"two password are not equal",Toast.LENGTH_SHORT).show();
						} else if (!inputEmail.getText().toString().equals(inputEmail2.getText().toString())) {
							Toast.makeText(getApplicationContext(),
									"two email are not equal",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"Username should be at least 7 and at most 15 characters",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"username,password, and email cannot be empty",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private class NetCheck extends AsyncTask<String, String, Boolean> {
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(RegisterActivity.this);
			nDialog.setMessage("Loading..");
			nDialog.setTitle("Checking Network");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {

			//Gets current device state and checks for working Internet
			//connection by trying Google.
			 
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
				registerErrorMsg.setText("Error in Network Connection");
			}
		}
	}

	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

		/**
		 * Defining Process dialog
		 **/
		private ProgressDialog pDialog;

		String email, password, fname = "", lname = "", uname;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			inputUsername = (EditText) findViewById(R.id.uname);
			inputPassword = (EditText) findViewById(R.id.pword);
			email = inputEmail.getText().toString();
			uname = inputUsername.getText().toString();
			password = inputPassword.getText().toString();

			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Registering ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {

			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.registerUser(email,uname, password);
			return json;

		}

		@Override
		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks for success message.
			 **/
			try {
				if (json.getString("status") != null) {
					String status = json.getString("status");
					if (status.equals("success")) {
						
						pDialog.setTitle("Getting Data");
						pDialog.setMessage("Loading Info");
						registerErrorMsg.setText("Successfully Registered");

						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());

						
						db.addUser(fname, lname, email, uname, "", "");
						pDialog.dismiss();

						Intent registered = new Intent(getApplicationContext(),
								RegisteredActivity.class);
						registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(registered);
						finish();
					} else {// status.equals("error")
						pDialog.dismiss();
						registerErrorMsg.setText(json.getString("error"));
					}
				} else {
					pDialog.dismiss();
					registerErrorMsg.setText("Error occured in registration");
				}

			} catch (JSONException e) {
				e.printStackTrace();

			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
