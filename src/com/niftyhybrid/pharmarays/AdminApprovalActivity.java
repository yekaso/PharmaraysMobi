package com.niftyhybrid.pharmarays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.niftyhybrid.pharmarays.utils.SessionManager;

public class AdminApprovalActivity extends Activity {
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_approval);

		session = new SessionManager(getApplicationContext(), this);

		findViewById(R.id.logout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.w("Approval Activity",
								"Logout Button clicked......");
						session.logoutUser();
					}
				});
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
