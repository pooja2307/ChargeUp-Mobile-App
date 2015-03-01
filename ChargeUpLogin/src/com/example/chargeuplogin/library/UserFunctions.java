package com.example.chargeuplogin.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	private JSONParser jsonParser;

	// URL of the PHP API
	// http://10.0.2.2/learn2crack_login_api/
	// "http://10.0.2.2/learn2crack_login_api/"
	// "http://gowat.aws.af.cm/"
	// private static String loginURL =
	// "http://10.0.2.2/learn2crack_login_api/";
	
	// local server
//	private static String loginURL = "http://10.0.2.2/chargeup/login.php/";
//	private static String registerURL = "http://10.0.2.2/chargeup/registeruser.php/";	
//	private static String updateURL = "http://10.0.2.2/chargeup/update.php/";
//	private static String forpassURL = "http://10.0.2.2/chargeup/forgetpassword.php/";	
//	private static String chgpassURL = "http://10.0.2.2/learn2crack_login_api/";
	
	// remote server
	private static String loginURL = "http://www.wat-get.tk/login.php/";
	private static String registerURL = "http://www.wat-get.tk/registeruser.php/";	
	private static String updateURL = "http://www.wat-get.tk/updateuserprofile.php/";
	private static String forpassURL = "http://www.wat-get.tk/forgetpassword.php/";	
	private static String chgpassURL = "http://www.wat-get.tk/changepassword.php/";
	private static String registerChargerURL = "http://www.wat-get.tk/registercharger.php/";
	private static String getChargersURL = "http://www.wat-get.tk/getchargers.php/";
	private static String getUserprofileURL="http://www.wat-get.tk/getuserprofile.php/";
	private static String getDevicesURL="http://www.wat-get.tk/getdevices.php/";
	private static String deleteChargerURL="http://www.wat-get.tk/deletecharger.php/";
	// Add Available Charger
	private static String addAvailableChargerURL = "http://www.wat-get.tk/addavailablecharger.php/";
	
	// Remove Available Charger
	private static String removeAvailableChargerURL = "http://www.wat-get.tk/removeavailablecharger.php/";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * Function to Login
	 **/

	public JSONObject loginUser(String username, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
		
	}
	
	public JSONObject getUserprofile(String sessionId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		JSONObject json = jsonParser.getJSONFromUrl(getUserprofileURL, params);
		return json;
	}

	public JSONObject UpdateUser(String sessionId, String avatar, String fname, String lname,
			String bio, String major) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		params.add(new BasicNameValuePair("avatar", avatar));
		params.add(new BasicNameValuePair("firstname", fname));
		params.add(new BasicNameValuePair("lastname", lname));
		params.add(new BasicNameValuePair("bio", bio));
		params.add(new BasicNameValuePair("major", major));
		JSONObject json = jsonParser.getJSONFromUrl(updateURL, params);
		return json;
	}
	
	/**
	 * Function to change password
	 **/

	public JSONObject chgPass(String sessionId, String oldpas, String newpas) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		params.add(new BasicNameValuePair("oldpassword", oldpas));
		params.add(new BasicNameValuePair("newpassword", newpas));
		JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
		return json;
	}

	/**
	 * Function to reset the password
	 **/

	public JSONObject forPass(String username, String email) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("tag", forpass_tag));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("email", email));
		
		// debug
		//System.err.println("jiang,forPass:0: "+params.get(0).getValue());
		//System.err.println("jiang,forPass:1: "+params.get(1).getValue());
		
		
		JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
		return json;
	}

	/**
	 * Function to Register
	 **/
	public JSONObject registerUser(String email,String uname, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("firstname", fname));
//		params.add(new BasicNameValuePair("lastname", lname));
		params.add(new BasicNameValuePair("email", email));
		// params.add(new BasicNameValuePair("uname", uname));
		params.add(new BasicNameValuePair("username", uname));
		params.add(new BasicNameValuePair("password", password));

		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);

		//
		return json;
	}
	
	public JSONObject registerCharger(String sessionId, String deviceId) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		params.add(new BasicNameValuePair("deviceid", deviceId));
		JSONObject json = jsonParser.getJSONFromUrl(registerChargerURL, params);
		return json;
	}
	
	public JSONObject getDevices(String sessionId, String type){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		params.add(new BasicNameValuePair("type", type));
		JSONObject json = jsonParser.getJSONFromUrl(getDevicesURL, params);
		return json;
	}
	
	public JSONObject getChargers(String sessionId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		JSONObject json = jsonParser.getJSONFromUrl(getChargersURL, params);
		return json;
	}
	
	public JSONObject deleteCharger(String sessionId, String chargerId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionId));
		params.add(new BasicNameValuePair("chargerid", chargerId));
		JSONObject json = jsonParser.getJSONFromUrl(deleteChargerURL, params);
		return json;
	}
	
	// add Available Charger
	public JSONObject addAvailableCharger(String sessionid,String chargerid,String longitude,String latitude){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionid));
		params.add(new BasicNameValuePair("chargerid",chargerid));
		params.add(new BasicNameValuePair("longitude",longitude));
		params.add(new BasicNameValuePair("latitude",latitude));
		JSONObject json = jsonParser.getJSONFromUrl(addAvailableChargerURL, params);
		return json;
	}
	
	// remove Available Charger
	public JSONObject removeAvailableCharger(String sessionid,String chargerid){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sessionid", sessionid));
		params.add(new BasicNameValuePair("chargerid", chargerid));
		JSONObject json = jsonParser.getJSONFromUrl(removeAvailableChargerURL, params);
		return json;
	}
	
	/**
	 * Function to logout user Resets the temporary data stored in SQLite
	 * Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

}
