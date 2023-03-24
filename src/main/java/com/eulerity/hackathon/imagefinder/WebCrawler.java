package com.eulerity.hackathon.imagefinder;
import java.io.IOException;
// import java.net.URI;
// import java.net.URISyntaxException;
import java.util.ArrayList;



// import javax.lang.model.element.Element;

import org.jsoup.Connection;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler implements Runnable {
    private static final int MAX_DEPTH = 3;
    private Thread thread;
    private String first_link;
    private int ID;
    // private String domain;
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private static ArrayList<String> Images = new ArrayList<String>();
    private ArrayList<String> logoImages;
    private ArrayList<String> outputImages;
    private ArrayList<String> peopleImages;

    public WebCrawler(String link, ArrayList<String> outputImages, ArrayList<String> logoImages, ArrayList<String> peopleImages ) {

        this.first_link = link;
		this.outputImages = outputImages;
        this.logoImages = logoImages; 
        this.peopleImages = peopleImages;
        thread = new Thread(this);
        thread.start();
    }

    // return the thread
    public Thread getThread(){
        return thread;
    }


    private void crawl( String url, ArrayList<String> peopleImages) throws IOException{
        
        System.out.println("Thread ID: "+thread.getId());
        System.out.println("Thread Url: "+url);
        //Connect to url and get HTML data
        Document doc = request(url);
        
        // if the doc is not null, iterare over all the images.
        if(doc != null){
            // Get all img tags
            Elements media = doc.select("img[src]");
            System.out.println("\nMedia: "+ media.size());
            // Loop through media/img tags
            for (Element src : media) {
                String tag = src.attr("abs:src");
                //if the img dosent have a valid image file extension then continue
                if(!tag.contains(".png") && !tag.contains(".apng") && 
                !tag.contains(".jpeg") && !tag.contains(".jpg") &&
                !tag.contains(".jfif") && !tag.contains(".pjpeg") &&
                !tag.contains(".pjp") && !tag.contains(".tiff") &&
                !tag.contains(".tif") && !tag.contains(".cur") &&
                !tag.contains(".svg") && !tag.contains(".webp") &&
                !tag.contains(".bmp") && !tag.contains(".ico")){
                    continue;
                }
                //Append img source(src) to arraylist of valid sources.
                if(outputImages.contains(tag))  continue;
                outputImages.add(src.attr("abs:src"));

                // find logos and append it to logoimagees
                if(tag.contains("logo") & !logoImages.contains(tag)){
                    logoImages.add(tag);
                }
                // try dtecting faces, if found then append it to peopleImages Arraylist
                if(tag != null) new FaceDetection(tag, peopleImages);
            }
        }
        else{
            System.out.println("No Media files found");
        }
    }

    //Connect to url and get HTML data
    private Document request(String url) {
        try{
            Connection con =  Jsoup.connect(url);
            Document doc = con.get(); 

            if(con.response().statusCode() == 200)  {
                System.out.println("\n Received Webpage at :" + url);
                String title = doc.title();
                System.out.println(title);
                visitedLinks.add(url);
                return doc;
            } 
            return null;
        }
        catch(IOException e){
            // e.printStackTrace();
            return null;
        }
    }
 
    @Override
    public void run() {
        // crawl the given url
        try {
            crawl( first_link, peopleImages);
        } catch (IOException e) {
            
            // e.printStackTrace();
        }
    }
}
