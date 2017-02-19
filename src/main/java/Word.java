import java.util.LinkedList;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Synset;


public class Word {
	String rawWord;
	String stem;
	
	IWord  word;
	IDictionary dict;
	
	public Word(IDictionary dict, String rawWord){
		this.rawWord = rawWord;
		this.dict = dict;
		word = getWord(dict, rawWord);
		if(rawWord.length() <= 2) word = null;
		
		//if(word != null) System.out.println(word.getLemma());
		/*
		if(word != null){
			System.out.println("Hyponeyms of " + word.getLemma());
			ISynset s = getSynset();
			for(ISynset relatedSet : getRelatedSynsets(s, Pointer.HYPERNYM, 0, 2)){
				System.out.println(relatedSet.toString());
			}
		}
		*/
	}
	
	
	public IWord getWord(IDictionary dict, String wordString){
		try{
		 IIndexWord idxWord = dict.getIndexWord (wordString, POS.VERB );
		 if(idxWord != null) return dict.getWord((IWordID)idxWord.getWordIDs().get(0));
		 
		 idxWord = dict.getIndexWord (wordString, POS.NOUN );
		 if(idxWord != null) return dict.getWord((IWordID)idxWord.getWordIDs().get(0));
		 
		 idxWord = dict.getIndexWord (wordString, POS.ADJECTIVE );
		 if(idxWord != null) return dict.getWord((IWordID)idxWord.getWordIDs().get(0));
		 
		 idxWord = dict.getIndexWord (wordString, POS.ADVERB );
		 if(idxWord != null) return dict.getWord((IWordID)idxWord.getWordIDs().get(0));
		}catch(Exception e){
			return null;
		}
		 return null;
	}
	
	public ISynset getSynset(){
		if(word == null) return null;
		return word.getSynset();
	}
	
	public List<ISynset> getRelatedSynsets(ISynset synset, Pointer p, int level, int maxlevel){
		List<ISynsetID> relatedSetsIDs = synset.getRelatedSynsets(p);
		List<ISynset> relatedSets = new LinkedList<ISynset>();
		 for(ISynsetID s : relatedSetsIDs){
			 ISynset set = dict.getSynset(s);
			 relatedSets.add(set);
			 if(level < maxlevel){
				 List<ISynset> nextLevel = getRelatedSynsets(set, p, level+1, maxlevel);
				 for(ISynset nextLevelMerge : nextLevel){
					 relatedSets.add(nextLevelMerge);
				 }
			 }
		 }
		
		return relatedSets;
	}
	
	public String longestWord(List<ISynset> sets){
		String longestWord = rawWord;
		for(ISynset s : sets){
			for(IWord w : s.getWords()){
				if(longestWord.length() < w.getLemma().length()){
					longestWord =  w.getLemma();
				}
			}
		}
		return longestWord;
	}
	
	public String shortestWord(List<ISynset> sets){
		String longestWord = rawWord;
		for(ISynset s : sets){
			for(IWord w : s.getWords()){
				if(longestWord.length() > w.getLemma().length()){
					longestWord =  w.getLemma();
				}
			}
		}
		return longestWord;
	}
	
	public String getWordReplacement(boolean longest, Pointer relationship, int level){
		if(word == null) return rawWord;
		List<ISynset> sets = new LinkedList<ISynset>();
		ISynset s = getSynset();
		if(relationship == null){
			sets.add(s);
		}else{
			sets = getRelatedSynsets(s, relationship, 0, level);
			
		}
		
		
		if(longest){
			return longestWord(sets).replace("_", " ");
		}else{
			return shortestWord(sets).replace("_", " ");
		}
	}
	
	
	public POS getPOS(){
		if(word == null) return null;
		return word.getPOS();
	}
	public void print(){
		System.out.println(rawWord);
	}

	
	
}
