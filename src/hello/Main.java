package hello;
import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) throws IOException{
	    String[] files= {"file1.txt", "file2.txt","file3.txt","file4.txt","file5.txt","file6.txt","file7.txt","file8.txt","file9.txt","file10.txt"};
	    InvertedIndex index = new InvertedIndex();
	    index.buildIndex(files);
	    Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your word: ");
        String word = scanner.nextLine();
        List< Integer> result = index.search(word);
        if(result.isEmpty()) {
        	System.out.print("This word does not exist");
        }
        else {
        	System.out.println("Result of search is: ");
        	for(Integer res:result) {
        		System.out.println("file"+res+".txt");
        	}
        }
	 }
}
