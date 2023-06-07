package hello;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class PositionalIndex {
    private HashMap<String, DictEntry> index;
    private HashMap<Integer, HashMap<String, List<Integer>>> DocTermPositions;
    String[] Files;
    public int NoOfDocs = 0;
    private HashMap<Integer, Integer> numOfWordsInDoc;
    public HashMap<String,Double>IDF;
    public HashMap<Integer,HashMap<String,Double>>TF;
    public HashMap<Integer,HashMap<String,Double>>TF_IDF;
    public PositionalIndex(String[] files) {
        index = new HashMap<String, DictEntry>();
        NoOfDocs = files.length;
        this.Files = files;
        DocTermPositions = new HashMap<>();
        numOfWordsInDoc = new HashMap<>();
        IDF= new HashMap<>();
        TF= new HashMap<>();
        TF_IDF= new HashMap<>();
    }

    public void buildIndex(String[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(files[i]));
            String lineByLine;   // to read line
            int docId = i + 1;    // document ID
            HashMap<String, List<Integer>> termPositions = new HashMap<>(); // to store the term positions for each document
            //To get position for positional index
            int position = 0;
            int num=0;
            while ((lineByLine = reader.readLine()) != null) {
                String[] words = lineByLine.split("\\W+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if (!index.containsKey(word))
                        index.put(word, new DictEntry());
                    DictEntry newOne = index.get(word);
                    newOne.term_freq++;//increment no times the term appears in all documents

                    if (newOne.pList == null || newOne.pList.docId != docId) {
                        Posting newPost = new Posting();
                        newPost.docId = docId;
                        newOne.doc_freq++;
                        newPost.next = newOne.pList;
                        newOne.pList = newPost;
                    } else {
                        newOne.pList.dtf++;
                    }

                    // update the term positions maps each document ID to a HashMap of term positions
                    List<Integer> positionsList = termPositions.getOrDefault(word, new ArrayList<>());
                    positionsList.add(position);
                    termPositions.put(word, positionsList);
                    position++;
                    num++;
                }
            }
            // add the term positions to the DocTermPositions map
            DocTermPositions.put(docId, termPositions);
            numOfWordsInDoc.put(docId, num);
            reader.close();
        }
    }
    public void Calculate_TF_IDF(Vector<String> query){
        List<String> queryList = new ArrayList<>(query);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        for(String ww:queryList) {
            int numOfDocsHasWord = 0;
            for(int num:numOfWordsInDoc.keySet()) {
                List<Integer> termPositions = DocTermPositions.get(num).get(ww);
                double termFreq = 0;
                if(termPositions != null) {
                    termFreq = termPositions.size();
                }
                double totalWords=numOfWordsInDoc.get(num);
                double res = termFreq/totalWords;
                res = Double.parseDouble(decimalFormat.format(res));
                HashMap<String,Double> inp = new HashMap<String,Double>();
                inp.put(ww, res);
                TF.put(num,inp);
                HashMap<String, List<Integer>> ll = DocTermPositions.get(num);
                if(ll.containsKey(ww)) {
                    numOfDocsHasWord++;
                }
            }
            double DTF= Files.length / numOfDocsHasWord;
            double gg=Math.log10(DTF);
            gg = Double.parseDouble(decimalFormat.format(gg));
            IDF.put(ww, gg);
            for(Integer x:TF.keySet()) {
                Map<String,Double> ff = TF.get(x);
                for(String y:ff.keySet()) {
                    double tf=ff.get(y);
                    double idf=IDF.get(y);
                    HashMap<String,Double> val = new HashMap<String,Double>();
                    System.out.println("file"+x+" : "+y+" "+ Double.parseDouble(decimalFormat.format(tf*idf)));
                }
            }
        }
    }


    public List<Integer> searchQuery(List<String> query) {
        List<Integer> result = new ArrayList<>();
        HashMap<Integer, Integer> docScores = new HashMap<>();//store the scores of each document.

        for (String term : query) {
            term = term.toLowerCase();
            if (index.containsKey(term)) {
                DictEntry storedEntry = index.get(term);
                Posting head = storedEntry.pList;
                while (head != null) {
                    int docId = head.docId;
                    int tf = head.dtf;
                    List<Integer> termPositions = DocTermPositions.get(docId).get(term);

                    // Perform phrase search
                    boolean found = false;
                    for (int pos : termPositions) {
                        boolean match = true;
                        for (int i = 1; i < query.size(); i++) {
                            String nextTerm = query.get(i).toLowerCase();
                            List<Integer> nextTermPositions = DocTermPositions.get(docId).get(nextTerm);
                            if (nextTermPositions == null || !nextTermPositions.contains(pos + i)) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        docScores.put(docId, docScores.getOrDefault(docId, 0) + tf);
                    }
                    head = head.next;
                }
            }
        }

        // Sort the documents by scores
        List<Map.Entry<Integer, Integer>> sortedDocs = new ArrayList<>(docScores.entrySet());
        sortedDocs.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Retrieve the top 10 documents
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : sortedDocs) {
            if (count >= 10) {
                break;
            }
            result.add(entry.getKey());
            count++;
        }

        return result;
    }

    public void ranking(Vector<String> query) {
        List<String> queryList = new ArrayList<>(query);
        List<Integer> result = searchQuery(queryList);
        if (result.isEmpty()) {
            System.out.print("No documents found.");
        } else {
            System.out.println("Ranking of documents based on query relevance: ");
            for (Integer docId : result) {
                System.out.println("File" + docId + ".txt");
            }
        }
    }

}
