package no.hvl.dat250.Assignment1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.Assignment1.DTO.PollRequest;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import no.hvl.dat250.Assignment1.Service.PollService;
import no.hvl.dat250.Assignment1.Service.UserService;
import no.hvl.dat250.Assignment1.Repos.Storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class Assignment1ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Storage storage;

    @BeforeEach
    public void setup() {
        storage.getUsers().clear();
        storage.getPolls().clear();
        storage.getVotes().clear();
        storage.getVoteOptions().clear();
    }

    @Test
    public void testPollCreationAndVoting() throws Exception {
        // Step 1: Create User
        User user = new User("User1", "user1@example.com");

        MvcResult userResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class);

        // Step 2: Create Poll with VoteOptions
        VoteOption option1 = new VoteOption("Red");
        VoteOption option2 = new VoteOption("Blue");

        PollRequest pollRequest = new PollRequest();
        pollRequest.setQuestion("What's your favorite color?");
        pollRequest.setPublishedAt(Instant.now());
        pollRequest.setValidUntil(Instant.now().plusSeconds(3600));
        pollRequest.setCreatorId(createdUser.getId());
        pollRequest.setOptions(List.of(option1, option2));

        MvcResult pollResult = mockMvc.perform(post("/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Poll createdPoll = objectMapper.readValue(pollResult.getResponse().getContentAsString(), Poll.class);
        UUID pollId = createdPoll.getId();

        // Grab the actual option IDs from the created poll
        UUID optionIdRed = createdPoll.getOptions().get(0).getId();

        // Step 3: Create second user to vote
        User voter = new User("User2", "user2@example.com");

        MvcResult voterResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voter)))
                .andExpect(status().isOk())
                .andReturn();

        User createdVoter = objectMapper.readValue(voterResult.getResponse().getContentAsString(), User.class);

        // Step 4: Cast vote using VoteRequest DTO (single-choice)
        Map<String, Object> voteRequest = new HashMap<>();
        voteRequest.put("userId", createdVoter.getId());
        voteRequest.put("selectedOptionId", optionIdRed); // ✅ use ID, not caption

        mockMvc.perform(post("/polls/" + pollId + "/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // Step 5: Verify results
        mockMvc.perform(get("/polls/" + pollId + "/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Red").value(1))
                .andExpect(jsonPath("$.Blue").value(0));
    }
}