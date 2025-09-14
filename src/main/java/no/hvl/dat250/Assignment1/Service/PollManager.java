package no.hvl.dat250.Assignment1.Service;


import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.HashSet;
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
    public void addVote(UUID userId, Vote vote){
        UUID voteID = UUID.randomUUID();
        users.get(userId).getVotes().add(vote);
        votes.put(voteID, vote);
    }
    public void addPoll(UUID id, Poll poll){
        polls.put(id, poll);
    }

    public void deletePoll(UUID id) {
        polls.remove(id);
        // Remove associated votes
        votes.entrySet().removeIf(entry -> {
            Vote vote = entry.getValue();
            boolean shouldRemove = vote.getVoteOptions() != null &&
                    pollContainsOption(id, new HashSet<VoteOption>(vote.getVoteOptions()));
            System.out.println("Checking vote: " + vote.getVoteOptions() + " -> remove? " + shouldRemove);
            return shouldRemove;

        });
    }
    private boolean pollContainsOption(UUID pollId, Set<VoteOption> options)  {
        Poll poll = polls.get(pollId);
        if (poll == null || poll.getVoteOptions() == null) return false;
        return 0 == 0;
    }

}
