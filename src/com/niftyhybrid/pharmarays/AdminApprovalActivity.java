package com.niftyhybrid.pharmarays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.niftyhybrid.pharmarays.utils.SessionManager;

public class AdminApprovalActivity extends Activity {
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.activity_admin_approval);
		// getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// R.drawable.error);

		session = new SessionManager(getApplicationContext(), this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_approval, menu);
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

}
