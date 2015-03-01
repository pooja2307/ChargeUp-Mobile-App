package com.example.chargeuplogin;

import java.util.HashMap;

import com.example.chargeuplogin.library.DatabaseHandler;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisteredActivity extends ActionBarActivity {
	
	Button btnBcktologin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registered);
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		HashMap<String, String> user = new HashMap<String, String>();
		user = db.getUserDetails();
//		final TextView fname = (TextView)findViewById(R.id.fname);
//        final TextView lname = (TextView)findViewById(R.id.lname);
        final TextView uname = (TextView)findViewById(R.id.uname);
        final TextView email = (TextView)findViewById(R.id.email);
        //final TextView created_at = (TextView)findViewById(R.id.regat);
        
//        fname.setText(user.get("fname"));//"fname"
//        lname.setText(user.get("lname"));//"lname"
        uname.setText(user.get("uname"));//"uname"
        email.setText(user.get("email"));
        //created_at.setText(user.get("created_at"));
		
		btnBcktologin = (Button)findViewById(R.id.login);
		btnBcktologin.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(v.getContext(),MainActivity.class);
				startActivityForResult(myIntent,0);
				finish();
			}
		});
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registered, menu);
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
