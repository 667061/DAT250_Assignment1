package no.hvl.dat250.Assignment1.Entities;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class VoteOption {

    public VoteOption(String option) {
        caption = option;
    }

    private String caption;
    private int presentationOrder = 0; //Default is ordery by selection, unless further specified.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteOption that = (VoteOption) o;
        return presentationOrder == that.presentationOrder &&
                Objects.equals(caption, that.caption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caption, presentationOrder);
    }

}

