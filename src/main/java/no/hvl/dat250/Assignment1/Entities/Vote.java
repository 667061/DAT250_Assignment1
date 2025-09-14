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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id = UUID.randomUUID();
    private Instant publishedAt = Instant.now();

    public Vote() {
        publishedAt = Instant.now();
        voteOptions = new ArrayList<>();
    }

    public Vote(List<String> voteOptions) {
        for (String option : voteOptions) {
            this.voteOptions.add(new VoteOption(option));
        }
    }

    @ManyToOne
    @JoinColumn(name = "voter_user_id")
    private User voter;


    @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VoteOption> voteOptions = new ArrayList<>();

}

