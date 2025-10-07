package no.hvl.dat250.Assignment1.Entities;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @OneToMany(mappedBy = "voter")
    @JsonManagedReference("user-votes")
    private List<Vote> votes = new LinkedList<>(); //votes given to polls


    @OneToMany(mappedBy = "createdBy", orphanRemoval = true, cascade =  CascadeType.ALL)
    @JsonManagedReference("user-polls")
    private List<Poll> polls = new LinkedList<>(); //polls created


    /**
     * Creates a new Poll object for this user
     * with the given poll question
     * and returns it.
     * @param question - Question asked in the poll
     * @return - A newly created poll object with "this" set as creator.
     */
    public Poll createPoll(String question) {
            Poll newPoll = new Poll();
            newPoll.setQuestion(question);
            newPoll.setCreatedBy(this);
            this.polls.add(newPoll);
            return newPoll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     *
     * @param option - Option which is voted upon to be cast
     * @return - the newly created vote-object
     */
    public Vote voteFor(VoteOption option) {
        Vote newVote = new Vote();
        newVote.setVoter(this);
        newVote.setVotesOn(option);
        this.votes.add(newVote);
        return newVote;
    }
}