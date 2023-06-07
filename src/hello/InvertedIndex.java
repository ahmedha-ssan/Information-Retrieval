
package hello;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
public class InvertedIndex {
    private HashMap<String, DictEntry> index;
    private HashMap<Integer, HashMap<String, Integer>> DocTermFreq;
    String[] Files;
    public int NoOfDocs=0;

    public InvertedIndex(String[] files) {
        index = new HashMap<String, DictEntry>();
        NoOfDocs = files.length;
        this.Files= files;
        DocTermFreq = new HashMap<>();
    }
    public void buildIndex(String[] files) throws IOException {
        for(int i=0;i<files.length;i++) {
            HashMap<String, Integer> termFreqs = new HashMap<String, Integer>();
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
                    termFreqs.put(word, termFreqs.getOrDefault(word, 0) + 1);
                }
            }
            DocTermFreq.put(docId, termFreqs);
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

    /* NEW FROM HERE*/
    public LinkedHashMap<String, Double> Ranking(List<String> query) {
        HashMap<String, Double> docCosineScores = new HashMap<>();
        for (int i = 0; i < NoOfDocs; i++) {
            String fileName = Files[i];
            HashMap<String, Integer> docTermFreq = DocTermFreq.get(i+1);
            double cosineScore = CalculateCosineSimilarity(query, docTermFreq);
            docCosineScores.put(fileName, cosineScore);
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(docCosineScores.entrySet());

        // Sort the entries using a custom Comparator
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                return entry1.getValue().compareTo(entry2.getValue());
            }
        });

        // Create a new LinkedHashMap to preserve the order
        LinkedHashMap<String, Double> sortedDocCosineScores = new LinkedHashMap<>();

        // Iterate over the sorted entries and put them into the new LinkedHashMap
        for (Map.Entry<String, Double> entry : entries) {
            sortedDocCosineScores.put(entry.getKey(), entry.getValue());
        }
        return sortedDocCosineScores;
    }

    public double CalculateCosineSimilarity(List<String> query, HashMap<String, Integer> document) {
        HashMap<String, Integer> terms = new HashMap<>();
        for (String term : document.keySet()) {
            int freq = document.getOrDefault(term, 0);
            terms.put(term, freq);
        }

        double documentMag = 0;
        double queryMag = 0;
        double dotProd = 0;
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        for(String Q:query) {
            queryMag += 1*1;
        }
        for(String D1:terms.keySet()) {
            int termFreq = terms.get(D1);
            documentMag += termFreq*termFreq;
        }

        for (String term : terms.keySet()) {
            int termFreq = terms.get(term);
            if (query.contains(term)) {
                int queryTermFreq = Collections.frequency(query, term);
                dotProd += queryTermFreq * termFreq;
            }
        }

        documentMag = Math.sqrt(documentMag);
        queryMag = Math.sqrt(queryMag);
        if (documentMag == 0 || queryMag == 0) {
            return 0;
        }
        double ans = dotProd / (queryMag * documentMag);
        double ans1 = Math.acos(ans);
        ans1 = Math.toDegrees(ans1);
        ans1 = Double.parseDouble(decimalFormat.format(ans1));
        return ans1;
    }
}
