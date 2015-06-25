/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

/**
 *
 * @author PieterJan
 */
public class Replacer {
    public static String replaceIllegalCharacters(String data) {
        return data.replace("<br />", "").replace("[i]", "").replace("[/i]", "").replace("&mdash;", " ").replace("&rsquo;", "'").replace("&radic;", "Root ")
                .replace("&quot;", "\"");
    }
    
    public static String encodeUrl(String data) {
        String character[][] = {
            {" ", "%20"},
            {":", "%3a"},
            {"(", "%28"},
            {")", "%29"},
            {"/", "%2F"},
            {")", "%21"},
            {"&", "%26"},
        };
        
        for (String[] x : character) {
            data = data.replace(x[0], x[1]);
        }
        
        return data;
    }
}
