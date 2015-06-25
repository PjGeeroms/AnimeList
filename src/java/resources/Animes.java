/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import com.sun.tools.ws.wsdl.framework.DuplicateEntityException;
import entity.Anime;
import exceptions.AlreadyFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author PieterJan
 */
@RequestScoped
@Path("animes")
@Transactional(dontRollbackOn = {BadRequestException.class,
    ForbiddenException.class, NotFoundException.class, AlreadyFoundException.class})
public class Animes {
    @PersistenceContext
    private EntityManager em;
    
    @Context
    private SecurityContext context;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Anime> getAllAnime(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("results") @DefaultValue("10") int results)
    {
        TypedQuery<Anime> queryFindAll = 
                em.createNamedQuery("Anime.findAll", Anime.class);
        
        queryFindAll.setFirstResult(first);
        List <Anime> animes = queryFindAll.getResultList();
        Collections.sort(animes, new AnimeAirDayComparator());
        return animes;
    }
    
    @Path("{search}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Anime> getAnime(@PathParam("search") String search)
    {
        List<Anime> animes;
        TypedQuery query = em.createNamedQuery("Anime.findByName", Anime.class).setParameter("title", "%"+search+"%");
        animes = query.getResultList();
        Collections.sort(animes, new AnimeAirDayComparator());
        return animes;
    }
    
    @Path("add/{search}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Anime> searchAnimes(@PathParam("search") String search)
    {
        List<Anime> animes;
        XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(search));
        XmlToAnime.ReadXml();
        animes = XmlToAnime.getAnimes();
        
        return animes;
    }
    
    //Overbodig
    @Path("add/{search}/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Anime searchAnime(@PathParam("search") String search, @PathParam("id") int id)
    {
        List<Anime> animes;
        XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(search));
        XmlToAnime.ReadXml();
        animes = XmlToAnime.getAnimes();
        if (!animes.isEmpty() && animes.size() >= id) {
            return animes.get(id);
        }
        
        
        return null;
    }
    
    @Path("add/{search}/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAnime(@PathParam("search") String search, @PathParam("id") int id) throws URISyntaxException, AlreadyFoundException
    {
        if (!context.isUserInRole("admin")) {
            throw new ForbiddenException();
        }
        
        List<Anime> animes;
        XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(search));
        XmlToAnime.ReadXml();
        animes = XmlToAnime.getAnimes();
        if (!animes.isEmpty() && animes.size() >= id) {
            //return animes.get(id);
            animes.get(id).setDescription(Replacer.replaceIllegalCharacters(animes.get(id).getDescription()));
            TypedQuery query = em.createNamedQuery("Anime.findByName", Anime.class).setParameter("title", animes.get(id).getTitle());
            List<Anime> results = query.getResultList();
            
            if (results.isEmpty()) {
                em.persist(animes.get(id));
            } else {
                throw new AlreadyFoundException(animes.get(id));
            }
            
        }
        return Response.status(201).build();
    }
    
    @Path("update/{id}")
    @GET
    public void updateAnimes(@PathParam("id") int id){
        List <Anime> animes;
        
        Anime anime = em.find(Anime.class, id);
        em.detach(anime);
        
        XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(anime.getTitle()));
        XmlToAnime.ReadXml();
        animes = XmlToAnime.getAnimes();
        Anime updated = animes.get(0);
        
        anime.setTitle(updated.getTitle());
        anime.setDescription(updated.getDescription());
        anime.setEpisodes(updated.getEpisodes());
        anime.setImageUrl(updated.getImageUrl());
        anime.setStatus(updated.getStatus());
        anime.setType(updated.getType());
        anime.setStartDate(updated.getStartDate());
        anime.setEndDate(updated.getEndDate());
        anime.setAirDay(updated.getAirDay());
        anime.setWatchedEpisodes(updated.getWatchedEpisodes());
        
        em.merge(anime);
    }
    
    @Path("delete/{id}")
    @DELETE
    public void deleteAnime(@PathParam("id") int id) {
        if (!context.isUserInRole("admin")) {
            throw new ForbiddenException();
        }
        
        Anime anime = em.find(Anime.class, id);
        if (anime != null) {
            em.remove(anime);
        } else {
            throw new NotFoundException("Anime not found!");
        }
    }
    
}
