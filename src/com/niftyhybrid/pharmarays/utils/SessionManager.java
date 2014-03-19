package com.niftyhybrid.pharmarays.utils;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.niftyhybrid.pharmarays.LoginActivity;

public class SessionManager {
	public SharedPreferences pref;
	Editor editor;
	Context newContext;
	Activity activity;
	int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "AndroidHivePref";
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_MEMBERID = "memberid";
	public static final String KEY_PHARMACYID = "pharmacyid";
	public static final String KEY_USERNAME = "membername";
	public static final String KEY_LOGINUSERROLE = "loginuserroleid";

	public SessionManager(Context context, Activity activity) {
		this.newContext = context;
		this.activity = activity;
		pref = newContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createUserLoginSession(String memberName, String memberId) {
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_USERNAME, memberName);
		editor.putString(KEY_MEMBERID, memberId);
		editor.commit();
	}

	public void createPharmacyOwnerLoginSession(String memberId,
			String pharmacyId, String memberName, String loginuserroleid) {
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_USERNAME, memberName);
		editor.putString(KEY_MEMBERID, memberId);
		editor.putString(KEY_PHARMACYID, pharmacyId);
		editor.putString(KEY_LOGINUSERROLE, loginuserroleid);
		editor.commit();
	}

	public void checkLogin() {
		if (!this.isLoggedIn()) {
			logoutUser();
		}
	}

	public void logoutUser() {
		editor.clear();
		editor.commit();

		Intent i = new Intent(newContext, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newContext.startActivity(i);
		activity.finish();

	}

	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}

	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_MEMBERID, pref.getString(KEY_MEMBERID, null));
		user.put(KEY_PHARMACYID, pref.getString(KEY_PHARMACYID, null));
		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
		return user;
	}
}
