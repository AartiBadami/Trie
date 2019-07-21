package trie;

import java.util.ArrayList;


public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
		addAllWords(root, allWords);
		
		return root;
	}
	
	/**
	 * this method takes in the root of a tree and an array of all words to be added to the tree
	 * it calls the addWordToTrie method in a for-loop to iterate over the entire array and add
	 * every word to the tree -- doesn't return anything, simply modifies the tree
	 * 
	 * @param root
	 * @param allWords
	 */
	private static void addAllWords(TrieNode root, String[] allWords) {
		for (int index = 0; index < allWords.length; index++) {
			addWordToTrie(root.firstChild, root, allWords[index], index, root, allWords);
//			System.out.println("iteration: " + index);
//			print(root, allWords);
//			System.out.println();
		}
	}
	
	/**
	 * This method takes in a String word, the index of the word in the array, and the root of the tree
	 * and adds the word to the tree accordingly -- doesn't return anything, simply modifies the tree
	 * 
	 * @param word
	 * @param index
	 * @param root
	 */
	private static void addWordToTrie(TrieNode pointer, TrieNode prevPointer, String word, int index, TrieNode root, String[] allWords) {
		if (word.equals("")) {
			return;
		}
		
		TrieNode prevPtr = prevPointer;
		TrieNode ptr = pointer;
		
		/* word being added is the first word in the tree --> root.firstChild = new word */ /**WORKS*/
		if ( null == root.firstChild) {
			Indexes indexesOfWord = setIndexes(index, 0, word.length()-1);
			root.firstChild = new TrieNode(indexesOfWord, null, null);
			return;
		}
		/* have common elements and pointer is a leaf node */ /**WORKS*/
		else if (commonElements(ptr, word, allWords) >= 0 && (null == ptr.firstChild)) {
			int endingIndex = commonElements(ptr, word, allWords);
			Indexes prefix = setIndexes(ptr.substr.wordIndex, ptr.substr.startIndex, ptr.substr.startIndex + endingIndex);
			ptr.substr.startIndex += (short) (endingIndex+1);
			TrieNode prefixNode = new TrieNode(prefix, ptr, ptr.sibling);
			Indexes wordIndexes = setIndexes(index, ptr.substr.startIndex, allWords[index].length()-1);
			
			if (prevPtr.firstChild == ptr) {
				prevPtr.firstChild = prefixNode;
			} else if (prevPtr.sibling == ptr) {
				prevPtr.sibling = prefixNode;
			}
			ptr.sibling = new TrieNode(wordIndexes, null, null);
			return;
		}
		/* have common elements and pointer is NOT a leaf node */
		else if (commonElements(ptr, word, allWords) >= 0 && (null != ptr.firstChild)) {
			
			if (!(isCompleteMatch(ptr, word, allWords))) {
				int endingIndex = commonElements(ptr, word, allWords);
				Indexes prefixIndex = setIndexes(ptr.substr.wordIndex, ptr.substr.startIndex, ptr.substr.startIndex + endingIndex);
				ptr.substr.startIndex += (short) (endingIndex+1);
				TrieNode prefixNode = new TrieNode(prefixIndex, ptr, null);
				Indexes wordIndex = setIndexes(index, ptr.substr.startIndex, allWords[index].length()-1);
				TrieNode wordNode = new TrieNode(wordIndex, null, null);
				ptr.sibling = wordNode;
				
				if (prevPtr.firstChild == ptr) {
					prevPtr.firstChild = prefixNode;
				} else if (prevPtr.sibling == ptr) {
					prevPtr.sibling = prefixNode;
				}
				return;
			}
			prevPtr = ptr;
			ptr = ptr.firstChild;
			addWordToTrie(ptr, prevPtr, word, index, root, allWords);
			return;
			
		}
		/* to traverse the sibling linked list (increments the pointer by 1) */
		else if (null != ptr && commonElements(ptr, word, allWords) < 0) {
			prevPtr = ptr;
			ptr = ptr.sibling;
			addWordToTrie(ptr, prevPtr, word, index, root, allWords);
			return;
		}
		/* if ptr is null, adds the word to the end */
		else if (null == ptr) {
			Indexes wordIndex = setIndexes(index, prevPtr.substr.startIndex, allWords[index].length()-1);
			prevPtr.sibling = new TrieNode(wordIndex, null, null);
			return;
		}
		/* have SOME common elements and pointer is NOT a leaf node */
	}
	
	// returns true if word contains the entire substr of ptr --> used for buildTrie method
	private static boolean isCompleteMatch(TrieNode a, String b, String[] allWords) {
		String A = allWords[a.substr.wordIndex].substring(a.substr.startIndex, a.substr.endIndex+1);
		String B;
		if (a.substr.endIndex+1 > b.length()) {
			B = b.substring(a.substr.startIndex);
		} else {
			B = b.substring(a.substr.startIndex, a.substr.endIndex+1);
		}
		int counter = 0;
		while (counter < A.length() && A.charAt(counter) == B.charAt(counter)) {
			counter++;
		}
		return (counter==A.length());
	}	
	
	// compares two strings and see if there are common elements & returns a new string of the common letters
	private static int commonElements(TrieNode a, String b, String[] allWords) {
		if (null == a) {
			return -1;
		}
		String A = allWords[a.substr.wordIndex].substring(a.substr.startIndex, a.substr.endIndex+1);
		String B = b.substring(a.substr.startIndex);
		int counter = 0;
		while (counter < A.length() && counter < B.length() 
				&& A.charAt(counter) == B.charAt(counter)) {
			counter++;
		} if (counter == 0) {
			return -1;
		} else if (counter > 0) {
			counter--;
		} return counter;
	}
	
	
	private static Indexes setIndexes(int indexOfWord, int start, int end) {
		return new Indexes(indexOfWord, (short)start, (short)end);
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		ArrayList<TrieNode> list = new ArrayList<>();
		
		if (null != root.firstChild) {
		completionList(list, root.firstChild, allWords, prefix);
		}
		
		return list;
	}
	
	private static void completionList(ArrayList<TrieNode> list, TrieNode ptr, String[] allWords, String prefix) {
		boolean branchFound = false;
		
		/* ptr starts with the ENTIRE prefix */
		if (allWords[ptr.substr.wordIndex].startsWith(prefix)) {
			branchFound = true;
			if (null != ptr.firstChild) { // if ptr is NOT a leaf node, keep recursing
				completionList(list, ptr.firstChild, allWords, prefix);
				if(null != ptr.sibling) {
					completionList(list, ptr.sibling, allWords, prefix);
				}
			} else { // if ptr IS a leaf node, store the word
				list.add(ptr);
				if (null != ptr.sibling) {
					completionList(list, ptr.sibling, allWords, prefix);
				} else {
					return;
				}
			}
		} /* ptr starts with PARTIAL prefix */
		else if (allWords[ptr.substr.wordIndex].charAt(0) == prefix.charAt(0)) {
			branchFound = true;
			if (null == ptr.firstChild) { // ptr IS a leaf node and doesn't contain the entire prefix
				if (null != ptr.sibling) {
					completionList(list, ptr.sibling, allWords, prefix);
				} else {
					return;
				}
			} else { // is NOT a leaf node --> recurse
				completionList(list, ptr.firstChild, allWords, prefix);
				if (null != ptr.sibling) {
					completionList(list, ptr.sibling, allWords, prefix);
				}
			}
		} /* ptr has NO common elements */
		else {
			if ((null != ptr.sibling) && !(branchFound)) {
				completionList(list, ptr.sibling, allWords, prefix);
			} else {
				return;
			}
		}
		
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
}
// final ver 3.0