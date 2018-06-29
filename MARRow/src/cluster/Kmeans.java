package cluster;

import java.util.ArrayList;

import data.Sample;
import data.HandleData;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Kmeans {
	private SimpleKMeans km;
	private Instances instances;
	private Instances instancesCentroids;

	public Kmeans() {

	}

	public void startCluster(HandleData md, int Kcluster) throws Exception {

		this.km = new SimpleKMeans();
		this.km.setNumClusters(Kcluster);
		md.saveArqWekaCluster(md.z2, "temp/cluster");
		this.instances = DataSource.read("temp/cluster.arff");
		this.km.buildClusterer(this.instances);
		for (int i = 0; i < this.instances.size(); i++) {
			md.z2.get(i).setPreditiveClass((int) this.km.clusterInstance(this.instances.get(i)));
		}
	}

	public void performsClusterZ1(HandleData md, int Kcluster) throws Exception {

		this.km = new SimpleKMeans();
		this.km.setNumClusters(Kcluster);
		md.saveArqWekaCluster(md.z1, "temp/cluster");
		this.instances = DataSource.read("temp/cluster.arff");
		this.km.buildClusterer(this.instances);
		for (int i = 0; i < this.instances.size(); i++) {
			md.z1.get(i).setPreditiveClass((int) this.km.clusterInstance(this.instances.get(i)));
		}
	}

	public void searchSampleCentroid(HandleData md, int funcaoDistancia, int classe) {
		this.instancesCentroids = this.km.getClusterCentroids();
		for (int i = 0; i < this.instancesCentroids.size(); i++) {
			ArrayList<Double> c = new ArrayList<Double>();

			for (int j = 0; j < this.instancesCentroids.get(i).numAttributes() - 1; j++) {
				c.add(Double.parseDouble(this.instancesCentroids.get(i).toString(j)));
			}
			Sample e = new Sample(c);
			md.orderDistance(md.z2, e, funcaoDistancia);
			if (md.z2.get(0).getClasse() == classe) {
				md.z2.get(0).setPreditiveClass(1);
			} else {
				md.z2.get(0).setPreditiveClass(0);
			}
			md.z1.add(md.z2.get(0));
			md.z2.remove(0);
		}
	}
}