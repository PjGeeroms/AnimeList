package json;

import entity.Anime;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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
import resources.CalculateNextRelease;
import resources.Replacer;

/*
 * Deze klasse is een JAX-RS provider die een List<Anime>
 * kan uitschrijven als JSON.
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class AnimeListWriter implements MessageBodyWriter<List<Anime>>
{
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType)
    {
        if (!List.class.isAssignableFrom(type)) {
            return false;
        }

        // Het volgende stukje code controleert of de List 
        //wel een List<User> was.
        if (genericType instanceof ParameterizedType) {
            Type[] arguments = ((ParameterizedType) genericType)
                    .getActualTypeArguments();
            return arguments.length == 1 && arguments[0]
                    .equals(Anime.class);
        } else {
            return false;
        }
    }

    @Override
    public long getSize(List<Anime> animes, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(List<Anime> animes, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws IOException, WebApplicationException
    {
        JsonArrayBuilder jsonAnimes = Json.createArrayBuilder();
        int i = 0;
        for (Anime anime : animes) {
            JsonObjectBuilder jsonAnime = Json.createObjectBuilder();
            
            jsonAnime.add("id", anime.getId());
            jsonAnime.add("title", anime.getTitle());
            jsonAnime.add("description", Replacer.replaceIllegalCharacters(anime.getDescription()));
            jsonAnime.add("episodes", anime.getEpisodes());
            jsonAnime.add("watchedEpisodes", anime.getWatchedEpisodes());
            jsonAnime.add("type", anime.getType().name());
            jsonAnime.add("status", anime.getStatus().toString());
            jsonAnime.add("img", anime.getImageUrl());
            jsonAnime.add("startDate", anime.getStartDate());
            jsonAnime.add("endDate", anime.getEndDate());
            jsonAnime.add("nextAirDate", CalculateNextRelease.getNextAirDate(anime));
            
            JsonArrayBuilder genres = Json.createArrayBuilder();
            anime.getGenres().stream().forEach((genre) -> {
                genres.add(genre);
            });
            jsonAnime.add("genres", genres);
            
            jsonAnimes.add(jsonAnime);
        }
        
        try (JsonWriter out = Json.createWriter(entityStream)) {
            out.writeArray(jsonAnimes.build());
        }
    }
}
