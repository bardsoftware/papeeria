package com.bardsoftware.papeeria.ner;

import org.apache.commons.math3.ml.clustering.Clusterable;

import java.util.Map;

public class CategoryWeightPair implements Clusterable, Comparable<CategoryWeightPair> {
	private final String category;
	private final Float weight;

	public CategoryWeightPair(Map.Entry<String, Float> pair) {
		this.category = pair.getKey();
		this.weight = pair.getValue();
	}

	@Override
	public double[] getPoint() {
		return new double[]{weight};
	}

	@Override
	public int compareTo(CategoryWeightPair that) {
		return -Float.compare(this.weight, that.weight);
	}

	@Override
	public String toString() {
		return String.format("%s : %f", category, weight);
	}
}
