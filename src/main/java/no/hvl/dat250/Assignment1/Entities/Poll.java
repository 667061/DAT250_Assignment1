package no.hvl.dat250.Assignment1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Poll {
    @Id
    private UUID id = UUID.randomUUID();
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @ManyToOne
    @JsonBackReference
    public User creator;

    @JsonManagedReference
    @OneToMany(mappedBy="yourEntity" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VoteOption> voteOptions = new ArrayList<>();


    @JsonProperty("options")
    public List<VoteOption> getVoteOptions() {
        return voteOptions;
    }

    @JsonProperty("options")
    public void setVoteOptions(List<VoteOption> options) {
        this.voteOptions = options;
    }


    public void giveVote(VoteOption voteOption){
        Vote vote = new Vote();
        vote.setVoteOptions(List.of(voteOption));
        vote.setPublishedAt(Instant.now());
        vote.setVoter(null);

    }

    public void addVoteOption(VoteOption voteOption){
        voteOptions.add(voteOption);
    }

    public boolean hasVoteOption(VoteOption voteOption){
        return  voteOptions.contains(voteOption);
    }

    public boolean hasVoteOption(UUID voteOptionId){
        return  voteOptions.stream().anyMatch(voteOption -> voteOption.getId().equals(voteOptionId));
    }

}
