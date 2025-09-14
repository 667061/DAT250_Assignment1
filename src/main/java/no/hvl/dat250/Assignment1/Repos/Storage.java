package no.hvl.dat250.Assignment1.Repos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@Component
public class Storage {
    private final HashMap<UUID, Poll> polls;
    private final HashMap<UUID, Vote> votes;
    private final HashMap<UUID, User> users;
    private final HashMap<UUID, VoteOption> voteOptions;

    public Storage() {
        polls = new HashMap<>();
        votes = new HashMap<>();
        users = new HashMap<>();
        voteOptions = new HashMap<>();
    }


}
