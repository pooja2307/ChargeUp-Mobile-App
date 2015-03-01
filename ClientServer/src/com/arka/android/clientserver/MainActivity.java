package com.arka.android.clientserver;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

   private Spinner chargerField;
   private TextView status,tv;

   @Override 
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      chargerField = (Spinner)findViewById(R.id.spinner1);
      String[] items = new String[]{"Apple","Android","Windows"};
	  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
	  chargerField.setAdapter(adapter);
	  status = (TextView)findViewById(R.id.textView6);
	  tv = (TextView)findViewById(R.id.textView5);
   }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }
   public void FindCharger(View view){
      String chargertype = chargerField.getSelectedItem().toString();
      new SigninActivity(this,status).execute(chargertype);
      tv.setText(chargertype);
   }
}