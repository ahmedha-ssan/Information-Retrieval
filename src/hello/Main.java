package hello;
import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        String[] files= {"file1.txt", "file2.txt","file3.txt","file4.txt","file5.txt","file6.txt","file7.txt","file8.txt","file9.txt","file10.txt"};

        InvertedIndex index = new InvertedIndex(files);
        index.buildIndex(files);

        PositionalIndex posindex = new PositionalIndex(files);
        posindex.buildIndex(files);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Make your choice:");
            System.out.println("[1]Search by Word");
            System.out.println("[2]Search by Query");
            System.out.println("[3]Ranking Files using cosine similarity");
            System.out.println("[4]Ranking Files using TF-IDF");
            System.out.println("[5]web crawling");
            System.out.println("[6]Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter your word: ");
                    String word = scanner.nextLine();
                    List<Integer> result = index.search(word);
                    if (result.isEmpty()) {
                        System.out.print("This word does not exist");
                    } else {
                        System.out.println("Result of search is: ");
                        for (Integer res : result) {
                            System.out.println("file" + res + ".txt");
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter your query: ");
                    String query = scanner.nextLine();
                    Vector<String> queryTerms = new Vector<>(Arrays.asList(query.split("\\s+")));
                    posindex.ranking(queryTerms);
                    break;
                case 3:
                    System.out.print("Enter your query: ");
                    String Query = scanner.nextLine();
                    Vector<String> QueryTerms = new Vector<>(Arrays.asList(Query.split("\\s+")));
                    HashMap<String, Double> top10=index.Ranking(QueryTerms);
                    //List<String> top10 = index.Ranking(QueryTerms);

                    for(String fileee:top10.keySet()) {
                        double x = top10.get(fileee);
                        System.out.println(fileee + " "+ x);
                    }
                    break;
                case 4:
                    System.out.print("Enter your query: ");
                    String QQuery = scanner.nextLine();
                    Vector<String> QQueryTerms = new Vector<>(Arrays.asList(QQuery.split("\\s+")));
                    posindex.Calculate_TF_IDF(QQueryTerms);

                    break;
                case 5:
                    WebCrawler test = new WebCrawler();
                    List<String> LINKS = test.extractDataWithJsoup("https://en.wikipedia.org/");
                    break;
                case 6:
                    System.out.println("Thx hope to see you again...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    break;
            }
        }
    }
}

