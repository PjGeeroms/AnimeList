package entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import validation.OnPasswordUpdate;
import validation.ValidPassword;
import validation.ValidUsername;

@Entity
@Table(name = "TBL_USER")
// We gaan de paswoorden opslaan in een aparte tabel,
//dit is een goede gewoonte.
@SecondaryTable(name = "USER_PASSWORD") 
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE UPPER(u.username) LIKE UPPER(:username)"),
})
public class User implements Serializable
{
    @Id
    @ValidUsername
    private String username;
    
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
    
    private String fullName;

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    @Transient // Het plain text paswoord mag nooit opgeslagen worden.
    // En het moet enkel worden gevalideerd wanneer het is gewijzigd.
    @ValidPassword(groups = OnPasswordUpdate.class) 
    private String plainPassword;

    @NotNull // Dit zou nooit mogen gebeuren.
    // Dit zou eveneens nooit mogen gebeuren (wijst op fout bij encryptie).
    @Pattern(regexp = "[A-Fa-f0-9]{64}+") 
    @Column(name = "PASSWORD", table = "USER_PASSWORD")
    private String encryptedPassword;

    /*
     * Geef het geëncrypteerde paswoord terug,
     * of null indien er nog geen paswoord ingesteld is.
     */
    public String getPassword()
    {
        return encryptedPassword;
    }

    /*
     * Verandert het paswoord en encrypteert het meteen.
     */
    public void setPassword(String plainPassword)
    {
        this.plainPassword = plainPassword != null ? 
                plainPassword.trim() : "";
        
        // De onderstaande code zal het paswoord hashen met 
        //SHA-256 en de hexadecimale hashcode opslaan.
        try {
            BigInteger hash = new BigInteger(1, 
                    MessageDigest.getInstance("SHA-256")
                    .digest(this.plainPassword.getBytes("UTF-8")));
            encryptedPassword = hash.toString(16);
        } catch (NoSuchAlgorithmException | 
                UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    @ElementCollection
    // We kiezen hier zelf de naam voor de tabel en de
    //kolommen omdat we deze nodig hebben voor het
    // instellen van de security realm.
    @CollectionTable(name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USERNAME"))
    @Column(name = "ROLES")
    private final List<String> roles = new ArrayList<>();

    public List<String> getRoles()
    {
        return roles;
    }

    
    @ManyToMany(fetch = FetchType.EAGER)
    private final List<Anime> animes = new ArrayList<>();
    
    public List<Anime> getAnimes() {
        return animes;
    }
    
    public void addAnime(Anime a) {
        animes.add(a);
    }
    
    public void RemoveAnime(Anime a) {
        animes.remove(a);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.username, other.username);
    }

    @Override
    public String toString()
    {
        return username;
    }
}
