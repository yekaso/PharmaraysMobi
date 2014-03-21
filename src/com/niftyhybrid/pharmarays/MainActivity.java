package com.niftyhybrid.pharmarays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.niftyhybrid.pharmarays.utils.ConvertUtil;
import com.niftyhybrid.pharmarays.utils.SessionManager;

public class MainActivity extends Activity {
	private EditText text;
	SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		session = new SessionManager(getApplicationContext(), this);

		text = (EditText) findViewById(R.id.firstname);
		final Button button = (Button) findViewById(R.id.signupbutton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				System.out.println("The view is clicked!!!!!!");
				// logger.info("The view is clicked!!!!");
				switch (view.getId()) {
				case R.id.signupbutton:
					RadioButton celsiusButton = (RadioButton) findViewById(R.id.male);
					RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.female);
					if (text.getText().length() == 0) {
						// Toast.makeText(this, "Please enter a valid number",
						// Toast.LENGTH_LONG).show();
						return;
					}

					float inputValue = Float.parseFloat(text.getText()
							.toString());
					if (celsiusButton.isChecked()) {
						text.setText(String.valueOf(ConvertUtil
								.convertFahrenheitToCelsius(inputValue)));
						celsiusButton.setChecked(false);
						fahrenheitButton.setChecked(true);
					} else {
						text.setText(String.valueOf(ConvertUtil
								.convertCelsiusToFahrenheit(inputValue)));
						fahrenheitButton.setChecked(false);
						celsiusButton.setChecked(true);
					}
					break;
				}
				return;
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

	// this method is called at button click because we assigned the name to the
	// "OnClick property" of the button

}