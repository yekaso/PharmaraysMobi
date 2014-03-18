package com.niftyhybrid.pharmarays;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.niftyhybrid.pharmarays.data.Constants;
import com.niftyhybrid.pharmarays.data.Pharmacy;
import com.niftyhybrid.pharmarays.utils.AppConnector;
import com.niftyhybrid.pharmarays.utils.AuthResponseFormat;
import com.niftyhybrid.pharmarays.utils.ProgressBarUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;
import com.niftyhybrid.pharmarays.utils.TrimmerUtil;

public class AssignPharmacyActivity extends Activity {
	private DrugsAdapter dataAdapter = null;
	private PharmListTask pharmListTask = null;
	private AssignPharmTask assignPharmTask = null;

	private TextView assignPharmAlert, loadingStatusMessageView;
	private ProgressBarUtil progressBarUtil;
	private View mSigninFormView;
	private View mSigninStatusView;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assign_pharmacy);

		session = new SessionManager(getApplicationContext(), this);
		mSigninFormView = findViewById(R.id.pharmlist_form);
		mSigninStatusView = findViewById(R.id.loading_status);

		assignPharmAlert = (TextView) findViewById(R.id.assignPharmAlert);
		loadingStatusMessageView = (TextView) findViewById(R.id.loading_status_message);

		progressBarUtil = new ProgressBarUtil();
		Log.w("Register Activity", "The views are not null...."
				+ mSigninFormView + "<<<< and the status is >>>>"
				+ mSigninStatusView);
		progressBarUtil.setmLoginFormView(mSigninFormView);
		progressBarUtil.setmLoginStatusView(mSigninStatusView);

		displayListView();
		findViewById(R.id.assignpharmacybutton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						assignPharmacy();
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assign_pharmacy, menu);
		return true;
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

	public void assignPharmacy() {
		assignPharmAlert.setText("");
		loadingStatusMessageView.setText(R.string.loading_progress_message);
		progressBarUtil.showProgress(true, this);

		assignPharmTask = new AssignPharmTask(this);
		assignPharmTask.execute((Void) null);
	}

	private ArrayList<NameValuePair> populateNameValuePair(Activity activity) {
		Log.w("DrugList Activity", "Update the database with the drugs>>>>>");

		ArrayList<Pharmacy> pharmacyList = dataAdapter.pharmList;
		String createPharmList = "";
		ArrayList<NameValuePair> nameValuePairs = null;
		HashMap<String, String> user = session.getUserDetails();

		int createCount = 0;
		for (Pharmacy pharmacy : pharmacyList) {
			if (pharmacy.isSelected()) {
				Log.w("DrugList",
						"Start doing the drugs magic of CREATE..... :D"
								+ pharmacy.getName() + " and brandnames >>>>>"
								+ pharmacy.getEmailAddress());
				if (createCount == 0)
					createPharmList = pharmacy.getId().toString();
				else
					createPharmList += "," + pharmacy.getId().toString();
				createCount++;
				Log.w("DrugList Act", "Displaying the created drugs......"
						+ createPharmList);
			}

		}
		nameValuePairs = new ArrayList<NameValuePair>();
		Log.w("DrugList Activity",
				"=======++++++++=======>>>>>>>>The create data||||"
						+ createPharmList);
		nameValuePairs.add(new BasicNameValuePair("createdata", createPharmList
				.trim()));
		nameValuePairs.add(new BasicNameValuePair("memberid", user
				.get(session.KEY_MEMBERID)));
		return nameValuePairs;

	}

	public class AssignPharmTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;
		private JSONArray jArray = null;

		public AssignPharmTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			JSONArray jArray = AppConnector
					.assignPharmacy(populateNameValuePair(activity));
			if (jArray != null) {
				Log.w("Login Activity", "The result has to be displayed here"
						+ jArray.toString());
				AuthResponseFormat.formatResponse(jArray);
				if (AuthResponseFormat.status.equalsIgnoreCase("error"))
					return false;
				else
					return true;
			} else {

				return null;
			}

		}

		protected void onPostExecute(final Boolean success) {
			assignPharmTask = null;

			Log.w("Register Activity",
					">>>>>>>>>the result of drug list is :::" + success);

			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				assignPharmAlert.setText(R.string.connection_error);
			} else if (success) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				assignPharmAlert.setText(R.string.successful_update);
				assignPharmAlert.setTextColor(activity.getResources().getColor(
						R.color.button_color_green));
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", "Continue please!!!");
				assignPharmAlert.setText(AuthResponseFormat.message);
			}

		}

		@Override
		protected void onCancelled() {
			assignPharmTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}

	private void displayListView() {

		// Array list of countries
		loadingStatusMessageView.setText(R.string.loading_progress_message);
		progressBarUtil.showProgress(true, this);
		pharmListTask = new PharmListTask(this);
		pharmListTask.execute((Void) null);

	}

	public class PharmListTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;
		private JSONArray jArray = null;

		public PharmListTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("limit", String
					.valueOf(Constants.DATALISTLIMIT)));
			Log.w("Assign Pharm", "The name value pair assigned is >>>"
					+ nameValuePairs.toString());
			jArray = AppConnector.retrievePharmacy(nameValuePairs);
			if (jArray != null) {
				Log.w("Register Activity",
						"The result has to be displayed here"
								+ jArray.toString());
				AuthResponseFormat.formatResponse(jArray);
				if (AuthResponseFormat.status.equalsIgnoreCase("error"))
					return false;
				else
					return true;
			} else {
				return null;
			}
			// TODO: register the new account here.
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			pharmListTask = null;

			Log.w("Register Activity",
					">>>>>>>>>the result of pharmacy list is :::" + success);

			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				assignPharmAlert.setText(R.string.connection_error);
			} else if (success) {
				ArrayList<Pharmacy> pharmList = new ArrayList<Pharmacy>();
				JSONObject jsonData = null;
				Pharmacy pharmacy = null;

				try {
					jsonData = jArray.getJSONObject(0);
					for (int i = 0; i < jArray.length(); i++) {
						pharmacy = new Pharmacy();
						jsonData = jArray.getJSONObject(i);
						// pharmacy.setName(TrimmerUtil.trim(
						// jsonData.getString("drug_name"), 10, true));
						pharmacy.setName(TrimmerUtil.capitalize(jsonData
								.getString("name")));
						pharmacy.setEmailAddress(jsonData.getString("location"));
						pharmacy.setPhoneNumber(jsonData.getString("telephone"));

						pharmacy.setId(jsonData.getLong("id"));
						pharmList.add(pharmacy);
					}
				} catch (JSONException e) {
					Log.w("Login Activity", e.toString());
				}
				Log.w("PHarm List Activity=======<<<<<<<<<>>>>>>>>>======",
						pharmList.toString());
				dataAdapter = new DrugsAdapter(activity, R.layout.pharm_info,
						pharmList);
				ListView listView = (ListView) findViewById(R.id.pharmacy_list);
				// Assign adapter to ListView
				listView.setAdapter(dataAdapter);
				progressBarUtil.showProgress(false, this.activity);
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", "Continue please!!!");
				assignPharmAlert.setText(AuthResponseFormat.message);
			}

		}

		@Override
		protected void onCancelled() {
			pharmListTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}

	private class DrugsAdapter extends ArrayAdapter<Pharmacy> {

		private ArrayList<Pharmacy> pharmList;

		public DrugsAdapter(Context context, int textViewResourceId,

		ArrayList<Pharmacy> pharmList) {
			super(context, textViewResourceId, pharmList);
			this.pharmList = new ArrayList<Pharmacy>();
			this.pharmList.addAll(pharmList);
		}

		private class ViewHolder {
			TextView pharmAddress, pharmEmail, pharmNumber;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Pharmacy pharmacy = null;
			ViewHolder holder = null;

			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.pharm_info, null);

				holder = new ViewHolder();
				holder.pharmAddress = (TextView) convertView
						.findViewById(R.id.pharmacy_address);
				holder.pharmEmail = (TextView) convertView
						.findViewById(R.id.pharmacy_email);
				holder.pharmNumber = (TextView) convertView
						.findViewById(R.id.pharmacy_mobile);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.pharmacyCheckBox);

				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					Pharmacy pharms = null;

					public void onClick(View v) {
						pharms = new Pharmacy();
						CheckBox cb = (CheckBox) v;
						pharms = (Pharmacy) cb.getTag();

						pharms.setSelected(cb.isChecked());
						Log.w("Pharm List Activity",
								"The pharms selected state >>>>"
										+ pharms.isSelected()
										+ "<<<The pharms not checked>>>>>>"
										+ pharms.isNotChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			pharmacy = new Pharmacy();
			pharmacy = pharmList.get(position);

			holder.pharmAddress.setText(pharmacy.getAddress());
			holder.pharmEmail.setText(pharmacy.getEmailAddress());
			holder.pharmNumber.setText(pharmacy.getPhoneNumber());
			holder.name.setText(pharmacy.getName());
			holder.name.setChecked(pharmacy.isSelected());

			holder.name.setTag(pharmacy);

			return convertView;
		}
	}

}
