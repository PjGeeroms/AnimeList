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
public enum AnimeStatus {
    FINISHED ("Finished Airing"), CURRENTLY_AIRING("Currently Airing"), NOT_YET_AIRED("Not yet aired"), UNKNOWN("Unknown type");
    
    private final String name;
    
    private AnimeStatus(String s) {
        name = s;
    }
    
    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    @Override
    public String toString(){
       return name;
    }
}
