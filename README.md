# MARRow
The MARRow project is dedicated empirical studies to Active learning in Content-Based Image Retrieval.

# Intodution

inpute - We have as input the described features of imagens. 

output - Precision Vs Recall of proposed approach

# Test
 - Import the folder (MARRow) project to eclipse.
 - Add External JARs (libraries/weka.jar)
 - Run the (StartProgram.java)

# New experiment
 - Add to the (dataset) directory the dataset.
 -- See, for example, the format contained in the (dataset/iris.txt)
 - In (StartProgram.java), configure:
 - Number of desired iterations in (maxIteration)
 - Number of samples for evaluation at each iteration in (maxSampleOracle)
 - Choice of distance function in (distanceFunction)
 - Value used of the query samples percentage (percentQuerySample)
 - Example (percentQuerySample = 10) 10% of samples will be used as query samples.

# Obs. 
The (ImagesDescriptors) directory contains a Java application for extracting features from images, in case you need.
