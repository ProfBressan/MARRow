package cbir;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import classifiers.Classifiers;
import cluster.Kmeans;
import data.Sample;
import data.HandleData;

public class CbirAL {
	private ArrayList<Sample> zQueryImagen = new ArrayList<Sample>();
	static double[][] report;
	static double meanSamplesAnalyzeds = 0;
	static int[][] vectorHits = new int[50][10];
	static int[][] vectorError = new int[50][10];

	public void start(HandleData md, int totalIteracao, int maxSampleOracle, int distanceFunction) throws Exception {
		for (int i = 0; i < md.getZQuerySamples().size(); i++) {
			this.zQueryImagen.add(md.getZQuerySamples().get(i));
		}
		int[] samplesAPI = new int[totalIteracao + 1];
		for (int i = 0; i < totalIteracao + 1; i++) {
			samplesAPI[i] = 0;
		}
		int sampleFirstIteration = (int) (maxSampleOracle / 3) * 2;
		int kcluster = maxSampleOracle - sampleFirstIteration;
		for (int i = 0; i < totalIteracao; i++) {
			for (int j = 0; j < 10; j++) {
				vectorHits[i][j] = 0;
				vectorError[i][j] = 0;
			}
		}
		FileWriter file = new FileWriter(
				"report_CBIR_AL_DistanceFunction_" + distanceFunction + "_" + md.getNameDataset());
		PrintWriter write = new PrintWriter(file);
		write.printf("report CBIR AL DistanceFunction " + distanceFunction + " " + md.getNameDataset() + " \n\n");

		for (int i = 0; i < zQueryImagen.size(); i++) {

			System.out.println("Query Sample : " + ((zQueryImagen.size()) - i));
			md.z1.clear();
			md.z2.clear();
			md.z3.clear();

			for (int ii = 0; ii < md.getZ().size(); ii++) {
				md.z3.add(md.getZ().get(ii));
				md.z2.add(md.getZ().get(ii));
			}

			Kmeans kmeans = new Kmeans();
			kmeans.startCluster(md, kcluster);

			kmeans.searchSampleCentroid(md, distanceFunction, this.zQueryImagen.get(i).getClasse());

			md.orderDistance(md.z2, this.zQueryImagen.get(i), distanceFunction);

			for (int j = sampleFirstIteration - 1; j >= 0; j--) {
				if (md.z2.get(j).getClasse() == this.zQueryImagen.get(i).getClasse()) {
					md.z2.get(j).setPreditiveClass(1);
				} else {
					md.z2.get(j).setPreditiveClass(0);
				}
				md.z1.add(md.z2.get(j));
				md.z2.remove(j);
			}
			samplesAPI[0] += md.z1.size();
			Classifiers classifier = new Classifiers();
			classifier.classifyKNN(1, md.z2, md);
			classifier.classifyKNN(1, md.z3, md);
			validReturns(this.zQueryImagen.get(i), md, 0, distanceFunction);

			for (int iteracao = 1; iteracao < totalIteracao; iteracao++) {

				activeLearning(this.zQueryImagen.get(i), md, maxSampleOracle, distanceFunction);

				samplesAPI[iteracao] += md.z1.size();

				classifier.classifyKNN(1, md.z2, md);
				classifier.classifyKNN(1, md.z3, md);

				validReturns(this.zQueryImagen.get(i), md, iteracao, distanceFunction);

			}

		}
		for (int i = 0; i < totalIteracao; i++) {
			write.printf("%d ", (i + 1));
			for (int j = 0; j < 10; j++) {
				double valorF = (double) (vectorHits[i][j] / (double) (vectorHits[i][j] + vectorError[i][j]));
				write.printf("%3.4f ", valorF);
			}
			write.printf("\n");
		}
		write.printf("\n Analyzed samples by iteration \n");
		for (int i = 0; i < totalIteracao; i++) {
			write.printf("\n iteration %d = %4.0f ", (i + 1),
					(double) ((double) samplesAPI[i] / (double) this.zQueryImagen.size()));
		}
		file.close();
	}

