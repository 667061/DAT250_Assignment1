package no.hvl.dat250.Assignment1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor

@Data
@Getter
@Setter
@Entity
public class VoteOption {

    public VoteOption(String option) {
        this.caption = option;
    }
    public VoteOption() {
        this.caption = ""; // or throw an exception if caption cannot be empty

    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String caption;
    private int presentationOrder = 0; //Default is order by creation, unless further specified.
    private int voteCount = 0; //Number votes gotten

    @ManyToOne
    @JoinColumn(name ="poll_id")
    @JsonBackReference("poll-options")
    private Poll poll;


    @JsonProperty("voteCount")

    public int getVoteCount(){
        return votes != null ? votes.size() : 0;
    }

    @OneToMany(mappedBy = "votesOn", cascade =   CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference("option-votes")
    private List<Vote> votes = new ArrayList<>();



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteOption that = (VoteOption) o;
        return this.id == that.getId();
    }


    @Override
    public int hashCode() {
        return Objects.hash(caption, presentationOrder);
    }



}

