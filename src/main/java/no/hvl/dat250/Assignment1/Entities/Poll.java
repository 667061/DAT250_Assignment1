package no.hvl.dat250.Assignment1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @ManyToOne
    @JoinColumn(name = "userID")
    @JsonBackReference("user-polls")
    @ToString.Exclude
    public User createdBy;

    @JsonManagedReference("poll-options")
    @ToString.Exclude
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();


    public void addVoteOption(VoteOption voteOption){
        options.add(voteOption);
    }

    public boolean hasVoteOption(VoteOption voteOption){
        return  options.contains(voteOption);
    }

    public boolean hasVoteOption(UUID voteOptionId){
        return  options.stream().anyMatch(voteOption -> voteOption.getId().equals(voteOptionId));
    }

    /**
     *
     * Adds a new option to this Poll and returns the respective
     * VoteOption object with the given caption.
     * The value of the presentationOrder field gets determined
     * by the size of the currently existing VoteOptions for this Poll.
     * I.e. the first added VoteOption has presentationOrder=0, the secondly
     * registered VoteOption has presentationOrder=1 and so on.
     * @param caption - Caption to assign to the new VoteOption
     * @return - return the newly created VoteOption
     */
    public VoteOption addVoteOption(String caption) {

        VoteOption voteOption = new VoteOption(caption);
        voteOption.setPresentationOrder(options.size());
        voteOption.setPoll(this);
        options.add(voteOption);

        return voteOption;
    }



}
