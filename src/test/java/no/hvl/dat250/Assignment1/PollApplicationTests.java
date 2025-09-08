package no.hvl.dat250.Assignment1;


import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import no.hvl.dat250.Assignment1.Service.PollManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PollApplicationTests {

    @Autowired
    private PollManager pollManager;

    @Test
    public void testScenario() {
        // Create users
        User user1 = new User();
        user1.setUsername("alice");
        user1.setEmail("alice@example.com");
        UUID user1Id = UUID.randomUUID();
        pollManager.addUser(user1Id, user1);

        User user2 = new User();
        user2.setUsername("bob");
        user2.setEmail("bob@example.com");
        UUID user2Id = UUID.randomUUID();
        pollManager.addUser(user2Id, user2);

        assertEquals(2, pollManager.getUsers().size());

        // Create poll
        Poll poll = new Poll();
        poll.setQuestion("Favorite color?");
        poll.setPublishedAt(Instant.now());
        poll.setValidUntil(Instant.now().plusSeconds(3600));

        VoteOption red = new VoteOption();
        red.setCaption("Red");
        red.setPresentationOrder(1);

        VoteOption blue = new VoteOption();
        blue.setCaption("Blue");
        blue.setPresentationOrder(2);

        poll.setVoteOptions(Arrays.asList(red, blue));
        UUID pollId = UUID.randomUUID();
        pollManager.addPoll(pollId, poll);

        assertEquals(1, pollManager.getPolls().size());

        // User 2 votes
        Vote vote = new Vote();
        vote.setPublishedAt(Instant.now());
        VoteOption redOption = new VoteOption();
        redOption.setCaption("red");
        Set<VoteOption> voteOptions = new HashSet<>();
        voteOptions.add(redOption);
        vote.setVoteOptions(voteOptions);
        pollManager.addVote(user2Id, vote);

        // User 2 changes vote
        Vote newVote = new Vote();
        newVote.setPublishedAt(Instant.now());
        VoteOption blueOption = new VoteOption();
        blueOption.setCaption("Blue");
        Set<VoteOption> voteOptions1 = new HashSet<>();
        voteOptions1.add(blueOption);
        newVote.setVoteOptions(voteOptions1);
        pollManager.addVote(user2Id, newVote);

        assertEquals(1, pollManager.getVotes().size());
        assertTrue(pollManager.getVotes().get(user2Id).getVoteOptions().stream().anyMatch( s -> s.getCaption().equals("Blue")));

        // Delete poll
        pollManager.deletePoll(pollId);
        assertEquals(0, pollManager.getVotes().size());

    }
}
