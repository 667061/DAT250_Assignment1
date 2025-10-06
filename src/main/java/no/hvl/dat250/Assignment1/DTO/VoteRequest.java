package no.hvl.dat250.Assignment1.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    private UUID userId;
    private UUID selectedOptionId;

    // Getters and setters
}
