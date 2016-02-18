package lm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** 
 * @author TSM
 * A simple backward bigram language model that uses simple fixed-weight 
 * interpolation with a unigram model for smoothing. Backward means that
 * it applies the bigram model to sentences in reverse direction.
*/

public class BackwardBigramModel extends BigramModel 
{
	@Override
    public void train (List<List<String>> sentences) 
	{
		super.train(reverseSentences(sentences));
	}
	
	@Override
	public double sentenceLogProb (List<String> sentence)
	{
		return super.sentenceLogProb(reverseSentence(sentence));
	}
	
	@Override
	public double sentenceLogProb2 (List<String> sentence)
	{
		return super.sentenceLogProb2(reverseSentence(sentence));
	}
	
	@Override
    public Double[] sentenceTokenProbs (List<String> sentence)
    {
		// Return the probabilities in original token order.
		//
	    //               Original: This is a sentence </S>
	    // Backward Probabilities: sentence a is This </S>
	    //      Reversed Backward: </S> This is a sentence
	    //                           |-----------------|
	    //           What We Want: This is a sentence </S>
		Double[] backwardProbabilities = super.sentenceTokenProbs(reverseSentence(sentence));
		List<Double> asList = new LinkedList<Double>(Arrays.asList(backwardProbabilities));
	    Double tail = asList.get(asList.size() - 1);
	    asList.remove(asList.size() - 1);
	    Collections.reverse(asList);
	    asList.add(tail);
		return (Double[]) asList.toArray(new Double[asList.size()]);
	}
	
	public List<String> reverseSentence(List<String> sentence)
	{
		List<String> reversed = new ArrayList<String>(sentence);
		Collections.reverse(reversed);
		return reversed;
	}
	
	public List<List<String>> reverseSentences(List<List<String>> sentences)
	{
		List<List<String>> reversedSentences = new ArrayList<List<String>>();
		for(List<String> sentence : sentences)
		{
			reversedSentences.add(reverseSentence(sentence));
		}
		return reversedSentences;
	}
	
	
	
	
	// This code is just pasted here from parent class to ensure proper running.
    public static void main(String[] args) throws IOException {
	// All but last arg is a file/directory of LDC tagged input data
	File[] files = new File[args.length - 1];
	for (int i = 0; i < files.length; i++) 
	    files[i] = new File(args[i]);
	// Last arg is the TestFrac
	double testFraction = Double.valueOf(args[args.length -1]);
	// Get list of sentences from the LDC POS tagged input files
	List<List<String>> sentences = 	POSTaggedFile.convertToTokenLists(files);
	int numSentences = sentences.size();
	// Compute number of test sentences based on TestFrac
	int numTest = (int)Math.round(numSentences * testFraction);
	// Take test sentences from end of data
	List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
	// Take training sentences from start of data
	List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
	System.out.println("# Train Sentences = " + trainSentences.size() + 
			   " (# words = " + wordCount(trainSentences) + 
			   ") \n# Test Sentences = " + testSentences.size() +
			   " (# words = " + wordCount(testSentences) + ")");
	// Create a bigram model and train it.
	BigramModel model = new BackwardBigramModel();
	System.out.println("Training...");
	model.train(trainSentences);
	// Test on training data using test and test2
	model.test(trainSentences);
	model.test2(trainSentences);
	System.out.println("Testing...");
    // Test on test data using test and test2
	model.test(testSentences);
	model.test2(testSentences);
    }
}
