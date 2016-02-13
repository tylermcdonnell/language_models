package lm;

import java.util.Arrays;
import java.util.LinkedList;
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
		for (List<String> sentence : sentences) {
		    List<Double> forwardProbs  = new LinkedList<Double>(Arrays.asList(forwardModel.sentenceTokenProbs(sentence)));
		    List<Double> backwardProbs = new LinkedList<Double>(Arrays.asList(backwardModel.sentenceTokenProbs(sentence)));
		    
		    assert(forwardProbs.size() == backwardProbs.size());
		    assert(forwardProbs.size() == sentence.size() + 1);
		    
		    // Include End-of-Sentence.
		    totalNumTokens += sentence.size() + 1;
		    
		    for(int i = 0; i < forwardProbs.size() && i < backwardProbs.size(); i++)
		    {
		    	totalLogProb += Math.log(interpolate(forwardProbs.get(i), backwardProbs.get(i)));
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
		for (List<String> sentence : sentences) {
		    List<Double> forwardProbs  = new LinkedList<Double>(Arrays.asList(forwardModel.sentenceTokenProbs(sentence)));
		    List<Double> backwardProbs = new LinkedList<Double>(Arrays.asList(backwardModel.sentenceTokenProbs(sentence)));
		    
		    assert(forwardProbs.size() == backwardProbs.size());
		    assert(forwardProbs.size() == sentence.size() + 1);
		    
		    // Don't include End-of-Sentence.
		    totalNumTokens += sentence.size();
		    forwardProbs.remove(forwardProbs.size() - 1);
		    backwardProbs.remove(backwardProbs.size() - 1);
		    
		    for(int i = 0; i < forwardProbs.size() && i < backwardProbs.size(); i++)
		    {
		    	totalLogProb += Math.log(interpolate(forwardProbs.get(i), backwardProbs.get(i)));
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
}
