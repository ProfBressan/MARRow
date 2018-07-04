# MARRow (Medical Active leaRning Retrieval)
The MARRow project is dedicated empirical studies to Active learning in Content-Based Image Retrieval.

# Intodution 
This proposal consists of efficiently locating the most informative samples in the CBIR process. For this, in the first iteration, N samples are located to be labeled by the expert, this process is simulated automatically by the system, thus, the N samples of the first iteration are formed by 1/3 of samples closer to the query sample and 2/3 of the medoids samples withdrawn from the klusters. After this labeling, the K-NN classifier is trained, classifying the whole set with labels (1 - Revive Sample) or (0 - Irrelevant Sample). Basically, in the next iterations, the algorithm performs the search for more informative samples to training the classification and to improve the recovery process of the others. The most informative samples are the ones that contain the nearest neighbors with a different label.

Experiments are easy to perform, consider:
- inpute - We have as input the described features of imagens. 
- output - Precision Vs Recall of proposed approach

# Test
 - Import the folder (MARRow) project to eclipse.
 - Add External JARs (libraries/weka.jar)
 - Run the (StartProgram.java)
 - The output (report_xxx.txt) file will be generated in the project directory (MARRow).

# New experiment
 - Add to the (dataset) directory the dataset. See, for example, the format contained in the (dataset/iris.txt)
 - In (StartProgram.java), configure:
 - Number of desired iterations in (maxIteration)
 - Number of samples for evaluation at each iteration in (maxSampleOracle)
 - Choice of distance function in (distanceFunction)
 - Value used of the query samples percentage (percentQuerySample). Example (percentQuerySample = 10) 10% of samples will be used as query samples.

# Obs. 
The (ImagesDescriptors) directory contains a Java application for extracting features from images, in case you need.
