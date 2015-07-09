/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author PieterJan
 */
@Entity
@RequestScoped
public class Episode implements Serializable {
    @Id
    private int episode;
    private String title;
    private boolean watched;
    
    public Episode() {
        episode = 0;
        title = "";
        watched = false;
    }
    
    /**
     * Constructor
     * @param id episode number
     * @param title title of episode
     * Watched field is auto false.
     */
    public Episode(int id, String title) {
        this.episode = id;
        this.title = title;
        this.watched = false;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String episodeTitle) {
        this.title = episodeTitle;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
    
    
}
