###########################################
# Language Models                         #
###########################################

Description: Package containing Forward Bigram, Backward Bigram, and Bidirectional Bigram language models. The Forward Bigram Model was written by Ray Mooney at UT Austin and features linear interpolation of a unigram model with weight 0.1 for basic smoothing and first-occurrence <UNK>-ification on the training set for handling out-of-vocabulary words. The Backward Bigram and Bidirectional Bigran language models appropriate this standard Forward Model in intuitive and well-documented ways. The Bidirectional Bigram model linearly interpolates both the Forward and Backward models and offers hooks for specifying the interpolation weights for the respective models.

###########################################
# To Run                                  #
###########################################

To run Bigram Model:

javac lm/*.java
java lm.BigramModel [DIR]* [TestFrac]

To run Backward Bigram Model:

javac lm/*.java
java lm.BackwardBigramModel [DIR]* [TestFrac]

To run Bidirectional Bigram Model:

javac lm/*.java
java lm.BidirectionalBigramModel

DIR      -- name of a file or directory with POS tagged files for input.
TestFrac -- fraction of sentences in the data to be used for testing.

Running any of these models will produce a trace with meta-information about the training and testing phases, including two measures of Perplexity: "perplexity" and "word perplexity." Perplexity is the average standard perplexity of sentences in the indicated test set, while word perplexity ignores sentence boundaries in its computation.

###########################################
# Included Source Files                   #
###########################################

BigramModel.java              -- Implementation of standard Bigram Model.

BackwardBigramModel.java      -- Implementation of Backward Bigram Model using the standard Bigram Model.

BidirectionalBigramModel.java -- Implementation of Bidirectional Bigram Model using the standard and Backward Bigram models.

POSTaggedFile.java            -- Pre-processing file used to strip POS tags from the training data.

DoubleValue.java              -- Helpful wrapper for doubles used by BigramModel.

###########################################
# Other Included Files                    #
###########################################

In the traces directory, you will find a collection of sample traces.