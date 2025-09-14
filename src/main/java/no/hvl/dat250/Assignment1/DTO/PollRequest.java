package no.hvl.dat250.Assignment1.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.hvl.dat250.Assignment1.Entities.VoteOption;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollRequest {
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private UUID creatorId;
    private List<VoteOption> options;
}

