/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entity.Anime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author PieterJan
 */
public class CalculateNextRelease {
    private static Anime myAnime;
    private static Calendar  cal;
    private static String nextDate;
    
    public static int getAirDay(Anime anime) {
        reset();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = null;
        try {
            startDate = format.parse(anime.getStartDate());
        }catch(ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(startDate);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    public static boolean dayHasPassed(Anime anime) {
        reset();
        int today = 0;
        int animeDay = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = null;
        try {
            startDate = format.parse(anime.getStartDate());
        }catch(ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(cal.getTime());
        today = cal.get(Calendar.DAY_OF_WEEK);
        
        cal.setTime(startDate);
        animeDay = cal.get(Calendar.DAY_OF_WEEK);
        
        if (today > animeDay) {
            return true;
        } else if(animeDay > today) {
            return false;
        }
        
        return false;
    }
    
    
    public static String getNextAirDate(Anime anime) {
        reset();
        myAnime = anime;
        if (anime.getStatus() == AnimeStatus.CURRENTLY_AIRING) {
            //2006-01-07
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            DateFormat toFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = format.parse(anime.getStartDate());
                endDate = format.parse(anime.getEndDate());
            }catch(ParseException e) {
                e.printStackTrace();
            }

            Date today = cal.getTime();

            cal.setTime(startDate);
            int airDay = cal.get(Calendar.DAY_OF_WEEK);

            cal.setTime(today);
            int currentDay = cal.get(Calendar.DAY_OF_WEEK);
            
            if (airDay == currentDay) {
                nextDate = "TODAY";
            } else {
                int difference = airDay - currentDay;
                int add = 0;
                if (difference < 0) {
                    add = difference + 7;
                } else {
                    add = difference;
                } 

                cal.add(Calendar.DATE, add);
                nextDate = (String)(toFormat.format(cal.getTime()));
            }
        } 
    
        return nextDate;
    }

    
    private static void reset() {
        myAnime = null;
        nextDate = "";
        cal = Calendar.getInstance();
    }
    
    
}
