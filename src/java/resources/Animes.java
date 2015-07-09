/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import com.sun.tools.ws.wsdl.framework.DuplicateEntityException;
import entity.Anime;
import entity.User;
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
import javax.ws.rs.CookieParam;
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
    public List<Anime> getAllAnime(){   
        System.out.println("Authorized: " + context.getUserPrincipal().getName());
        if (context.getUserPrincipal() != null) {
            String username = context.getUserPrincipal().getName();
            TypedQuery<User> query = em.createNamedQuery("User.findById", User.class).setParameter("username", username);
            User u = query.getSingleResult();
            System.out.println("Username: " + u.getUsername());
            List<Anime> animes = u.getAnimes();
            Collections.sort(animes, new AnimeAirDayComparator());
            System.out.println("Animes: ");
            animes.stream().forEach((anime) -> {
                System.out.println(anime.getTitle());
            });
            return animes;
        } else {
            throw new ForbiddenException("You have not been Authorized!");
        }
        
    }
    
    @Path("{search}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Anime> getAnime(@PathParam("search") String search)
    {
        if(context.getUserPrincipal() != null) {
            String username = context.getUserPrincipal().getName();
            TypedQuery<User> query = em.createNamedQuery("User.findById", User.class).setParameter("username", username);
            User u = query.getSingleResult();
            List<Anime> animes = u.getAnimes();
            List<Anime> filtered = new ArrayList<>();
            animes.stream().filter((a) -> (a.getTitle().toLowerCase().contains(search.toLowerCase()))).forEach((a) -> {
                filtered.add(a);
            });

            Collections.sort(filtered, new AnimeAirDayComparator());
            return filtered;
        } else {
            throw new ForbiddenException("You have not been Authorized!");
        }
        
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
    
    
    @Path("add/{search}/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAnime(@PathParam("search") String search, @PathParam("id") int id) throws URISyntaxException, AlreadyFoundException
    {
        if (context.getUserPrincipal() != null) {
            String username = context.getUserPrincipal().getName();
            Anime myAnime = em.find(Anime.class, id);
            List <Anime> animes;


            TypedQuery<User> findUser = em.createNamedQuery("User.findById", User.class).setParameter("username", username);
            User u = findUser.getSingleResult();

            if (myAnime != null) {
                if (u.getAnimes().contains(myAnime)) {
                    // Kijken of anime al in de lijst zit.
                    throw new AlreadyFoundException(myAnime);
                } else {
                    // Anime toevoegen aan gebruiker
                    u.addAnime(myAnime);
                    em.persist(u);
                }
            } else {
                // Anime opzoeken en toevoegen aan gebruiker
                XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(search));
                XmlToAnime.ReadXml();
                animes = XmlToAnime.getAnimes();

                // Kijken of de lijst niet leeg is
                if (!animes.isEmpty()) {   
                    for (Anime a : animes) {
                        if(a.getId() == id) {
                            myAnime = a;
                            break;
                        }
                    }



                    myAnime.setDescription(Replacer.replaceIllegalCharacters(myAnime.getDescription()));
                    em.persist(myAnime);
                    u.addAnime(myAnime);
                    em.persist(u);
                }

            }

            return Response.status(201).build();
        } else {
            throw new ForbiddenException("You have not been authorized!");
        }
        
    }
    
//    @Path("update/{id}")
//    @GET
//    public void updateAnimes(@PathParam("id") int id){
//        List <Anime> animes;
//        
//        Anime anime = em.find(Anime.class, id);
//        em.detach(anime);
//        
//        XmlToAnime.setUrl("http://myanimelist.net/api/anime/search.xml?q=" + Replacer.encodeUrl(anime.getTitle()));
//        XmlToAnime.ReadXml();
//        animes = XmlToAnime.getAnimes();
//        Anime updated = animes.get(0);
//        
//        anime.setId(updated.getId());
//        anime.setTitle(updated.getTitle());
//        anime.setDescription(updated.getDescription());
//        anime.setEpisodes(updated.getEpisodes());
//        anime.setImageUrl(updated.getImageUrl());
//        anime.setStatus(updated.getStatus());
//        anime.setType(updated.getType());
//        anime.setStartDate(updated.getStartDate());
//        anime.setEndDate(updated.getEndDate());
//        anime.setAirDay(updated.getAirDay());
//        anime.setWatchedEpisodes(updated.getWatchedEpisodes());
//        
//        em.merge(anime);
//    }
    
    @Path("delete/{id}")
    @DELETE
    public void deleteAnime(@PathParam("id") int id) {
        if (context.getUserPrincipal() != null) {
            String username = context.getUserPrincipal().getName();
            User u = em.find(User.class, username);
            Anime anime = em.find(Anime.class, id);
            List<Anime> animes = u.getAnimes();
            if (anime != null) {
                if (animes.contains(anime)) {
                    u.RemoveAnime(anime);
                    em.persist(u);
                } else {
                    throw new NotFoundException("Anime is not in your list!");
                }
            } else {
                throw new NotFoundException("Anime not found!");
            }
        } else {
            throw new ForbiddenException("You are not authorized!");
        }
    }
    
}
