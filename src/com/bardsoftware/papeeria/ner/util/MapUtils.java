package com.bardsoftware.papeeria.ner.util;

import java.util.Map;

public class MapUtils {

	public static <K> void addToMap(Map<K, Float> mapToAddIn, K keyToAdd, Float valueToAdd) {
		if (mapToAddIn.containsKey(keyToAdd)) {
			mapToAddIn.put(keyToAdd, mapToAddIn.get(keyToAdd) + valueToAdd);
		} else {
			mapToAddIn.put(keyToAdd, valueToAdd);
		}
	}

	public static <K> void mergeTwoMaps(Map<K, Float> toMergeIn, Map<K, Float> toBeMerged) {
		for (Map.Entry<K, Float> entry : toBeMerged.entrySet()) {
			addToMap(toMergeIn, entry.getKey(), entry.getValue());
		}
	}
}
