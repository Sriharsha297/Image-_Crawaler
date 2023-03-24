package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
// import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// import com.eulerity.hackathon.imagefinder.WebCrawler;

import org.jsoup.Connection;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;

@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;
	// private ArrayList<String> visitedLinks = new ArrayList<String>();

	protected static final Gson GSON = new GsonBuilder().create();


	//This is just a test array
	public static final String[] testImages = {
			"https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny",

  };

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		String path = req.getServletPath();
		String url = req.getParameter("url");
		String depth = req.getParameter("depth");
		System.out.println("Got request of:" + path + " with query param:" + url + "with depth: "+ depth);
		String domain = null;
		ArrayList<String> outputImages = new ArrayList<String>();
		ArrayList<String> logoImages = new ArrayList<String>();
		ArrayList<String> peopleImages = new ArrayList<String>();
		ArrayList<String> visitedLinks = new ArrayList<String>();
		// if url is not provided, return with testImages
		if (url == null){	
			resp.getWriter().print(GSON.toJson(testImages));
			return;
		}
		// if url is provided, retrive domain name, establish a connection to the url using jsoup and fetch a HTML file as doc. 
		if(url != null)
		{
			domain = getDomain(url);
			System.out.println(domain);
			Document doc = request(url, visitedLinks);

			//if the doc is not null, create a arrayList of threads to add all the created threads, iterate all the links in doc, and create a webaCrawler object.
			if(doc != null){
				int i = 1;
				
				ArrayList<Thread> threads = new ArrayList<Thread>();
				// Get all url tags and iterate over them
				for(Element link : doc.select("a[href]")){
					if(i<= 100){
						//Get absolute path of url
						String next_link = link.absUrl("href");
						// check if the link is not visited and has the same domain as root url
						if(!visitedLinks.contains(next_link) && next_link.contains(domain)) {
							Thread imageScraperThread =  new WebCrawler(next_link, outputImages, logoImages, peopleImages).getThread();
							threads.add(imageScraperThread);
							visitedLinks.add(next_link);
						}
					}
					i = i+1;
				}
				//  Waiting for image scraping threads to finish
				for (Thread thread : threads){
					try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}

				System.out.println("Finished");

			}
		}
		// Converting Arraylist<String> to String[]
		String[] res = outputImages.toArray(new String[outputImages.size()]);
		String[] res_logos = logoImages.toArray(new String[logoImages.size()]);
		String[] people = peopleImages.toArray(new String[peopleImages.size()]);
		String[] visited = visitedLinks.toArray(new String[visitedLinks.size()]);

		// Respond to post request 
		try{
			PrintWriter out = resp.getWriter();
			resp.setContentType("text/json");
			resp.setCharacterEncoding("UTF-8");
			out.print(GSON.toJson(visited)+" "+GSON.toJson(people)+" "+GSON.toJson(res_logos)+" "+GSON.toJson(res));
			out.flush(); //commit response by flushing the stream
		}catch(IOException e){System.out.println("Exception");}
	}

	//Get parsed domain from initially provided url
	private String getDomain(String url) {
		URI uri;
		String domain;
		try {
			uri = new URI(url);
			domain = uri.getHost();
			return domain;
		} 
		catch (URISyntaxException e) {}
		return null;
	}

	//Connect to url and get HTML data
	private Document request(String url, ArrayList<String> visitedLinks) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static Gson getGson() {
		return GSON;
	}

	public static String[] getTestimages() {
		return testImages;
	}


}
