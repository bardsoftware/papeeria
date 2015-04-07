package com.bardsoftware.papeeria.ner.util;

import java.util.Map;

public class MapUtils {

	public static void addToMap(Map<String, Float> mapToAddIn, String keyToAdd, Float valueToAdd) {
		if (mapToAddIn.containsKey(keyToAdd)) {
			mapToAddIn.put(keyToAdd, mapToAddIn.get(keyToAdd) + valueToAdd);
		} else {
			mapToAddIn.put(keyToAdd, valueToAdd);
		}
	}

	public static void mergeTwoMaps(Map<String, Float> toMergeIn, Map<String, Float> toBeMerged) {
		for (Map.Entry<String, Float> entry : toBeMerged.entrySet()) {
			addToMap(toMergeIn, entry.getKey(), entry.getValue());
		}
	}
}
