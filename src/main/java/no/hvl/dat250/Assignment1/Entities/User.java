package no.hvl.dat250.Assignment1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
public class User {

    @Id
    private UUID userID = UUID.randomUUID();
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        userID = UUID.randomUUID();
    }

    @OneToMany(mappedBy = "voter",fetch = FetchType.EAGER)
    private List<Vote> votes; //votes given to polls


    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Poll> polls; //polls created


}
