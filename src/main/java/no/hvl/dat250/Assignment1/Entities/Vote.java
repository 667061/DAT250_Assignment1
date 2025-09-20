package no.hvl.dat250.Assignment1.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private User voter;



    @ManyToOne
    private VoteOption votesOn;

}

