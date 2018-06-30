package data;

import java.io.File;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
//import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class HandleData {
	private String nameDataset = "";
	private ArrayList<Sample> z = null;
	public ArrayList<Sample> z2 = null;
	public ArrayList<Sample> z1 = null;
	public ArrayList<Sample> z3 = null;
	private ArrayList<Sample> zQuerySamples = null;
	private int numSamples = 0;
	private int numClasses = 0;
	private int numFeatures = 0;
	public int[][] matrizTest = null;
	public int linhaMatriz;
	public int colunaMatriz;

	public HandleData(String nomeBase) {
		this.setNameDataset(nomeBase);
		readDataset(this.getNameDataset());
		this.z2 = new ArrayList<Sample>();
		this.z1 = new ArrayList<Sample>();
		this.z3 = new ArrayList<Sample>();
		searchNumSamplesPerClass();
	}

	public ArrayList<Sample> getZ2() {
		return z2;
	}

	public void setZ2(ArrayList<Sample> z2) {
		this.z2 = z2;
	}

	public ArrayList<Sample> getZ1() {
		return this.z1;
	}

	public void setZ1(ArrayList<Sample> z1) {
		this.z1 = z1;
	}

	public ArrayList<Sample> getZ3() {
		return this.z3;
	}

	public void setZ3(ArrayList<Sample> z3) {
		this.z3 = z3;
	}

	public int getNumeroElementos() {
		return numSamples;
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	public int getNumClasses() {
		return numClasses;
	}

	public void setNumClasses(int numClasses) {
		this.numClasses = numClasses;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public String getNameDataset() {
		return nameDataset;
	}

	public void setNameDataset(String nameDataset) {
		this.nameDataset = nameDataset;
	}

	public ArrayList<Sample> getZ() {
		return z;
	}

	public void setZ(ArrayList<Sample> samples) {
		this.z = samples;
	}

	public ArrayList<Sample> getZQuerySamples() {
		return this.zQuerySamples;
	}

	public void setZusca(ArrayList<Sample> samples) {
		this.zQuerySamples = samples;
	}

	public void readDataset(String nameDataset) {

		this.z = new ArrayList<Sample>();

		ArrayList<String> inRead = new ArrayList<String>();
		File file = new File("dataset/" + nameDataset);
		try {
			Scanner arq = new Scanner(file);
			while (arq.hasNextLine()) {
				String linha = arq.nextLine();
				if (linha.contains("@") || linha.startsWith("%") || linha.equals("")) {
					// do nothing
				} else {
					inRead.add(linha);
				}
			}
			arq.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		for (int i = 0; i < inRead.size(); i++) {

			if (i == 0) {
				String[] s = inRead.get(i).split("\\s+");
				this.numSamples = Integer.parseInt(s[0]);
				this.numClasses = Integer.parseInt(s[1]);
				this.numFeatures = Integer.parseInt(s[2]);
			} else {
				String[] s = inRead.get(i).split("\\s+");
				ArrayList<Double> featuresE = new ArrayList<Double>();
				int numeroE = 0;
				String classeE;
				numeroE = Integer.parseInt(s[0]);
				classeE = s[1];
				for (int j = 2; j < s.length; j++) {
					featuresE.add(Double.parseDouble(s[j].replaceAll(",", ".")));
				}
				this.z.add(new Sample(featuresE, numeroE, classeE));
			}
		}
		Set<String> set = new HashSet<>();
		for (int i = 0; i < z.size(); i++) {
			set.add(z.get(i).classString);
		}
		Iterator<String> it = set.iterator();
		ArrayList<String> classesIt = new ArrayList<String>();
		while (it.hasNext()) {
			String valor = it.next();
			classesIt.add(valor);
		}
		for (int i = 0; i < z.size(); i++) {
			for (int j = 0; j < classesIt.size(); j++) {
				if (z.get(i).classString.equals(classesIt.get(j))) {
					z.get(i).classe = j + 1;
					z.get(i).numOfSample = i;
				}
			}

		}
		System.out.println("######### Input elements analysis ###########");
		System.out.println("Name dataset       : " + this.nameDataset);
		System.out.println("Number of samples  : " + this.numSamples);
		System.out.println("Number of classes  : " + this.numClasses);
		System.out.println("Number of features : " + this.numFeatures);
		System.out.println("#############################################");

	}

	public void normalizeData() {
		// Mim-Max
		int valueMaxVector = z.get(0).getFeatures().size() + 1;
		double[] max = new double[valueMaxVector];
		double[] min = new double[valueMaxVector];

		for (int k = 0; k < (valueMaxVector - 1); k++) {
			max[k] = z.get(0).getFeatures().get(k);
			min[k] = z.get(0).getFeatures().get(k);
			for (int i = 0; i < z.size(); i++) {
				if (z.get(i).getFeatures().get(k) > max[k]) {
					max[k] = z.get(i).getFeatures().get(k);
				}
				if (z.get(i).getFeatures().get(k) < min[k]) {
					min[k] = z.get(i).getFeatures().get(k);
				}
			}
		}
		for (int k = 0; k < z.get(0).getFeatures().size(); k++) {
			for (int i = 0; i < z.size(); i++) {
				if ((max[k] - min[k]) != 0) {
					double novoValor = ((z.get(i).getFeatures().get(k) - min[k]) / (max[k] - min[k]));
					z.get(i).feature.set(k, novoValor);
				} else {
					z.get(i).feature.set(k, 0.0);
				}
			}
		}
	}

	public void orderDistance(ArrayList<Sample> samplesList, Sample sample, int opFuncDist) {

		if (opFuncDist <= 1) {
			// Manhatam
			// |x1 - x2| + |y1 - y2|
			double resp = 0.0;
			double soma = 0.0;
			for (int i = 0; i < samplesList.size(); i++) {
				for (int j = 0; j < sample.getFeatures().size(); j++) {
					resp = Math.abs(sample.getFeatures().get(j) - samplesList.get(i).getFeatures().get(j));
					soma += resp;
				}
				samplesList.get(i).setDistance(soma);
				resp = 0.0;
				soma = 0.0;
			}
			Collections.sort(samplesList);
		} else if (opFuncDist == 2) {
			// Euclidiana
			// Raiz ((x1 - x2)^2 + (y1 - y2)^2)
			double resp = 0.0;
			double soma = 0.0;
			for (int i = 0; i < samplesList.size(); i++) {
				for (int j = 0; j < sample.getFeatures().size(); j++) {
					resp = Math.pow((sample.getFeatures().get(j) - samplesList.get(i).getFeatures().get(j)), 2);
					soma += resp;
				}
				samplesList.get(i).setDistance(Math.sqrt(soma));
				resp = 0.0;
				soma = 0.0;
			}
			Collections.sort(samplesList);
		} else if (opFuncDist == 3) {
			// R = Infinito (Chebyshev)
			double maxDiferenca = 0.0;
			double diferenca = 0.0;
			for (int i = 0; i < samplesList.size(); i++) {
				maxDiferenca = 0.0;
				diferenca = 0.0;
				for (int j = 0; j < sample.getFeatures().size(); j++) {
					if (Math.abs(sample.getFeatures().get(j) - samplesList.get(i).getFeatures().get(j)) != 0) {
						diferenca = (double) Math
								.abs(sample.getFeatures().get(j) - samplesList.get(i).getFeatures().get(j));
						if (diferenca > maxDiferenca) {
							maxDiferenca = (double) diferenca;
						}
					}
				}
				samplesList.get(i).setDistance(maxDiferenca);
			}
			Collections.sort(samplesList);

		} else if (opFuncDist == 4) {
			// X2
			double soma = 0.0;
			double mi = 0.0;
			double resp = 0.0;
			double dist = 0.0;
			for (int i = 0; i < samplesList.size(); i++) {

				for (int j = 0; j < sample.getFeatures().size(); j++) {

					if ((sample.getFeatures().get(j) + samplesList.get(i).getFeatures().get(j)) != 0) {

						mi = (sample.getFeatures().get(j) + samplesList.get(i).getFeatures().get(j)) / 2.0;

						resp = Math.pow((samplesList.get(i).getFeatures().get(j) - mi), 2.0) / mi;
						soma += resp;
					} else {
						resp = 0.0;
						soma += resp;
					}

				}
				dist = (double) soma;
				samplesList.get(i).setDistance(dist);
				soma = 0.0;
				dist = 0.0;
				mi = 0.0;
			}
			Collections.sort(samplesList);

		} else if (opFuncDist == 5) {
			// Canberra
			// |x1 - x2| / (|x1|+|x2|)
			double soma = 0.0;
			double resp = 0.0;
			double dist = 0.0;
			double pi;
			double qi;
			double abspi;
			double absqi;
			for (int i = 0; i < samplesList.size(); i++) {
				for (int j = 0; j < sample.getFeatures().size(); j++) {

					pi = sample.feature.get(j) + 0.1;
					qi = samplesList.get(i).feature.get(j) + 0.1;
					abspi = Math.abs(sample.getFeatures().get(j)) + 0.1;
					absqi = Math.abs(samplesList.get(i).getFeatures().get(j) + 0.1);

					if (Math.abs(abspi + absqi) != 0) {
						resp = Math.abs(pi - qi) / (abspi + absqi);
						soma += resp;
					}

				}
				dist = soma;
				samplesList.get(i).setDistance(dist);
				soma = 0.0;
				resp = 0.0;
				dist = 0.0;
			}
			Collections.sort(samplesList);

		} else if (opFuncDist == 6) {
			// Divergencia de Jeffrey
			double soma = 0.0;
			double mi = 0.0;
			double resp = 0.0;
			double dist = 0.0;
			double v1 = 0.0;
			double v2 = 0.0;
			double x;
			double y;
			for (int i = 0; i < samplesList.size(); i++) {
				soma = 0.0;
				v1 = 0.0;
				v2 = 0.0;
				mi = 0.0;
				for (int j = 0; j < sample.getFeatures().size(); j++) {
					x = Math.abs(sample.getFeatures().get(j));
					y = Math.abs(samplesList.get(i).getFeatures().get(j));
					mi = (double) ((x + y) / 2.0);
					if (mi != 0) {
						if (x != 0) {
							v1 = x * Math.log((x / mi));
						} else {
							v1 = 0.0;
						}

						if (y != 0) {
							v2 = y * Math.log((y / mi));
						} else {
							v2 = 0.0;
						}

						resp = (v1 + v2);
						soma += (double) resp;
					}
				}
				dist = (double) soma;
				samplesList.get(i).setDistance(dist);
			}

			Collections.sort(samplesList);

		} else {
			// dLog
			float soma = 0.0f;
			double abspi;
			double abspj;
			double resp;
			for (int i = 0; i < samplesList.size(); i++) {
				soma = 0.0f;
				for (int j = 0; j < sample.getFeatures().size(); j++) {
					abspi = Math.abs(sample.getFeatures().get(j)) + 1;
					abspj = Math.abs(samplesList.get(i).getFeatures().get(j)) + 1;
					resp = Math.abs(dLog(abspi) - dLog(abspj));
					soma += resp;
				}
				samplesList.get(i).setDistance(soma);
			}
			Collections.sort(samplesList);
		}

	}

	private static double dLog(double valor) {
		if (valor == 0) {
			return 0;
		} else {
			return ((Math.log(valor) / Math.log(2)) + 1);
		}
	}

	public void saveArqWeka(ArrayList<Sample> samples, String name) throws IOException {
		FileWriter arq = new FileWriter(name + ".arff");
		PrintWriter gravarArq = new PrintWriter(arq);
		gravarArq.printf("%% %n%% %n");
		gravarArq.printf("@RELATION CBIRAL %n");
		for (int i = 1; i <= samples.get(0).getFeatures().size(); i++) {
			gravarArq.printf("@ATTRIBUTE %d REAL%n", i);
		}
		gravarArq.printf("@ATTRIBUTE class  {0, 1}");
		gravarArq.printf("\n%n");
		gravarArq.printf("@DATA %n");

		for (int i = 0; i < samples.size(); i++) {
			for (int j = 0; j < samples.get(i).getFeatures().size(); j++) {
				gravarArq.printf(samples.get(i).getFeatures().get(j) + ",");
			}
			int c = samples.get(i).getPreditiveClass();
			if (c == 0) {
				c = 0;
			} else {
				c = 1;
			}
			gravarArq.printf("%d%n", c);
		}
		gravarArq.printf("%% %n%% %n%% %n");
		arq.close();
	}

	public void saveArqWekaCluster(ArrayList<Sample> elementos, String nome) throws IOException {
		FileWriter arq = new FileWriter(nome + ".arff");
		PrintWriter gravarArq = new PrintWriter(arq);
		gravarArq.printf("%% %n%% %n");
		gravarArq.printf("@RELATION CBIRAL %n");

		for (int i = 1; i <= elementos.get(0).getFeatures().size(); i++) {
			gravarArq.printf("@ATTRIBUTE %d REAL%n", i);
		}

		gravarArq.printf("@ATTRIBUTE class  {");
		for (int i = 1; i <= getNumClasses() - 1; i++) {
			gravarArq.printf(i + ",");
		}
		gravarArq.printf(getNumClasses() + "}");
		gravarArq.printf("\n%n");
		gravarArq.printf("@DATA %n");

		for (int i = 0; i < elementos.size(); i++) {
			for (int j = 0; j < elementos.get(i).getFeatures().size(); j++) {
				gravarArq.printf(elementos.get(i).getFeatures().get(j) + ",");
			}
			gravarArq.printf("%s\n", elementos.get(i).getClasse());
		}
		gravarArq.printf("%% %n%% %n%% %n");
		arq.close();
	}

	public void originalBack() {
		// Volta os elementos de Z1 (treino) para Z2 (aprendizado)
		for (int i = 0; i < this.z1.size(); i++) {
			this.z2.add(this.z1.get(i));
		}
		// Apaga os dados de Z1
		this.z1.clear();

		// Deleta classes Preditivas
		for (int i = 0; i < this.z2.size(); i++) {
			this.z2.get(i).setPreditiveClass(100);
		}
		for (int i = 0; i < this.z1.size(); i++) {
			this.z1.get(i).setPreditiveClass(100);
		}

	}

	public void searchNumSamplesPerClass() {

		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < this.z.size(); i++) {
			set.add(this.z.get(i).getClasse());
		}
		matrizTest = new int[set.size()][12];
		this.linhaMatriz = set.size();
		this.colunaMatriz = 12;
		Iterator<Integer> it = set.iterator();
		int k = 0;
		while (it.hasNext()) {
			int valor = it.next();
			matrizTest[k][0] = valor;
			k++;
		}
		for (int i = 0; i < set.size(); i++) {
			int contador = 0;
			for (int j = 0; j < this.z.size(); j++) {
				if (this.z.get(j).getClasse() == matrizTest[i][0]) {
					contador++;
				}
			}
			matrizTest[i][1] = contador;
		}
		for (int i = 0; i < set.size(); i++) {
			int porcentagem = 0;
			for (int j = 2; j < 12; j++) {
				if (j == 2) {
					if (((int) matrizTest[i][1] / 10) == 0) {
						matrizTest[i][j] = 1;
					} else {
						matrizTest[i][j] = (int) matrizTest[i][1] / 10;
					}
					porcentagem = matrizTest[i][2];
				} else {
					if (j == 11) {
						matrizTest[i][j] = matrizTest[i][1];
					} else {
						if (porcentagem == 1) {
							if (porcentagem * (j - 1) > matrizTest[i][1]) {
								matrizTest[i][j] = matrizTest[i][1];
							} else {
								matrizTest[i][j] = porcentagem * (j - 1);
							}

						} else {
							matrizTest[i][j] = porcentagem * (j - 1);
						}

					}

				}

			}
		}
		for (int i = 0; i < set.size(); i++) {
			for (int j = 0; j < 12; j++) {
				System.out.print(matrizTest[i][j] + "  ");
			}
			System.out.println(" ");
		}

	}

	public void searchPercentQuerySamples(int porcBusca) {

		// Controle de porcentagem
		int controle;
		if (porcBusca >= 100) {
			controle = 11;
		} else if (porcBusca >= 90) {
			controle = 10;
		} else if (porcBusca >= 80) {
			controle = 9;
		} else if (porcBusca >= 70) {
			controle = 8;
		} else if (porcBusca >= 60) {
			controle = 7;
		} else if (porcBusca >= 50) {
			controle = 6;
		} else if (porcBusca >= 40) {
			controle = 5;
		} else if (porcBusca >= 30) {
			controle = 4;
		} else if (porcBusca >= 20) {
			controle = 3;
		} else {
			controle = 2;
		}

		this.zQuerySamples = new ArrayList<Sample>();
		int contaAmostrasBusca = 0;

		// Collections.shuffle(this.z);

		for (int j = 0; j < this.linhaMatriz; j++) {
			contaAmostrasBusca = 0;
			for (int i = 0; i < this.z.size(); i++) {

				if (this.z.get(i).getClasse() == this.matrizTest[j][0])
					if (contaAmostrasBusca <= this.matrizTest[j][controle]) { // this.matrizTeste[j][controle]
						this.zQuerySamples.add(this.z.get(i));
						contaAmostrasBusca++;
					}
			}
		}

	}

	public void clear() {
		this.z1.clear();
		this.z2.clear();
		this.z3.clear();
	}

}
