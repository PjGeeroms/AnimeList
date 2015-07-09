package json;

import entity.User;
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

/*
 * Deze klasse is een JAX-RS provider die JSON kan parsen tot een User.
 */

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class UserReader implements MessageBodyReader<User>
{
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return User.class.isAssignableFrom(type);
    }

    @Override
    public User readFrom(Class<User> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        try (JsonReader in = Json.createReader(entityStream)) {
            
            JsonObject jsonUser = in.readObject();
            User user = new User();
            
            user.setUsername(jsonUser.getString("username", null));
            user.setPassword(jsonUser.getString("password", null));
                 
            JsonArray jsonRoles = jsonUser.getJsonArray("roles");
            if (jsonRoles != null) {
                for (JsonString jsonRole : jsonRoles.getValuesAs(JsonString.class)) {
                    user.getRoles().add(jsonRole.getString());
                }
            }

            return user;
            
        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
    }
}
