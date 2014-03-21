package com.niftyhybrid.pharmarays.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AuthResponseFormat {
	public String status;
	public String message;
	public String memberId;
	public String userName;
	public String pharmacyId;
	public String pharmacyAssignmentStatus;
	public String loginuserroleid;
	public String memberTypeName;

	public void formatResponse(JSONArray jArray) {
		JSONObject jsonData = null;
		try {
			for (int i = 0; i < jArray.length(); i++) {
				jsonData = jArray.getJSONObject(i);
				status = jsonData.getString("status");
				message = jsonData.getString("message");
			}
		} catch (JSONException e) {
			Log.w("Login Activity", e.toString());
		}
		Log.w("Auth Response Format",
				"After formatting the response JSON object");

	}

	public JSONArray getErrorExecution() {
		JSONArray jArray = new JSONArray();

		JSONObject jData = new JSONObject();
		try {
			jData.put("status", "error");
			jData.put("message", "Error processing request");
			jArray.put(jData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jArray;
	}

	public void formatLoginResponse(JSONArray jArray) {
		JSONObject jsonData = null, pharmJsonData = null, rawPharmData = null;
		try {
			for (int i = 0; i < jArray.length(); i++) {
				jsonData = jArray.getJSONObject(i);
				if (memberId == null)
					memberId = jsonData.getString("memberid");
				Log.w("Auth Resp", "The RETRIEVED MEMBER ID IS >>>>>>"
						+ memberId);
				if (userName == null)
					userName = jsonData.getString("logged_in_user");
				if (memberTypeName == null)
					memberTypeName = jsonData.getString("membertypename");
				if (loginuserroleid == null) {
					loginuserroleid = jsonData.getString("loginuserroleid");
					if (loginuserroleid != null)
						if (loginuserroleid.equalsIgnoreCase("null"))
							loginuserroleid = null;
					Log.w("Auth Resp", "The RETRIEVED LOGINROLEID IS >>>>>>"
							+ loginuserroleid);
				}
				if (rawPharmData == null) {
					rawPharmData = jsonData.getJSONObject("pharmacydata");
					if (rawPharmData != null) {
						Log.w("Login response", "The pharmacy data>>>>>>>>"
								+ rawPharmData.toString());
						// pharmJsonData = rawPharmData.getJSONObject(0);
						pharmacyId = rawPharmData.getString("id");
						pharmacyAssignmentStatus = rawPharmData
								.getString("status");
					}
				}
				Log.w("Response Formatter", "================>>>>>>>The " + i
						+ " iterator for member..." + memberId
						+ " and userName..." + userName + " and pharmacy id..."
						+ pharmacyId);
			}
			if (rawPharmData == null) {

			}
		} catch (JSONException e) {
			Log.w("Login Activity", e.toString());
		}
		Log.w("Auth Response Format",
				"After formatting the response JSON object for login");
	}

}
