package start;

import cbir.CbirAL;
import data.HandleData;

public class StartProgram {

	public static void main(String[] args) throws Exception {
		/*
		 * Configuration
		 */
		// Number of desired iterations
		int maxIteration = 8;

		// Number of samples for evaluation at each iteration
		int maxSampleOracle = 7;

		// Choice of distance function
		// 1 - L1 | 2 - L2
		// 3 - L_inf | 4 - X2
		// 5 - Canberra | 6 - Jeffrey
		// 7 - dLog
		int distanceFunction = 2;

		// value of the query sample percentage.
		// 10 = 10% ... 100 = 100%
		int percentQuerySample = 100;

		/*
		 * Start Program
		 */

		// read the dataset from the directory (dataset /)
		HandleData md = new HandleData("iris.txt");

		// Normalizes (MIN-MAX)
		md.normalizeData();

		// Organization of the query samples
		md.searchPercentQuerySamples(percentQuerySample);
		System.out.println("Quantity of query sample : " + md.getZQuerySamples().size());

		// Start CBIR+AL
		System.out.println("############################################# ");
		System.out.println("START CBIR AL (MARRow)");
		md.clear();
		CbirAL cbirAL = new CbirAL();
		cbirAL.start(md, maxIteration, maxSampleOracle, distanceFunction);
		System.out.println("END CBIR AL (MARRow)");
	}
}
