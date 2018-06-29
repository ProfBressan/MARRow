package classifiers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import data.Sample;
import data.HandleData;
import weka.classifiers.lazy.IBk;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Classifiers {
	public Classifiers() {

	}

	public void classifyKNN(int k, ArrayList<Sample> samples, HandleData md) throws Exception {
		md.saveArqWeka(md.z1, "temp/training");
		Instances treinoInst = startInstaces("temp/training.arff");
		if (treinoInst == null) {
			System.out.println("Instancias error!");
			return;
		}
		treinoInst.setClassIndex(treinoInst.numAttributes() - 1);
		IBk knn = new IBk(k);
		knn.buildClassifier(treinoInst);
		for (int i = 0; i < samples.size(); i++) {
			int atributos = samples.get(i).getFeatures().size() + 1;
			Instance newInst = new DenseInstance(atributos);
			newInst.setDataset(treinoInst);
			for (int j = 0; j < samples.get(i).getFeatures().size(); j++) {
				newInst.setValue(j, samples.get(i).getFeatures().get(j));
			}
			double pred = knn.classifyInstance(newInst);
			samples.get(i).setPreditiveClass((int) (pred));
		}
	}

	private static Instances startInstaces(String nameDataset) {
		BufferedReader br;
		Instances instace;
		try {
			br = new BufferedReader(new FileReader(nameDataset));
			instace = new Instances(br);
			br.close();

		} catch (IOException e) {
			System.out.println("Error - read dataset!");
			e.printStackTrace();
			return null;
		}
		return instace;

	}

}
