package com.eulerity.hackathon.imagefinder;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class FaceDetection {
    private ArrayList<String> peopleImages;
    
    public FaceDetection(String image, ArrayList<String> peopleImages)  {
        this.peopleImages = peopleImages;
        // System.out.println("image : "+image);
        URL url;
        try {
            url = new URL(image);
            BufferedImage img = ImageIO.read(url);
            detectFaces(img, image, peopleImages);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        
    }

    public static Mat BufferedImage2Mat(BufferedImage image)  {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        } catch (IOException e) {
           
        }
       return null;
    }
    
    public void detectFaces(BufferedImage image, String url, ArrayList<String> peopleImages) throws IOException{
        if(peopleImages.contains(url))  return;
        if(image == null)   return;
        // Load the OpenCV library
        nu.pattern.OpenCV.loadShared();
        // convert the image of type BufferedImage to type Mat
        Mat loadedImage = BufferedImage2Mat(image);
        if(loadedImage == null) return;
        MatOfRect facesDetected = new MatOfRect();
        // detect faces
        CascadeClassifier cascadeClassifier = new CascadeClassifier(); 
        cascadeClassifier.load("./src/main/java/com/eulerity/hackathon/imagefinder/haarcascade_frontalface_alt.xml"); 
        try{
            cascadeClassifier.detectMultiScale(loadedImage, 
            facesDetected
            );
            if(facesDetected == null)   return;
            // each rectangle in facesDetected is a face:
            Rect[] arr = facesDetected.toArray();
            // if face is found add the url to peopleImages Arraylist.
            if(arr.length !=0){
                // System.out.println("Face Detected");
                peopleImages.add(url);

            }
        }
        catch(CvException e){
            return;
        }
        catch(java.lang.Exception e){
            return;
        }
        
    }

}
