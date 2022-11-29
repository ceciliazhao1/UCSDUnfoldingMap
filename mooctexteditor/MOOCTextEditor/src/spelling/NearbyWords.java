package spelling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * @author UC San Diego Intermediate MOOC team
 * @modified by Dong Pei
 * @modified on June 2017
 */
public class NearbyWords implements SpellingSuggest {
	// THRESHOLD to determine how many words to look through when looking
	// for spelling suggestions (stops prohibitively long searching)
	// For use in the Optional Optimization in Part 2.
	private static final int THRESHOLD = 100000;

	Dictionary dict;

	public NearbyWords (Dictionary dict)
	{
		this.dict = dict;
	}

	/** Return the list of Strings that are one modification away
	 * from the input string.  
	 * @param s The original String
	 * @param wordsOnly controls whether to return only words or any String
	 * @return list of Strings which are nearby the original string
	 */
	public List<String> distanceOne(String s, boolean wordsOnly)  {
		List<String> retList = new ArrayList<String>();
		insertions(s, retList, wordsOnly);
		substitution(s, retList, wordsOnly);
		deletions(s, retList, wordsOnly);
		return retList;
	}


	/** Add to the currentList Strings that are one character mutation away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void substitution(String s, List<String> currentList, boolean wordsOnly) {
		// for each letter in the s and for all possible replacement characters
		for(int index = 0; index < s.length(); index++){
			for(int charCode = (int)'a'; charCode <= (int)'z'; charCode++) {
				// use StringBuffer for an easy interface to permuting the 
				// letters in the String
				StringBuffer sb = new StringBuffer(s);
				sb.setCharAt(index, (char)charCode);

				// if the item isn't in the list, isn't the original string, and
				// (if wordsOnly is true) is a real word, add to the list
				if(!currentList.contains(sb.toString()) &&
						(!wordsOnly||dict.isWord(sb.toString())) &&
						!s.equals(sb.toString())) {
					currentList.add(sb.toString());
				}
			}
		}
	}

	/** Add to the currentList Strings that are one character insertion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void insertions(String s, List<String> currentList, boolean wordsOnly ) {
		for(int index = 0; index <= s.length(); index++){
			for(int charCode = (int)'a'; charCode <= (int)'z'; charCode++) {
				StringBuffer sb = new StringBuffer(s);

				// this line changed to insert()
				sb.insert(index, (char)charCode);

				// Two criteria: 1. if the item isn't in the list, and 2.
				// (if wordsOnly is true) is a real word, add to the list
				if(!currentList.contains(sb.toString()) &&
						(!wordsOnly||dict.isWord(sb.toString()))) {
					currentList.add(sb.toString());
				}
			}
		}
	}

	/** Add to the currentList Strings that are one character deletion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void deletions(String s, List<String> currentList, boolean wordsOnly ) {
		for(int index = 0; index < s.length(); index++){
			StringBuffer sb = new StringBuffer(s);

			// this line changed to insert()
			sb.deleteCharAt(index);

			// Two criteria: 1. if the item isn't in the list, and 2.
			// (if wordsOnly is true) is a real word, add to the list
			if(!currentList.contains(sb.toString()) &&
					(!wordsOnly||dict.isWord(sb.toString()))) {
				currentList.add(sb.toString());
			}
		}
	}

	/** Add to the currentList Strings that are one character deletion away
	 * from the input string.  
	 * @param word The misspelled word
	 * @param numSuggestions is the maximum number of suggestions to return 
	 * @return the list of spelling suggestions
	 */
	@Override



	public List<String> suggestions(String word, int numSuggestions) {

		// initial variables
		// String to explore
		List<String> queue = new LinkedList<String>();
		// to avoid exploring the same string multiple times
		HashSet<String> visited = new HashSet<String>();
		// words to return
		List<String> retList = new LinkedList<String>();

		// insert first node
		queue.add(word);
		visited.add(word);

		// remove word from queue, calculate distanceOne() mutation words and add back to queue
		int threshold = 0;
		while (!queue.isEmpty() && retList.size() < numSuggestions && threshold < THRESHOLD){
			String curr = queue.remove(0);

			// No need to specify true words here because words will be evaluated later
			List<String> currNeib = distanceOne(curr, false);
			for (String n : currNeib){
				if (!visited.contains(n)){
					visited.add(n);
					queue.add(n);
					if (dict.isWord(n)){
						retList.add(n);

					}
				}
				threshold++;
			}
		}

		return retList;

	}

	public static void main(String[] args) {
		// basic testing code to get started
		String word = "h";
		// Pass NearbyWords any Dictionary implementation you prefer
		Dictionary d = new DictionaryHashSet();
		DictionaryLoader.loadDictionary(d, "data/dict.txt");
		NearbyWords w = new NearbyWords(d);

		List<String> l = w.distanceOne(word, true);
		System.out.println("One away word Strings for for \""+word+"\" are:");
		System.out.println(l+"\n");

		word = "tailo";

		List<String> suggest = w.suggestions(word, 10);
		System.out.println("Spelling Suggestions for \""+word+"\" are:");
		System.out.println(suggest);

	}

}