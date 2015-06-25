/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import entity.Anime;

/**
 *
 * @author PieterJan
 */
public class AlreadyFoundException extends Exception {
    String message = "";
    public AlreadyFoundException() {}
    public AlreadyFoundException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
    
    public AlreadyFoundException(Anime anime) {
        super();
        this.message = "The anime: " + anime.getTitle() + " is already added to your library";
    }
}
