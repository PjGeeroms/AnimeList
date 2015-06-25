package json;

import entity.Anime;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import resources.Replacer;

/*
 * Deze klasse is een JAX-RS provider die een User kan uitschrijven
 * als JSON.
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class AnimeWriter implements MessageBodyWriter<Anime>
{
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType)
    {
        return Anime.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Anime anime, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(Anime anime, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws IOException, WebApplicationException
    {
        
       
        JsonObjectBuilder jsonAnime = Json.createObjectBuilder();
        if(anime.getId() != null) {
            jsonAnime.add("id", anime.getId());
        } else {
            jsonAnime.add("id", 0);
        }
        jsonAnime.add("title", anime.getTitle());
        jsonAnime.add("description", Replacer.replaceIllegalCharacters(anime.getDescription()));
        jsonAnime.add("episodes", anime.getEpisodes());
        jsonAnime.add("watchedEpisodes", anime.getWatchedEpisodes());
        jsonAnime.add("type", anime.getType().name());
        jsonAnime.add("status", anime.getStatus().toString());
        jsonAnime.add("img", anime.getImageUrl());
        jsonAnime.add("startDate", anime.getStartDate());
        jsonAnime.add("endDate", anime.getEndDate());
            
        JsonArrayBuilder genres = Json.createArrayBuilder();
        anime.getGenres().stream().forEach((genre) -> {
            genres.add(genre);
        });
        jsonAnime.add("genres", genres);

        
        try (JsonWriter out = Json.createWriter(entityStream)) {
            out.writeObject(jsonAnime.build());
        }
    }
}
