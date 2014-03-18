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

import com.niftyhybrid.pharmarays.data.Drugs;
import com.niftyhybrid.pharmarays.utils.AppConnector;
import com.niftyhybrid.pharmarays.utils.AuthResponseFormat;
import com.niftyhybrid.pharmarays.utils.ProgressBarUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;
import com.niftyhybrid.pharmarays.utils.TrimmerUtil;

public class DrugListActivity extends Activity {
	private DrugsAdapter dataAdapter = null;
	private DrugListTask drugListTask = null;
	private DrugAvailabilityTask drugAvailabilityTask = null;
	private ProgressBarUtil progressBarUtil;
	private View mSigninFormView;
	private View mSigninStatusView;
	private TextView drugListAlert, loadingStatusMessageView, pharmacyName;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drug_list);
		session = new SessionManager(getApplicationContext(), this);
		mSigninFormView = findViewById(R.id.druglist_form);
		mSigninStatusView = findViewById(R.id.loading_status);

		drugListAlert = (TextView) findViewById(R.id.drugListAlert);
		loadingStatusMessageView = (TextView) findViewById(R.id.loading_status_message);
		Log.w("Drug List Activity", "The component in contention is:::::"
				+ findViewById(R.id.pharmacy_name).toString());
		// Log.w("Drug List Activity", "The component in contention is:::::"
		// + findViewById(R.id.pharmacyname).toString());
		pharmacyName = (TextView) findViewById(R.id.pharmacy_name);

		progressBarUtil = new ProgressBarUtil();
		Log.w("Register Activity", "The views are not null...."
				+ mSigninFormView + "<<<< and the status is >>>>"
				+ mSigninStatusView);
		progressBarUtil.setmLoginFormView(mSigninFormView);
		progressBarUtil.setmLoginStatusView(mSigninStatusView);

		findViewById(R.id.findSelected).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						triggerUpdateAvailability();
					}
				});

		displayListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drug_list, menu);
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

	public void triggerUpdateAvailability() {
		drugListAlert.setText("");
		loadingStatusMessageView.setText(R.string.loading_progress_message);
		progressBarUtil.showProgress(true, this);

		drugAvailabilityTask = new DrugAvailabilityTask(this);
		drugAvailabilityTask.execute((Void) null);
	}

	private void displayListView() {

		// Array list of countries
		loadingStatusMessageView.setText(R.string.loading_progress_message);
		progressBarUtil.showProgress(true, this);
		drugListTask = new DrugListTask(this);
		drugListTask.execute((Void) null);

	}

	private class DrugsAdapter extends ArrayAdapter<Drugs> {

		private ArrayList<Drugs> drugList;

		public DrugsAdapter(Context context, int textViewResourceId,

		ArrayList<Drugs> drugList) {
			super(context, textViewResourceId, drugList);
			this.drugList = new ArrayList<Drugs>();
			this.drugList.addAll(drugList);
		}

		private class ViewHolder {
			TextView drugBrands, drugManufacturers;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Drugs drug = null;
			ViewHolder holder = null;

			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = vi.inflate(R.layout.drug_info, null);

				holder = new ViewHolder();
				holder.drugBrands = (TextView) convertView
						.findViewById(R.id.drugs_brand);
				holder.drugManufacturers = (TextView) convertView
						.findViewById(R.id.drugs_manufacturer);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.drugCheckBox);

				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					Drugs drugs = null;

					public void onClick(View v) {
						drugs = new Drugs();
						CheckBox cb = (CheckBox) v;
						drugs = (Drugs) cb.getTag();

						drugs.setSelected(cb.isChecked());
						Log.w("Drug List Activity",
								"The drugs selected state >>>>"
										+ drugs.isSelected()
										+ "<<<The drugs not checked>>>>>>"
										+ drugs.isNotChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			drug = new Drugs();
			drug = drugList.get(position);

			holder.drugBrands.setText(drug.getBrandNames());
			holder.drugManufacturers.setText(drug.getDrugManufacturer());
			holder.name.setText(drug.getName());
			holder.name.setChecked(drug.isSelected());

			holder.name.setTag(drug);

			return convertView;
		}
	}

	private ArrayList<NameValuePair> populateNameValuePair(Activity activity) {
		Log.w("DrugList Activity", "Update the database with the drugs>>>>>");

		ArrayList<Drugs> drugsList = dataAdapter.drugList;
		String createDrugList = "", deleteDrugList = "";
		ArrayList<NameValuePair> nameValuePairs = null;
		HashMap<String, String> user = session.getUserDetails();

		int createCount = 0, deleteCount = 0;
		// Drugs drugs = null;
		for (Drugs drugs : drugsList) {
			if (drugs.isSelected() && drugs.isNotChecked()) {
				Log.w("DrugList",
						"Start doing the drugs magic of CREATE..... :D"
								+ drugs.getName() + " and brandnames >>>>>"
								+ drugs.getBrandNames());
				if (createCount == 0)
					createDrugList = drugs.getId().toString();
				else
					createDrugList += "," + drugs.getId().toString();
				createCount++;
				Log.w("DrugList Act", "Displaying the created drugs......"
						+ createDrugList);
				// dataAdapter.remove(drugs);
				drugs.setNotChecked(false);
				// drugsList.set(i, drugs);
				// dataAdapter.add(drugs);

			} else if (!drugs.isSelected() && !drugs.isNotChecked()) {
				Log.w("DrugList",
						"Start doing the drugs magic of DELETE..... :D"
								+ drugs.getName() + " and brandnames >>>>>"
								+ drugs.getBrandNames());
				if (deleteCount == 0)
					deleteDrugList = drugs.getId().toString();
				else
					deleteDrugList += "," + drugs.getId().toString();
				deleteCount++;
				Log.w("DrugList Act", "Displaying the deleted drugs......"
						+ deleteDrugList);
				// dataAdapter.remove(drugs);
				drugs.setNotChecked(true);
				// drugsList.set(i, drugs);

				// dataAdapter.add(drugs);
			}

		}
		// dataAdapter.

		nameValuePairs = new ArrayList<NameValuePair>();
		Log.w("DrugList Activity",
				"=======++++++++=======>>>>>>>>The create data||||"
						+ createDrugList
						+ " =========>>>>>> the delete data ||||"
						+ deleteDrugList);
		nameValuePairs.add(new BasicNameValuePair("createdata", createDrugList
				.trim()));
		nameValuePairs.add(new BasicNameValuePair("deletedata", deleteDrugList
				.trim()));
		nameValuePairs.add(new BasicNameValuePair("pharmacyid", user
				.get(session.KEY_PHARMACYID)));
		return nameValuePairs;

	}

	public class DrugAvailabilityTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;
		private JSONArray jArray = null;

		public DrugAvailabilityTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			JSONArray jArray = AppConnector
					.updateDrugAvailability(populateNameValuePair(activity));
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
			drugListTask = null;

			Log.w("Register Activity",
					">>>>>>>>>the result of drug list is :::" + success);

			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				drugListAlert.setText(R.string.connection_error);
			} else if (success) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				drugListAlert.setText(R.string.successful_update);
				drugListAlert.setTextColor(activity.getResources().getColor(
						R.color.button_color_green));
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", "Continue please!!!");
				drugListAlert.setText(AuthResponseFormat.message);
			}

		}

		@Override
		protected void onCancelled() {
			drugListTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}

	public class DrugListTask extends AsyncTask<Void, Void, Boolean> {
		private Activity activity = null;
		private JSONArray jArray = null;

		public DrugListTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ArrayList<NameValuePair> nameValuePairs;
			nameValuePairs = new ArrayList<NameValuePair>();
			HashMap<String, String> user = session.getUserDetails();
			nameValuePairs.add(new BasicNameValuePair("pharmacyid", user
					.get(session.KEY_PHARMACYID)));
			nameValuePairs.add(new BasicNameValuePair("member_id", user
					.get(session.KEY_MEMBERID)));
			Log.w("Drug LIst Activity",
					"Pharmacy id >>>><<<<" + user.get(session.KEY_PHARMACYID)
							+ " and Member id is >>>>><<<<<"
							+ user.get(session.KEY_MEMBERID));
			jArray = AppConnector.retrieveDrugs(nameValuePairs);
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
			drugListTask = null;

			Log.w("Register Activity",
					">>>>>>>>>the result of drug list is :::" + success);

			if (success == null) {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", getBaseContext().toString());
				drugListAlert.setText(R.string.connection_error);
			} else if (success) {
				ArrayList<Drugs> drugList = new ArrayList<Drugs>();
				JSONObject jsonData = null;
				Drugs drug = null;

				try {
					jsonData = jArray.getJSONObject(0);
					jArray = new JSONArray();
					jArray = jsonData.getJSONArray("drugs");
					pharmacyName.setText(jsonData.getString("pharmacy"));
					for (int i = 0; i < jArray.length(); i++) {
						drug = new Drugs();
						jsonData = jArray.getJSONObject(i);
						// drug.setName(TrimmerUtil.trim(
						// jsonData.getString("drug_name"), 10, true));
						drug.setName(TrimmerUtil.capitalize(jsonData
								.getString("drug_name")));
						drug.setBrandNames(jsonData
								.getString("drug_brandnames"));
						drug.setDrugManufacturer(jsonData
								.getString("drug_company"));

						drug.setDescription(jsonData
								.getString("drug_description"));
						drug.setId(jsonData.getLong("id_drug"));
						drug.setNotChecked(jsonData.getBoolean("notChecked"));
						drug.setSelected(!drug.isNotChecked());
						drugList.add(drug);
					}
				} catch (JSONException e) {
					Log.w("Login Activity", e.toString());
				}
				Log.w("Drug Activity=============", drugList.toString());
				dataAdapter = new DrugsAdapter(activity, R.layout.drug_info,
						drugList);
				ListView listView = (ListView) findViewById(R.id.drug_list);
				// Assign adapter to ListView
				listView.setAdapter(dataAdapter);
				progressBarUtil.showProgress(false, this.activity);
			} else {
				progressBarUtil.showProgress(false, this.activity);
				Log.w("Register Activity", "Continue please!!!");
				drugListAlert.setText(AuthResponseFormat.message);
			}

		}

		@Override
		protected void onCancelled() {
			drugListTask = null;
			progressBarUtil.showProgress(false, activity);
		}
	}
}