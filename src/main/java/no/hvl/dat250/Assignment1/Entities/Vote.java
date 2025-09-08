package no.hvl.dat250.Assignment1.Entities;

import lombok.*;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter

public class Vote {

    private Instant publishedAt;
    private Set<VoteOption> voteOptions;

}

