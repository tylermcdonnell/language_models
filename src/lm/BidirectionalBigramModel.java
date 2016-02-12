package lm;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** 
 * @author TSM
 * A simple backward bigram language model that combines a standard
 * and backward bigram model for prediction. Linearly interpolates
 * probabilities of words using both forward and backward estimates.
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
		    totalNumTokens += sentence.size() + 1; // Yes end-of-sentence.
		    List<Double> forwardProbs  = new LinkedList<Double>(Arrays.asList(forwardModel.sentenceTokenProbs(sentence)));
		    List<Double> backwardProbs = new LinkedList<Double>(Arrays.asList(backwardModel.sentenceTokenProbs(sentence)));
		  
		    // Rearrange for interpolation.
		    rearrangeBackwardProbabilities(backwardProbs);
		    
		    assert(forwardProbs.size() == backwardProbs.size());
		    assert(forwardProbs.size() == sentence.size() + 1);
		    
		    for(int i = 0; i < forwardProbs.size(); i++)
		    {
		    	totalLogProb += Math.log(interpolate(forwardProbs.get(i), backwardProbs.get(i)));
		    }
		}
		double perplexity = Math.exp(-totalLogProb / totalNumTokens);
		System.out.println("Perplexity = " + perplexity );
	}
	
	@Override
	public void test2(List<List<String>> sentences)
	{
		Double totalLogProb   = 0.0;
		Double totalNumTokens = 0.0;
		for (List<String> sentence : sentences) {
		    totalNumTokens += sentence.size(); // No end-of-sentence.
		    List<Double> forwardProbs  = new LinkedList<Double>(Arrays.asList(forwardModel.sentenceTokenProbs(sentence)));
		    List<Double> backwardProbs = new LinkedList<Double>(Arrays.asList(backwardModel.sentenceTokenProbs(sentence)));
		    
		    // Rearrange for interpolation.
		    rearrangeBackwardProbabilities(backwardProbs);
		    
		    assert(forwardProbs.size() == backwardProbs.size());
		    assert(forwardProbs.size() == sentence.size() + 1);
		    
		    // For word perplexity, we ignore end-of-sentence probabilities.
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
	
	/*
	 * Rearranges the backward probabilities of a sentence for easy
	 * traversal and interpolation with forward counterparts.
	 */
	private void rearrangeBackwardProbabilities(List<Double> backwardProbs)
	{
	    // Observe that </S> will be out-of-place after a simple reverse.
	    //             Original: This is a sentence </S>
	    //             Reversed: sentence a is This </S>
	    //    Reversed Reversed: </S> This is a sentence
	    //                         |-----------------|
	    //         What We Want: This is a sentence </S>
	    Double tail = backwardProbs.get(backwardProbs.size() - 1);
	    backwardProbs.remove(backwardProbs.size() - 1);
	    Collections.reverse(backwardProbs);
	    backwardProbs.add(tail);
	}
	
	private double interpolate(double forwardProbability, double backwardProbability)
	{
		return (forwardProbability * forwardLambda) + 
			   (backwardProbability * backwardLambda);
	}
}
