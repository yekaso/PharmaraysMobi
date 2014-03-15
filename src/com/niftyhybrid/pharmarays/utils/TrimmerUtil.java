package com.niftyhybrid.pharmarays.utils;

public class TrimmerUtil {
	private final static String ELIPSIS = "...";

	public static String trim(String input, int textSize, boolean elipsis) {
		String result = "";
		if (input.length() > textSize) {
			result = input.substring(0, textSize);
			if (elipsis) {
				result = result.concat(ELIPSIS);
			}
		} else
			return input;
		return result;
	}

	public static String capitalize(String input) {
		String result = "";
		String[] splitString = input.split(" ");
		for (String split : splitString) {
			result += split.substring(0, 1).toUpperCase();
			if (split.length() > 1)
				result = (result.concat(split.substring(1, (split.length()))) + " ");
		}
		return result.trim();
	}

}
