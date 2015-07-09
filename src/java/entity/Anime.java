/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import resources.AnimeStatus;

/**
 *
 * @author PieterJan
 */
@Entity
@Table(name = "TBL_ANIME")
@NamedQueries({
    @NamedQuery(name = "Anime.findAll", query = "SELECT a FROM Anime a"),
    @NamedQuery(name = "Anime.findByName", query = "SELECT a FROM Anime a WHERE UPPER(a.title) LIKE UPPER(:title)"),
})
public class Anime implements Serializable {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", length = 5000)
    private String description;
    
    public Anime() {
        description = "";
        episodes = 0;
        title = "";
        type = TYPE.TV;
        status = AnimeStatus.UNKNOWN;
        imageUrl = "";
        watchedEpisodes = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name = "image")
    private String imageUrl;
    
    public enum TYPE {
        TV, Movie, OVA, ONA, Special, Music
    };
    
    @Column(name = "type")
    private TYPE type;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
    
    @Column(name = "episodes")
    private Integer episodes;
    
    @Column(name = "watchedEpisodes")
    private Integer watchedEpisodes;
    
    @Column(name = "status")
    private AnimeStatus status;

    public AnimeStatus getStatus() {
        return status;
    }

    public void setStatus(AnimeStatus status) {
        this.status = status;
    }
  
    @ElementCollection
    private List<String> genres = new ArrayList<>();
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getEpisodes() {
        return episodes;
    }

    /**
     * Will set the episodes and generate the episodeList
     * @param episodes The amount of episodes in the anime.
     */
    public void setEpisodes(Integer episodes) {
        this.episodes = episodes;
        createListFromEpisodes();
    }

    public Integer getWatchedEpisodes() {
        return watchedEpisodes;
    }

    public void setWatchedEpisodes(Integer watchedEpisodes) {
        this.watchedEpisodes = watchedEpisodes;
    }

    
    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    
    private String startDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    private String endDate;
    
    private int airDay;

    public int getAirDay() {
        return airDay;
    }

    public void setAirDay(int airDay) {
        this.airDay = airDay;
    }

    @OneToMany
    private final List<Episode> episodeList = new ArrayList<>();
    
    public void addEpisodeToList(Episode e) {
        episodeList.add(e);
    }
    
    public void removeEpisodeFromList(Episode e) {
        episodeList.remove(e);
    }
    
    public List<Episode> getEpisodeList() {
        return episodeList;
    }
    
    /**
     * Creates the list with empty titles using the episodes in the anime object.
     */
    public void createListFromEpisodes() {
        for(int i = 0; i< episodes; i++) {
            episodeList.add(new Episode(i, ""));
        }
    }
    
    public void setEpisodeAsWatched(int i) {
        if (episodeList.size() >= i) {
            episodeList.get(i).setWatched(true);
        }
    }
    
    public void setAllEpisodesWatched() {
        if (episodeList != null) {
            episodeList.forEach(e -> {
                e.setWatched(true);
            });
        }
    }
}

