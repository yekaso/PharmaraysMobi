package com.niftyhybrid.pharmarays;

import java.util.ArrayList;

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

	private TextView pharmListAlert, loadingStatusMessageView;
	private ProgressBarUtil progressBarUtil;
	private View mSigninFormView;
	private View mSigninStatusView;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assign_pharmacy);

		session = new SessionManager(getApplicationContext());
		mSigninFormView = findViewById(R.id.pharmlist_form);
		mSigninStatusView = findViewById(R.id.loading_status);

		pharmListAlert = (TextView) findViewById(R.id.assignPharmAlert);
		loadingStatusMessageView = (TextView) findViewById(R.id.loading_status_message);

		progressBarUtil = new ProgressBarUtil();
		Log.w("Register Activity", "The views are not null...."
				+ mSigninFormView + "<<<< and the status is >>>>"
				+ mSigninStatusView);
		progressBarUtil.setmLoginFormView(mSigninFormView);
		progressBarUtil.setmLoginStatusView(mSigninStatusView);

		displayListView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assign_pharmacy, menu);
		return true;
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
				pharmListAlert.setText(R.string.connection_error);
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
				pharmListAlert.setText(AuthResponseFormat.message);
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