	private void activeLearning(Sample querySample, HandleData md, int maxSampleOracle, int funcDist) {
		int kneighbors = 1;
		int maxSamplesAnalyze = maxSampleOracle;
		ArrayList<Sample> suspectsSamples = new ArrayList<Sample>();
		ArrayList<Sample> analyzedSamples = new ArrayList<Sample>();
		for (int i = 0; i < md.z2.size(); i++) {
			analyzedSamples.add(md.z2.get(i));
		}
		if (md.z2.size() > kneighbors + 1) {
			for (int i = 0; i < (md.z2.size() - kneighbors); i++) {

				md.orderDistance(md.z2, analyzedSamples.get(i), funcDist);

				if (md.z2.get(0).getPreditiveClass() != md.z2.get(1).getPreditiveClass()) {
					if (checkExistence(suspectsSamples, md.z2.get(0)) == false) {

						suspectsSamples.add(md.z2.get(0));
					}
					if (checkExistence(suspectsSamples, md.z2.get(1)) == false) {

						suspectsSamples.add(md.z2.get(1));
					}

				}

			}
		}
		ArrayList<Sample> auxSuspectsSamples = new ArrayList<Sample>();
		ArrayList<Sample> auxE0 = new ArrayList<Sample>();
		ArrayList<Sample> auxE1 = new ArrayList<Sample>();
		ArrayList<Sample> auxZ1 = new ArrayList<Sample>();
		for (int i = 0; i < suspectsSamples.size(); i++) {
			auxSuspectsSamples.add(suspectsSamples.get(i));
		}
		for (int i = 0; i < md.z1.size(); i++) {
			auxZ1.add(md.z1.get(i));
		}
		for (int i = 0; i < auxSuspectsSamples.size(); i++) {
			md.orderDistance(auxZ1, auxSuspectsSamples.get(i), funcDist);
			if (auxZ1.get(0).getPreditiveClass() == 1) {
				auxE1.add(auxSuspectsSamples.get(i));
			} else {
				auxE0.add(auxSuspectsSamples.get(i));
			}
		}
		suspectsSamples.clear();
		md.orderDistance(auxE1, querySample, funcDist);
		for (int i = 0; i < auxE1.size(); i++) {
			suspectsSamples.add(auxE1.get(i));
		}
		md.orderDistance(auxE0, querySample, funcDist);
		for (int i = 0; i < auxE0.size(); i++) {
			suspectsSamples.add(auxE0.get(i));
		}
		if (suspectsSamples.size() < maxSamplesAnalyze) {
			ArrayList<Sample> samples1 = new ArrayList<Sample>();
			ArrayList<Sample> samples0 = new ArrayList<Sample>();
			for (int i = 0; i < analyzedSamples.size(); i++) {
				if (analyzedSamples.get(i).getPreditiveClass() == 1) {
					if (checkExistence(suspectsSamples, analyzedSamples.get(i)) == false) {
						samples1.add(analyzedSamples.get(i));
					}

				} else {
					if (checkExistence(suspectsSamples, analyzedSamples.get(i)) == false) {
						samples0.add(analyzedSamples.get(i));
					}

				}
			}
			Double[] vector1 = new Double[md.getNumFeatures()];
			for (int k = 0; k < md.getNumFeatures(); k++) {
				vector1[k] = 0.0;
			}
			for (int i = 0; i < samples1.size(); i++) {
				for (int k = 0; k < samples1.get(i).feature.size(); k++) {
					vector1[k] += samples1.get(i).feature.get(k);
				}
			}
			ArrayList<Double> c1 = new ArrayList<Double>();
			for (int i = 0; i < md.getNumFeatures(); i++) {
				vector1[i] = vector1[i] / samples1.size();
				c1.add(vector1[i]);
			}
			Sample centroide1 = new Sample(c1);
			md.orderDistance(samples0, centroide1, funcDist);
			md.orderDistance(samples1, centroide1, funcDist);
			int auxt = 1;
			int e = 0;
			while (auxt != 0) {
				if (samples0.size() == 0) {
					e = 1;
				}
				if (samples1.size() == 0) {
					e = 0;
				}

				if (e == 0 && samples0.size() != 0) {
					suspectsSamples.add(samples0.get(0));
					samples0.remove(0);
					e = 1;
				}
				if (e == 1 && samples1.size() != 0) {
					suspectsSamples.add(samples1.get(samples1.size() - 1));
					samples1.remove(samples1.size() - 1);
					e = 0;
				}
				if (samples0.size() == 0 && samples1.size() == 0) {
					auxt = 0;
				}
			}
		}
		if (suspectsSamples.size() < maxSamplesAnalyze) {
			maxSamplesAnalyze = suspectsSamples.size();
		}
		int controle = 1;
		int it = 0;
		while (controle <= maxSamplesAnalyze) {
			int numElemento = suspectsSamples.get(it).getNumElemento();
			for (int i = md.z2.size() - 1; i >= 0; i--) {
				if (md.z2.get(i).getNumElemento() == numElemento) {
					if (md.z2.get(i).getClasse() == querySample.getClasse()) {
						md.z2.get(i).setPreditiveClass(1);
					} else {
						md.z2.get(i).setPreditiveClass(0);
					}
					md.z1.add(md.z2.get(i));
					md.z2.remove(i);
				}
			}
			controle++;
			it++;
		}
	}

