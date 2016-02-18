package lm;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** 
 * @author TSM
 * A simple bidirectional bigram language model that linearly interpolates
 * predictions from a standard forward and backward bigram model.
 */

public class BidirectionalBigramModel extends BigramModel
{
	private BigramModel forwardModel;
	private BackwardBigramModel backwardModel;
	
	// Default interpolation weight for forward model.
	private double forwardLambda = 0.5;
	
	// Default interpolation weight for backward model.
	private double backwardLambda = 0.5;
	
	public BidirectionalBigramModel()
	{
		forwardModel  = new BigramModel();
		backwardModel = new BackwardBigramModel();
	}
	
	public BidirectionalBigramModel(double forwardLambda, double backwardLambda)
	{
		forwardModel  = new BigramModel();
		this.forwardLambda = forwardLambda;
		
		backwardModel = new BackwardBigramModel();
		this.backwardLambda = backwardLambda;	
	}
	
	@Override
	public void train(List<List<String>> sentences)
	{
		forwardModel.train(sentences);
		backwardModel.train(sentences);
	}
	
	@Override
	public void test(List<List<String>> sentences)
	{
		Double totalLogProb   = 0.0;
		Double totalNumTokens = 0.0;
		for (List<String> sentence : sentences) 
		{
			// For easy manipulation.
		    Double[] forwardProbs  = forwardModel.sentenceTokenProbs(sentence);
		    Double[] backwardProbs = backwardModel.sentenceTokenProbs(sentence);
		    
		    assert(forwardProbs.length == backwardProbs.length);
		    assert(forwardProbs.length == sentence.size() + 1);
		    
		    // Include End-of-Sentence.
		    int numTokens = sentence.size() + 1;
		    totalNumTokens += numTokens;
		    
		    for(int i = 0; i < numTokens; i++)
		    {
		    	totalLogProb += Math.log(interpolate(forwardProbs[i], backwardProbs[i]));
		    }
		}
		double perplexity = Math.exp(-totalLogProb / totalNumTokens);
		System.out.println("Word Perplexity = " + perplexity );
	}
	
	@Override
	public void test2(List<List<String>> sentences)
	{
		Double totalLogProb   = 0.0;
		Double totalNumTokens = 0.0;
		for (List<String> sentence : sentences) 
		{
		    Double[] forwardProbs  = forwardModel.sentenceTokenProbs(sentence);
		    Double[] backwardProbs = backwardModel.sentenceTokenProbs(sentence);
		    
		    assert(forwardProbs.length == backwardProbs.length);
		    assert(forwardProbs.length == sentence.size() + 1);
		    
		    // Don't include End-of-Sentence.
		    int numTokens = sentence.size();
		    totalNumTokens += numTokens;
		    
		    for(int i = 0; i < numTokens; i++)
		    {
		    	totalLogProb += Math.log(interpolate(forwardProbs[i], backwardProbs[i]));
		    }
		}
		double perplexity = Math.exp(-totalLogProb / totalNumTokens);
		System.out.println("Word Perplexity = " + perplexity );
	}
	
	private double interpolate(double forwardProbability, double backwardProbability)
	{
		return (forwardProbability * forwardLambda) + 
			   (backwardProbability * backwardLambda);
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
	BigramModel model = new BidirectionalBigramModel();
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
