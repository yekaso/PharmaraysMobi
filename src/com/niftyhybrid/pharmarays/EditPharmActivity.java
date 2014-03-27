package com.niftyhybrid.pharmarays;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.niftyhybrid.pharmarays.utils.AppConnector;
import com.niftyhybrid.pharmarays.utils.AuthResponseFormat;
import com.niftyhybrid.pharmarays.utils.GPSTracker;
import com.niftyhybrid.pharmarays.utils.ProgressBarUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;

public class EditPharmActivity extends Activity {
	private SessionManager session;
	private EditText pharmName, pharmAddress, pharmPhone, pharmEmail;
	private CheckBox userCurrentLocationCheck;
	private TextView updateAlert, updateStatusMsgView;
	private GPSTracker gps;
	private PharmUpdateTask pharmUpdateTask;
	private ProgressBarUtil progressBarUtil;
	private View mPharmEditFormView;
	private View mProgressStatusView;
	private AuthResponseFormat authResponseFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_pharm);
		session = new SessionManager(getApplicationContext(), this);

		authResponseFormat = new AuthResponseFormat();

		pharmName = (EditText) findViewById(R.id.pharmacyname);
		pharmAddress = (EditText) findViewById(R.id.pharmacyaddress);
		pharmPhone = (EditText) findViewById(R.id.pharmacymobile);
		pharmEmail = (EditText) findViewById(R.id.pharmacyemail);
		userCurrentLocationCheck = (CheckBox) findViewById(R.id.pharmacylocation_check);

		mPharmEditFormView = findViewById(R.id.eidtpharmform);
		mProgressStatusView = findViewById(R.id.progress_status);
		progressBarUtil = new ProgressBarUtil();
		progressBarUtil.setmLoginFormView(mPharmEditFormView);
		progressBarUtil.setmLoginStatusView(mProgressStatusView);

		updateAlert = (TextView) findViewById(R.id.updatepharmalert);
		updateStatusMsgView = (TextView) findViewById(R.id.update_status_message);

		defaultViewSetting();

		findViewById(R.id.updatepharm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						updatePharmacyInfo();
					}
				});
		findViewById(R.id.cancelpharm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						cancelPharmEdit();
					}
				});
		userCurrentLocationCheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userCurrentLocationCheck.isChecked()) {
					gps = new GPSTracker(EditPharmActivity.this);

					// check if GPS enabled
					if (!gps.canGetLocation()) {
						gps.showSettingsAlert();

						// can't get location
						// GPS or Network is not enabled
						// Ask user to enable GPS/network in settings

					}
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_pharm, menu);
		return true;
	}

	public void defaultViewSetting() {
		HashMap<String, String> pharmacyDetails = session.getPharmacyDetails();
		Log.w("Edit Pharm", pharmacyDetails.toString());
		pharmName.setText(pharmacyDetails.get(session.KEY_PHARMNAME));
		pharmAddress.setText(pharmacyDetails.get(session.KEY_PHARMADDRESS));
		pharmPhone.setText(pharmacyDetails.get(session.KEY_PHARMPHONE));
		pharmEmail.setText(pharmacyDetails.get(session.KEY_PHARMEMAIL));
	}

	public void cancelPharmEdit() {
		Intent i = new Intent(EditPharmActivity.this, DrugListActivity.class);
		startActivity(i);

		// close this activity
		finish();
	}

	public void updatePharmacyInfo() {
		Log.w("Edit Pharm", "Edit Pharmacy Information...");
		if (pharmUpdateTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(pharmName.getText().toString())) {
			pharmName.setError(getString(R.string.error_field_required));
			focusView = pharmName;
			cancel = true;
		} else if (TextUtils.isEmpty(pharmAddress.getText().toString())) {
			pharmAddress.setError(getString(R.string.error_field_required));
			focusView = pharmAddress;
			cancel = true;
		} else if (TextUtils.isEmpty(pharmPhone.getText().toString())) {
			pharmPhone.setError(getString(R.string.error_field_required));
			focusView = pharmPhone;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt update and focus the
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			updateStatusMsgView.setText(R.string.update_status);
			progressBarUtil.showProgress(true, this);
			pharmUpdateTask = new PharmUpdateTask(this);
			pharmUpdateTask.execute((Void) null);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			session.logoutUser();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class PharmUpdateTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;

		public PharmUpdateTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			ArrayList<NameValuePair> nameValuePairs;
			HashMap<String, String> user = session.getUserDetails();
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("pharmName", pharmName
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pharmAddress",
					pharmAddress.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pharmEmail", pharmEmail
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pharmPhone", pharmPhone
					.getText().toString()));
			if (userCurrentLocationCheck.isChecked()) {
				nameValuePairs.add(new BasicNameValuePair("locationselected",
						"true"));
				nameValuePairs.add(new BasicNameValuePair("pharmLongitude",
						String.valueOf(gps.getLongitude())));
				nameValuePairs.add(new BasicNameValuePair("pharmLatitude",
						String.valueOf(gps.getLatitude())));
			} else {
				nameValuePairs.add(new BasicNameValuePair("locationselected",
						"false"));
			}
			nameValuePairs.add(new BasicNameValuePair("pharmacyid", user
					.get(session.KEY_PHARMACYID)));
			Log.d("Debugs", nameValuePairs.toString());
			JSONArray jArray = AppConnector.updatePharm(nameValuePairs);
			if (jArray != null) {
				Log.w("Login Activity", "The result has to be displayed here"
						+ jArray.toString());
				authResponseFormat.formatResponse(jArray);

				if (authResponseFormat.status.equalsIgnoreCase("error"))
					return false;
				else {
					Log.w("Login Activity",
							"===========+++++++++========+++++++++========++++++++"
									+ session.pref.getString(
											session.KEY_PHARMACYID, null));
					return true;
				}
			} else {

				return null;
			}
			// TODO: register the new account here.
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			pharmUpdateTask = null;

			Log.w("Login Activity", ">>>>>>>>>the result of login is :::"
					+ success);
			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Login Activity", getBaseContext().toString());
				updateAlert.setText(R.string.connection_error);
			} else if (success) {
				updateAlert.setText(R.string.pharm_update_success);
				updateAlert.setTextColor(activity.getResources().getColor(
						R.color.button_color_green));
				progressBarUtil.showProgress(false, this.activity);
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Login Activity", "Continue please!!!");
				updateAlert.setText(authResponseFormat.message);
			}
		}

		@Override
		protected void onCancelled() {
			pharmUpdateTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}
}
