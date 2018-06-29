package data;

import java.util.ArrayList;

public class Sample implements Comparable<Sample> {
	public ArrayList<Double> feature = new ArrayList<Double>();
	public int classe;
	public int numOfSample;
	public double distance = 0;
	public int cluster;
	private int preditiveClass;
	public String classString = "";

	public Sample(ArrayList<Double> caracteristicas, int numElemento, String classe) {
		super();
		this.feature = caracteristicas;
		this.numOfSample = numElemento;
		this.classString = classe;
	}

	public Sample(ArrayList<Double> features) {
		super();
		this.feature = features;
	}

	public Sample() {

	}

	public int getNumElemento() {
		return numOfSample;
	}

	public void setNumElemento(int numSample) {
		this.numOfSample = numSample;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public int getPreditiveClass() {
		return preditiveClass;
	}

	public void setPreditiveClass(int preditiveClass) {
		this.preditiveClass = preditiveClass;
	}

	public int getNumberOfSample() {
		return numOfSample;
	}

	public void setNumberOfSample(int numSamples) {
		this.numOfSample = numSamples;
	}

	public int getClasse() {
		return this.classe;
	}

	public void setClasse(int classe) {
		this.classe = classe;
	}

	public ArrayList<Double> getFeatures() {
		return feature;
	}

	public void setFeatures(ArrayList<Double> features) {
		this.feature = features;
	}

	public double getDistance() {
		return this.distance;
	}

	public void setDistance(double dist) {
		this.distance = dist;
	}

	@Override
	public int compareTo(Sample sample) {
		if (this.distance < sample.distance) {
			return -1;
		}
		if (this.distance > sample.distance) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Sample " + this.numOfSample + " [features=" + this.feature + "] \n";
	}
}
