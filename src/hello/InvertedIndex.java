package hello;
import java.io.*;
import java.util.*;

public class InvertedIndex {
	private HashMap<String, DictEntry> index;
    
    public InvertedIndex() {
        index = new HashMap<String, DictEntry>();
    }
    public void buildIndex(String[] files) throws IOException {
    	for(int i=0;i<files.length;i++) {
    		BufferedReader reader = new BufferedReader(new FileReader(files[i]));
    		String lineByLine;   // to read line 
    		int docId=i+1;    // document ID
    		while((lineByLine=reader.readLine())!=null) {
    			String[] words = lineByLine.split("\\W+");
    			for(String word:words) {
    				word=word.toLowerCase();
    				if(!index.containsKey(word))
    					index.put(word,new DictEntry());
    				DictEntry newOne = index.get(word);
    				newOne.term_freq++;
    				if(newOne.pList==null || newOne.pList.docId != docId) {  // lw el el pointer m4 by4awr 3laa 7aga w el doc el naa 3aleh daah mtkrar4 ya3ni l2a el klma aktr mn mra f el doc
    					Posting newPost = new Posting();
    					newPost.docId=docId;
    					newOne.doc_freq++;
    					newPost.next = newOne.pList;
    					newOne.pList = newPost;
    				}
    				else {
    					newOne.pList.dtf++;
    				}
    			}
    		}
    		reader.close();
    	}
    }
    public List<Integer> search (String word){
    	List<Integer> result = new ArrayList<Integer>();
    	if(index.containsKey(word)) {
    		DictEntry storedEntry = index.get(word);
    		System.out.println("number of documents that contain the term: "+storedEntry.doc_freq);
    		System.out.println("number of times the term is mentioned in the collection: "+storedEntry.term_freq);
    		Posting head= storedEntry.pList;
    		while(head !=null) {
    			System.out.println("document term frequency: "+head.dtf+ ", in document: "+head.docId );
    			result.add(head.docId);
    			head=head.next;
    		}
    	}
    	return result;
    }
}
