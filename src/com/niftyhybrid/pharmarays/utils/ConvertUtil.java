package com.niftyhybrid.pharmarays.utils;


public class ConvertUtil {
	public static float convertFahrenheitToCelsius(float fahrenheit) {
		System.out.println("Fah>>>>>>>>>>>>>>");
		return ((fahrenheit - 32) * 5 / 9);
	}

	public static float convertCelsiusToFahrenheit(float celsius) {
		System.out.println("Celsius>>>>>>>>>>>>");
		return ((celsius * 9 / 5) + 32);
	}
}