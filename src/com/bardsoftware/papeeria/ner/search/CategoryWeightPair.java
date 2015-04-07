package com.bardsoftware.papeeria.ner.search;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CategoryWeightPair implements Clusterable, Comparable<CategoryWeightPair> {
	private final String category;
	private final Float weight;

	private static final int NUMBER_OF_CLUSTERS = 3;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CategoryWeightPair)) return false;

		CategoryWeightPair that = (CategoryWeightPair) o;

		if (category != null ? !category.equals(that.category) : that.category != null) return false;
		if (weight != null ? !weight.equals(that.weight) : that.weight != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = category != null ? category.hashCode() : 0;
		result = 31 * result + (weight != null ? weight.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("%s : %f", category, weight);
	}

	public static List<CategoryWeightPair> sortByWeights(Map<String, Float> categoriesIntoWeights) {
		List<CategoryWeightPair> storage = new ArrayList<>();
		for (Map.Entry<String, Float> entry : categoriesIntoWeights.entrySet()) {
			storage.add(new CategoryWeightPair(entry));
		}
		Collections.sort(storage);
		return storage;
	}


	public static List<CategoryWeightPair> cluster(List<CategoryWeightPair> searchResult) {
		KMeansPlusPlusClusterer<CategoryWeightPair> clusterer = new KMeansPlusPlusClusterer<>(NUMBER_OF_CLUSTERS, 1000);
		List<CentroidCluster<CategoryWeightPair>> clusterResults = clusterer.cluster(searchResult);
		CentroidCluster<CategoryWeightPair> maxCluster = Collections.max(clusterResults, (c1, c2) ->
						Double.compare(c1.getCenter().getPoint()[0], c2.getCenter().getPoint()[0])
		);
		return maxCluster.getPoints();
	}

	public static void clusterAndPrintToStdOut(List<CategoryWeightPair> sorted) {
		sorted.forEach(System.out::println);
		System.out.println("\nClustering result:");
		cluster(sorted).forEach(System.out::println);
	}
}
