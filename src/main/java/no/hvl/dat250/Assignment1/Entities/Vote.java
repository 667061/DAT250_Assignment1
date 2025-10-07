package no.hvl.dat250.Assignment1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

@AllArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant publishedAt = Instant.now();

    public Vote() {
        publishedAt = Instant.now();
        votesOn = new VoteOption();
    }

    public Vote(VoteOption voteOption) {
        this.votesOn = voteOption;
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference("user-votes")
    private User voter;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @ManyToOne
    @JoinColumn(name="option_id")
    @JsonBackReference("option-votes")
    private VoteOption votesOn;

}