	private boolean checkExistence(ArrayList<Sample> elementosSuspeitos, Sample elemento) {
		for (int i = 0; i < elementosSuspeitos.size(); i++) {
			if (elemento.getNumElemento() == elementosSuspeitos.get(i).getNumElemento()) {
				return true;
			}
		}
		return false;
	}

	private void validReturns(Sample querySample, HandleData md, int iteration, int funcDist) throws Exception {
		ArrayList<Sample> auxZ3 = new ArrayList<Sample>();
		ArrayList<Sample> auxZ1 = new ArrayList<Sample>();
		ArrayList<Sample> samples1 = new ArrayList<Sample>();
		ArrayList<Sample> samples0 = new ArrayList<Sample>();
		ArrayList<Sample> samples1aux = new ArrayList<Sample>();
		ArrayList<Sample> samples0aux = new ArrayList<Sample>();
		ArrayList<Sample> exitSamples = new ArrayList<Sample>();

		for (int i = 0; i < md.z3.size(); i++) {
			auxZ3.add(md.z3.get(i));
		}

		for (int i = 0; i < md.z1.size(); i++) {
			auxZ1.add(md.z1.get(i));
		}

		for (int i = 0; i < auxZ1.size(); i++) {
			md.orderDistance(auxZ3, auxZ1.get(i), funcDist);
			if (auxZ1.get(i).getClasse() == querySample.getClasse()) {
				samples1.add(auxZ3.get(0));
				auxZ3.remove(0);
			} else {
				samples0.add(auxZ3.get(0));
				auxZ3.remove(0);
			}

		}
		Classifiers classifier = new Classifiers();
		classifier.classifyKNN(1, auxZ3, md);
		for (int i = 0; i < auxZ3.size(); i++) {
			md.orderDistance(auxZ1, auxZ3.get(i), funcDist);
			if (auxZ1.get(0).getPreditiveClass() == 1) {
				samples1aux.add(auxZ3.get(i));
			} else {
				samples0aux.add(auxZ3.get(i));
			}
		}
		md.orderDistance(samples0aux, querySample, funcDist);
		md.orderDistance(samples1aux, querySample, funcDist);
		for (int i = 0; i < samples1.size(); i++) {
			exitSamples.add(samples1.get(i));
		}
		for (int i = 0; i < samples1aux.size(); i++) {
			exitSamples.add(samples1aux.get(i));
		}
		for (int i = 0; i < samples0aux.size(); i++) {
			exitSamples.add(samples0aux.get(i));
		}
		for (int i = 0; i < samples0.size(); i++) {
			exitSamples.add(samples0.get(i));
		}
		int acerto, erro;
		for (int r = 2; r < 12; r++) {
			acerto = 0;
			erro = 0;
			int max = md.matrizTest[querySample.getClasse() - 1][r];
			for (int i = 0; i < max; i++) {

				if (exitSamples.get(i).getClasse() == querySample.getClasse()) {
					acerto++;
				} else {
					erro++;
				}
			}
			vectorHits[iteration][r - 2] += acerto;
			vectorError[iteration][r - 2] += erro;
		}
	}
}
