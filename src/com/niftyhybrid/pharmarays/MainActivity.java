package com.niftyhybrid.pharmarays;

import com.niftyhybrid.pharmarays.utils.ConvertUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends Activity {
	private EditText text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

	// this method is called at button click because we assigned the name to the
	// "OnClick property" of the button

}