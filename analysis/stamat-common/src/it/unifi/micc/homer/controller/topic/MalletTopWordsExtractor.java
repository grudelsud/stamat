/**
 * 
 */
package it.unifi.micc.homer.controller.topic;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import stamat.model.TopicWord;

import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;

/**
 * @author bertini
 * 
 */
public class MalletTopWordsExtractor {

	public static Object[][] getTopWordsWithWeights(ArrayList<TreeSet<IDSorter>> topicSortedWords, int numTopics, int numWords, Alphabet alphabet) {

		Object[][] result = new Object[numTopics][];

		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);

			// How many words should we report? Some topics may have fewer than
			// the default number of words with non-zero weight.
			int limit = numWords;
			if (sortedWords.size() < numWords) {
				limit = sortedWords.size();
			}
			result[topic] = new Object[limit];

			for (int i = 0; i < limit; i++) {
				IDSorter info = iterator.next();
				TopicWord tw = new TopicWord(alphabet.lookupObject(info.getID()).toString(), info.getWeight());
				result[topic][i] = tw;
			}
		}

		return result;
	}

}
