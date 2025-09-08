package no.hvl.dat250.Assignment1.Controllers;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/polls")
public class PollController {

    @Autowired
    private PollManager pollManager;

    @PostMapping
    public UUID createPoll(@RequestBody Poll poll) {
        UUID id = UUID.randomUUID();
        pollManager.addPoll(id, poll);
        return id;
    }

    @GetMapping
    public Collection<Poll> listPolls() {
        return pollManager.getPolls().values();
    }

    @DeleteMapping("/{id}")
    public void deletePoll(@PathVariable UUID id) {
        pollManager.deletePoll(id);
    }
}
