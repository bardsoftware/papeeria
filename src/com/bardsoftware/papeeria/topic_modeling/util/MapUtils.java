package com.bardsoftware.papeeria.topic_modeling.util;

import java.util.Map;

public final class MapUtils {

	private MapUtils() {
	}

	public static <K> void addToMap(Map<K, Float> mapToAddIn, K keyToAdd, Float valueToAdd) {
		mapToAddIn.put(keyToAdd, mapToAddIn.getOrDefault(keyToAdd, 0f) + valueToAdd);
	}

	public static <K> void mergeTwoMaps(Map<K, Float> toMergeIn, Map<? extends K, ? extends Float> toBeMerged) {
		toBeMerged.entrySet().forEach(e -> addToMap(toMergeIn, e.getKey(), e.getValue()));
	}
}
