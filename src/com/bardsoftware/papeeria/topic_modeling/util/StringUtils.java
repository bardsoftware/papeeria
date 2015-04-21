package com.bardsoftware.papeeria.topic_modeling.util;

public final class StringUtils {

	private StringUtils() {
	}

	public static String removeNonAlphanumeric(String s) {
		return s.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
	}

	public static String[] splitIntoChunks(String s, int step) {
		return s.split(String.format("(?<=\\G.{%d})", step));
	}

	public static String removeFirstWord(String s) {
		return s.split(" ", 2)[1];
	}

	public static String removeHyphenation(String s) {
		return s.replaceAll("\\-[\r\n\b]", "");
	}

	public static String preprocess(String s) {
		return removeHyphenation(removeNonAlphanumeric(s));
	}
}
