package lm;

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
}
