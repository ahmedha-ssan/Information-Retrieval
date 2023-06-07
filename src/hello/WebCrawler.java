package hello;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element; 
import org.jsoup.select.Elements; 
public class WebCrawler {	 
	   public List<String> extractDataWithJsoup(String href){
		  List<String> LINKS = new ArrayList<String>();
	      Document doc = null; 
	      try { 
	         doc = Jsoup.connect(href).timeout(10*1000).userAgent
	             ("Mozilla").ignoreHttpErrors(true).get(); 
	      } catch (IOException e) { 
	    	  System.err.println(e.getMessage());
	      } 
	      if(doc != null){ 
	         String title = doc.title(); 
	         String text = doc.body().text(); 
	         Elements links = doc.select("a[href]"); 
	         for (Element link : links) { 
	            String linkHref = link.attr("href"); 
	            String linkText = link.text(); 
	            String linkOuterHtml = link.outerHtml(); 
	            String linkInnerHtml = link.html();
	            if(!LINKS.contains(linkHref)) {
	            	LINKS.add(linkHref);
	            	System.out.println(linkText+ " => " + linkHref);
	            }
	            //System.out.println(linkHref + "t" + linkText + "t"  + linkOuterHtml + "t" + linkInnerHtml); 
	         } 
	      } 
	      return LINKS;
	   } 
}
