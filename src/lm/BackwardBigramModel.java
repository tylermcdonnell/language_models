package lm;

import java.util.ArrayList;
import java.util.Collections;
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
		return super.sentenceTokenProbs(reverseSentence(sentence));
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
