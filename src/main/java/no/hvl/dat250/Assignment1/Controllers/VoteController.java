package no.hvl.dat250.Assignment1.Controllers;

import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private PollManager pollManager;

    @PostMapping("/{userId}")
    public void vote(@PathVariable UUID userId, @RequestBody Vote vote) {
        pollManager.addVote(userId, vote);
    }

    @GetMapping
    public Collection<Vote> listVotes() {
        return pollManager.getVotes().values();
    }
}

