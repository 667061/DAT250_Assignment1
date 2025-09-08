package no.hvl.dat250.Assignment1.Service;


import lombok.Getter;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@Getter
@Component
public class PollManager {

    private final HashMap<UUID, Poll> polls;
    private final HashMap<UUID, Vote> votes;
    private final HashMap<UUID, User> users;

    public PollManager(){
        polls = new HashMap<>();
        votes = new HashMap<>();
        users = new HashMap<>();
    }

    public void addUser(UUID id, User user){
        users.put(id, user);
    }
    public void addVote(UUID id, Vote vote){
        votes.put(id, vote);
    }
    public void addPoll(UUID id, Poll poll){
        polls.put(id, poll);
    }

    public void deletePoll(UUID id) {
        polls.remove(id);
        // Remove associated votes
        votes.entrySet().removeIf(entry -> {
            Vote vote = entry.getValue();
            return vote.getVoteOptions() != null &&
                    pollContainsOption(id, vote.getVoteOptions());
        });
    }
    private boolean pollContainsOption(UUID pollId, Set<VoteOption> options) {
        Poll poll = polls.get(pollId);
        if (poll == null || poll.getVoteOptions() == null) return false;

        return options.stream().anyMatch(poll.getVoteOptions()::contains);
    }
}
