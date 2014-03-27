package com.niftyhybrid.pharmarays;

import java.util.ArrayList;

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
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.niftyhybrid.pharmarays.utils.AppConnector;
import com.niftyhybrid.pharmarays.utils.AuthResponseFormat;
import com.niftyhybrid.pharmarays.utils.ProgressBarUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;

public class RegisterActivity extends Activity {
	AuthResponseFormat authResponseFormat;
	private EditText fName, lName, email, password;
	private Spinner userRole;
	private DatePicker dateOfBirth;
	private RadioButton gender;
	private RadioGroup genderGroup;
	private RegistrationTask regTask = null;
	private ProgressBarUtil progressBarUtil;
	private View mSigninFormView;
	private View mSigninStatusView;
	private TextView registrationAlert, signinStatusMsgView;
	private String selectedUserType = "";
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		fName = (EditText) findViewById(R.id.pharmacyname);
		lName = (EditText) findViewById(R.id.pharmacymobile);
		email = (EditText) findViewById(R.id.pharmacyemail);
		password = (EditText) findViewById(R.id.new_userpassword);

		registrationAlert = (TextView) findViewById(R.id.registerAlert);
		authResponseFormat = new AuthResponseFormat();
		session = new SessionManager(getApplicationContext(), this);

		genderGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

		dateOfBirth = (DatePicker) findViewById(R.id.dobDatePicker);
		userRole = (Spinner) findViewById(R.id.userroleSelect);

		mSigninFormView = findViewById(R.id.reg_form);
		mSigninStatusView = findViewById(R.id.signin_status);
		progressBarUtil = new ProgressBarUtil();
		Log.w("Register Activity", "The views are not null...."
				+ mSigninFormView + "<<<< and the status is >>>>"
				+ mSigninStatusView);
		progressBarUtil.setmLoginFormView(mSigninFormView);
		progressBarUtil.setmLoginStatusView(mSigninStatusView);

		signinStatusMsgView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.completeregistration).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.w("Register Activity",
								"Complete Registration Button clicked......");
						concludeRegistration();
					}
				});
		findViewById(R.id.cancelregistration).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.w("Register Activity",
								"Cancel Button clicked......");
						cancelRegistration();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	public void cancelRegistration() {
		Log.w("Register Activity", "Cancel Registration attempt");
		Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
		startActivity(i);

		// close this activity
		finish();
	}

	public void concludeRegistration() {
		if (regTask != null) {
			return;
		}
		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(fName.getText().toString())) {
			fName.setError(getString(R.string.error_field_required));
			focusView = fName;
			cancel = true;
		} else if (TextUtils.isEmpty(lName.getText().toString())) {
			lName.setError(getString(R.string.error_field_required));
			focusView = lName;
			cancel = true;
		} else if (TextUtils.isEmpty(email.getText().toString())) {
			email.setError(getString(R.string.error_field_required));
			focusView = email;
			cancel = true;
		} else if (TextUtils.isEmpty(password.getText().toString())) {
			password.setError(getString(R.string.error_field_required));
			focusView = password;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			signinStatusMsgView.setText(R.string.registration_status);
			progressBarUtil.showProgress(true, this);
			regTask = new RegistrationTask(this);
			regTask.execute((Void) null);
		}

	}

	public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;

		public RegistrationTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			int selectedId = genderGroup.getCheckedRadioButtonId();

			// find the radiobutton by returned id
			gender = (RadioButton) findViewById(selectedId);

			ArrayList<NameValuePair> nameValuePairs;
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("fname", fName.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("lname", lName.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("email", email.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("password", password
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("gender", (String) gender
					.getText()));
			;
			selectedUserType = (String) userRole.getSelectedItem();
			nameValuePairs.add(new BasicNameValuePair("member_type",
					selectedUserType));
			nameValuePairs.add(new BasicNameValuePair("date_of_birth",
					dateOfBirth.getDayOfMonth() + "-" + dateOfBirth.getMonth()
							+ "-" + dateOfBirth.getYear()));
			Log.w("Register Activity", "The name value pairs>>>>>"
					+ nameValuePairs.toString());

			JSONArray jArray = AppConnector.registerUser(nameValuePairs);
			if (jArray != null) {
				Log.w("Register Activity",
						"The result has to be displayed here"
								+ jArray.toString());
				authResponseFormat.formatResponse(jArray);
				if (authResponseFormat.status.equalsIgnoreCase("error"))
					return false;
				else {
					authResponseFormat.formatRegistrationResponse(jArray);
					return true;
				}
			} else {

				return null;
			}
			// TODO: register the new account here.
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			regTask = null;

			Log.w("Register Activity", ">>>>>>>>>the result of login is :::"
					+ success);

			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				registrationAlert.setText(R.string.connection_error);
			} else if (success) {
				Log.w("Register Activity", "It is finished!!!!!");

				Log.w("Register Activity",
						"Registration attempt on completed>>>>>"
								+ authResponseFormat.loginuserroleid);
				Intent i = null;
				if (selectedUserType.equalsIgnoreCase("pharmacy owner")) {
					session.createPharmacyOwnerLoginSession(authResponseFormat.loggedInPharmacy);
					i = new Intent(RegisterActivity.this,
							AssignPharmacyActivity.class);
				} else {
					i = new Intent(RegisterActivity.this, MainActivity.class);
				}
				startActivity(i);

				// close this activity
				progressBarUtil.showProgress(false, this.activity);
				// close this activity
				finish();
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", "Continue please!!!");
				registrationAlert.setText(authResponseFormat.message);
				fName.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			regTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}
}
