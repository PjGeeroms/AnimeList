/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import entity.Anime;
import java.util.Calendar;
import java.util.Comparator;

/**
 *
 * @author PieterJan
 */
public class AnimeAirDayComparator implements Comparator<Anime>{
    private Calendar cal = Calendar.getInstance();
    
    
    @Override
    public int compare(Anime o1, Anime o2) {

        if (o1.getStatus() == AnimeStatus.CURRENTLY_AIRING && o2.getStatus() == AnimeStatus.FINISHED) {
            return -1;
        } else if(o1.getStatus() == AnimeStatus.FINISHED && o2.getStatus() == AnimeStatus.CURRENTLY_AIRING) {
            return 1;
        } else if (o1.getStatus() == AnimeStatus.CURRENTLY_AIRING && o2.getStatus() == AnimeStatus.CURRENTLY_AIRING) {
            // De animes airen beide
            if (o1.getAirDay() < o2.getAirDay()) {
                //Anime 1 valt voor Anime 2 maar die dag is al voorbij.
                if (CalculateNextRelease.dayHasPassed(o1)) {
                    return 1;
                }
                
                //Anime 1 komt voor Anime 2.
               return -1; 
            } else if (o1.getAirDay() > o2.getAirDay()) {
                if (o2.getAirDay() == 1 && !CalculateNextRelease.dayHasPassed(o1)) {
                    return -1;
                }
                //Anime 1 komt na Anime 2
                return 1;
            }
        } 
        
        return 0;
    }
    
}
