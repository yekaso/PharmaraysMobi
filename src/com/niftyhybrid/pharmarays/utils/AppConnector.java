package com.niftyhybrid.pharmarays.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class AppConnector {
	private final static String ipAddress = "192.168.1.6/Pharmarays";

	// private final static String ipAddress = "www.pharmarays.com";

	// private final static String ipAddress = "10.0.2.2/Pharmarays";

	public static JSONArray login(ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/loginauthorization";

		return connectToServer(nameValuePairs, url);

	}

	public static JSONArray assignPharmacy(
			ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/assignpharmacy";

		return connectToServer(nameValuePairs, url);
	}

	public static JSONArray registerUser(ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/registernewuser";

		return connectToServer(nameValuePairs, url);
	}

	public static JSONArray updateDrugAvailability(
			ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/processdrugavailability";

		return connectToServer(nameValuePairs, url);
	}

	public static JSONArray retrieveDrugs(
			ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/retrieve_drugs";

		return connectToServer(nameValuePairs, url);

	}

	public static JSONArray retrievePharmacy(
			ArrayList<NameValuePair> nameValuePairs) {
		String url = "/pharma-public/index.php/mobile/mobile_controller/retrieve_pharmacy";

		return connectToServer(nameValuePairs, url);
	}

	public static JSONArray connectToServer(
			ArrayList<NameValuePair> nameValuePairs, String url) {
		InputStream is = null;
		StringBuilder sb = null;
		String formattedUrl = "http://" + ipAddress + url;

		try {
			// JSONObject nameValuePairs = new JSONObject();
			// nameValuePairs.put("desiredPosts", "10");
			System.out.println("...........Initializing");

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
			// localhost for simulator
			// "http://10.0.2.2/Pharmarays/pharma-public/index.php/mobile/mobile_controller/loginauthorization");
			// "http://10.0.2.2/Pharmarays/pharma-public/index.php/sys_admin/user_authorization/default_select");
			// localhost for device
					formattedUrl);
			// online for both emulator and device
			// "http://www.pharmarays.com/pharma-public/index.php/sys_admin/user_authorization/default_select");
			Log.w("App Connector", "After initiating the connection");
			if (nameValuePairs != null)
				httpPost.setEntity(new UrlEncodedFormEntity(
						(List<? extends NameValuePair>) nameValuePairs));
			Log.w("App Connector", "After setting the JSON values======"
					+ formattedUrl);
			HttpResponse response = httpClient.execute(httpPost);
			Log.w("AppConnector", "After getting http response------");
			HttpEntity entity = response.getEntity();
			Log.w("App Connector",
					"After getting the entity from the response object=====");
			is = entity.getContent();
			Log.w("App Connector", "After getting the input stream");
			System.out.println("After getting the contents.......");
		} catch (Exception e) {
			System.out.println("Error in http connection........"
					+ e.toString());
			e.printStackTrace();
			Log.w("Login Activity", "error in http connection" + e.toString());
			return null;
		}

		return formatInputStream(is, sb);
	}

	public static JSONArray formatInputStream(InputStream is, StringBuilder sb) {
		Log.w("App Connector", "Inside the format input stream.....");
		String result = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			System.out.println("Reading the contents of the buffer........");
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.w("Login Activity", result);
			// System.out.println("The result is>>>>>>>>>>" + result);
		} catch (Exception e) {
			System.out.println("Error converting the result......."
					+ e.toString());
			Log.w("Login Activity", "Error converting result" + e.toString());
			return null;
		}

		JSONArray jArray = null;

		try {
			// System.out.println("Preparing to format the JSON==============="+
			// result);
			jArray = new JSONArray(result);

		} catch (JSONException e1) {
			// Toast.makeText(getBaseContext(), "No food found",
			// Toast.LENGTH_LONG).show();
			Log.w("Login Activity", "No food found");
			System.out.println("No food found.....");

			return AuthResponseFormat.getErrorExecution();

		} catch (ParseException e1) {
			e1.printStackTrace();
			return AuthResponseFormat.getErrorExecution();
		}
		return jArray;
	}
}
