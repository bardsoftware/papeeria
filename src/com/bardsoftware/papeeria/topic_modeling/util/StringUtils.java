package com.bardsoftware.papeeria.topic_modeling.util;

public class StringUtils {
	public static String removeNonAlphanumeric(String s) {
		return s.replaceAll("[^\\p{L}\\p{Nd}]+", " ");
	}

	public static String[] splitIntoChunks(String s, int step) {
		return s.split(String.format("(?<=\\G.{%d})", step));
	}

	public static String removeFirstWord(String str) {
		return str.split(" ", 2)[1];
	}

}
