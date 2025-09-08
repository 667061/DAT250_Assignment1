package no.hvl.dat250.Assignment1.Entities;

import lombok.*;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Poll {
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private List<VoteOption> voteOptions;


}
