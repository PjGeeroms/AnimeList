package json;

import entity.Anime;
import entity.Episode;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import resources.AnimeStatus;

/*
 * Deze klasse is een JAX-RS provider die JSON kan parsen tot een Anime.
 */

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class AnimeReader implements MessageBodyReader<Anime>
{
    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType)
    {
        return Anime.class.isAssignableFrom(type);
    }

    @Override
    public Anime readFrom(Class<Anime> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
            throws IOException, WebApplicationException
    {
        try (JsonReader in = Json.createReader(entityStream)) {
            
            JsonObject jsonAnime = in.readObject();
            Anime anime = new Anime();
            
            anime.setId(jsonAnime.getInt("id", 0));
            anime.setTitle(jsonAnime.getString("title",null));
            anime.setDescription(jsonAnime.getString("description", null));
            anime.setEpisodes(jsonAnime.getInt("episodes", 0));
            anime.setWatchedEpisodes(jsonAnime.getInt("watchedEpisodes", 0));
            anime.setType(Anime.TYPE.valueOf(jsonAnime.getString("type", "TV")));
            anime.setStatus(AnimeStatus.valueOf(jsonAnime.getString("status", "Currently Airing")));
            anime.setImageUrl(jsonAnime.getString("img", null));
            anime.setStartDate(jsonAnime.getString("startDate", null));
            anime.setEndDate(jsonAnime.getString("endDate",null));
            
            JsonArray episodeList =  jsonAnime.getJsonArray("episodeList");
            JsonObject episode;
            for (int i = 0; i < episodeList.size(); i++) {
                episode = episodeList.getJsonObject(i);
                anime.addEpisodeToList(new Episode(episode.getInt("episode", i), episode.getString("title", "")));
            }
            return anime;
            
        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
    }
}
