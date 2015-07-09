/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entity.Anime;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.http.*;
import org.apache.commons.codec.binary.Base64;
import resources.InputStreamToString;
        
/**
 *
 * @author PieterJan
 */
public class XmlToAnime {
    private static String sUrl;
    private static STATUS status = STATUS.PROGRESS;
    private static final List<Anime> animes = new ArrayList<>();
    
    private enum STATUS {
        SUCCES, ERROR, PROGRESS
    }
    
    public static final List<Anime> getAnimes() {
        return animes;
    }
    
    public static void setUrl(String url) {
        if (url != null && url.trim().length() > 0)
            XmlToAnime.sUrl = url;
    }
    
    public static String getUrl() {
        return sUrl;
    }
    
    private static InputStream getInput() {
        String username = "shigato";
        String password = "unlimited0";
        String authString = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        InputStream in = null;
        
        try {
            URL url = new URL(sUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            urlConnection.setRequestProperty("Accept-Language", "en");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
            in = urlConnection.getInputStream();
            String temp = InputStreamToString.getStringFromInputStream(in);
            temp = Replacer.replaceIllegalCharacters(temp);
            in = new ByteArrayInputStream(temp.getBytes(StandardCharsets.UTF_8));
            System.out.println();
            
//            InputStreamReader isr = new InputStreamReader(in);
//            
//            int numCharsRead;
//            char[] charArray = new char[1024];
//            StringBuilder sb = new StringBuilder();
//            while ((numCharsRead = isr.read(charArray)) > 0) {
//                    sb.append(charArray, 0, numCharsRead);
//            }
//            String result = sb.toString();
//            System.out.println(result);
            
            
        } catch (IOException e) {
            
        }
        return in;
    }
    
    public static void ReadXml() {
        animes.clear();
        if (sUrl.isEmpty() || sUrl == null) {
            throw new InternalError("Url should not be empty!");
        }
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //Document doc = (Document) dBuilder.parse(new URL(sUrl).openStream());
            //Document doc = (Document) dBuilder.parse
            
            InputStream input = getInput();
            if (input.available() > 0) {
                Document doc = (Document) dBuilder.parse(input);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entry");
            
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Anime anime = new Anime();
                    Element eElement = (Element) nNode;
                    anime.setId(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()));
                    anime.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
                    anime.setDescription(eElement.getElementsByTagName("synopsis").item(0).getTextContent());
                    anime.setEpisodes(Integer.parseInt(eElement.getElementsByTagName("episodes").item(0).getTextContent()));
                    anime.setImageUrl(eElement.getElementsByTagName("image").item(0).getTextContent());
                    anime.setStartDate(eElement.getElementsByTagName("start_date").item(0).getTextContent());
                    anime.setEndDate(eElement.getElementsByTagName("end_date").item(0).getTextContent());
                    
                    String temp = eElement.getElementsByTagName("status").item(0).getTextContent();
                    if (temp.toLowerCase().contains("finished")) {
                        anime.setStatus(AnimeStatus.FINISHED);
                    } else if (temp.toLowerCase().contains("currently")) {
                        anime.setStatus(AnimeStatus.CURRENTLY_AIRING);
                    }
                    
                    anime.setAirDay(CalculateNextRelease.getAirDay(anime));
                    anime.setType(Anime.TYPE.valueOf(eElement.getElementsByTagName("type").item(0).getTextContent()));
                    animes.add(anime);
                }
            }
            }
            
            
        } catch (SAXException | IOException | ParserConfigurationException e) {
            status = STATUS.ERROR;
            e.printStackTrace();
        }
    }
    
    
    
    
}
