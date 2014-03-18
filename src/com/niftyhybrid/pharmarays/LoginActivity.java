package com.niftyhybrid.pharmarays;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.niftyhybrid.pharmarays.data.Constants;
import com.niftyhybrid.pharmarays.utils.AppConnector;
import com.niftyhybrid.pharmarays.utils.AuthResponseFormat;
import com.niftyhybrid.pharmarays.utils.ProgressBarUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("ShowToast")
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	SessionManager session;
	// UI references.
	private EditText mEmailView;
	private TextView siginAlert;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private ProgressBarUtil progressBarUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		session = new SessionManager(getApplicationContext(), this);

		// the alert field
		siginAlert = (TextView) findViewById(R.id.siginAlert);
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		// mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		progressBarUtil = new ProgressBarUtil();
		progressBarUtil.setmLoginFormView(mLoginFormView);
		progressBarUtil.setmLoginStatusView(mLoginStatusView);

		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegistration();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptRegistration() {
		System.out.println("Registration attempt made :D");
		Log.w("Login Activity", "Registration attempt on log file");
		// Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
		Intent i = new Intent(LoginActivity.this, HomePageActivity.class);
		startActivity(i);

		// close this activity
		// finish();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			progressBarUtil.showProgress(true, this);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;

		public UserLoginTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			ArrayList<NameValuePair> nameValuePairs;
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", mEmail));
			nameValuePairs.add(new BasicNameValuePair("password", mPassword));
			JSONArray jArray = AppConnector.login(nameValuePairs);
			if (jArray != null) {
				Log.w("Login Activity", "The result has to be displayed here"
						+ jArray.toString());
				AuthResponseFormat.formatResponse(jArray);

				if (AuthResponseFormat.status.equalsIgnoreCase("error"))
					return false;
				else {
					Log.w("Login Activity",
							"===========+++++++++========+++++++++========++++++++");
					AuthResponseFormat.formatLoginResponse(jArray);
					return true;
				}
			} else {

				return null;
			}
			// TODO: register the new account here.
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			Log.w("Login Activity", ">>>>>>>>>the result of login is :::"
					+ success);
			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Login Activity", getBaseContext().toString());
				siginAlert.setText(R.string.connection_error);
			} else if (success) {
				Log.w("Login Activity", "It is finished!!!!!>><<<<"
						+ AuthResponseFormat.memberId + " username id is "
						+ AuthResponseFormat.userName + " pharmacy id is "
						+ AuthResponseFormat.pharmacyId);
				session.createPharmacyOwnerLoginSession(
						AuthResponseFormat.memberId,
						AuthResponseFormat.pharmacyId,
						AuthResponseFormat.userName,
						AuthResponseFormat.loginuserroleid);
				Intent i = null;
				if (AuthResponseFormat.pharmacyAssignmentStatus
						.equalsIgnoreCase("null"))
					i = new Intent(LoginActivity.this,
							AssignPharmacyActivity.class);
				else if (AuthResponseFormat.pharmacyAssignmentStatus
						.equalsIgnoreCase(Constants.APPROVED))
					i = new Intent(LoginActivity.this, DrugListActivity.class);
				else
					i = new Intent(LoginActivity.this,
							AdminApprovalActivity.class);
				startActivity(i);
				progressBarUtil.showProgress(false, this.activity);
				// close this activity
				finish();
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Login Activity", "Continue please!!!");
				siginAlert.setText(AuthResponseFormat.message);
				mEmailView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}
}
