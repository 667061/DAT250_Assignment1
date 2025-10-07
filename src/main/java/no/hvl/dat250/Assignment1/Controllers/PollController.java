package no.hvl.dat250.Assignment1.Controllers;
import no.hvl.dat250.Assignment1.DTO.PollRequest;
import no.hvl.dat250.Assignment1.DTO.VoteRequest;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import no.hvl.dat250.Assignment1.Service.PollService;
import no.hvl.dat250.Assignment1.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.UnifiedJedis;

import java.util.*;
@CrossOrigin
@RestController
@RequestMapping("/polls")
public class PollController {

    //Inject
    @Autowired
    private PollService pollService;

    @Autowired
    private UserService userService;

    @Autowired
    UnifiedJedis jedis;
    //Crud polls
    @PostMapping
    public ResponseEntity<Poll> createPoll(@RequestBody PollRequest request) {
        User creator = userService.getUserById(request.getCreatorId());
        if (creator == null || !jedis.sismember("loggedInUsers", creator.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Poll poll = new Poll();
        poll.setId(UUID.randomUUID());
        poll.setQuestion(request.getQuestion());
        poll.setPublishedAt(request.getPublishedAt());
        poll.setValidUntil(request.getValidUntil());
        poll.setCreatedBy(creator);

        List<VoteOption> options = new ArrayList<>();
        for(String caption : request.getOptions()) {
            VoteOption option = new VoteOption(caption);
            option.setId(UUID.randomUUID());
            option.setPoll(poll);
            options.add(option);
        }
        poll.setOptions(options);

        for (VoteOption option : poll.getOptions()) {
            jedis.hset("poll:" + poll.getId(), option.getId().toString(), "0");
        }

        try {
            pollService.createPoll(poll, creator.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


        return ResponseEntity.ok(poll);
    }




    @PutMapping("/{pollId}")
    public ResponseEntity<Poll> updatePoll(@RequestBody Poll poll, @PathVariable String pollId) {
        pollService.updatePoll(poll);
        return ResponseEntity.ok(poll);
    }

    @GetMapping
    public Collection<Poll> listPolls() {
        return pollService.getPolls();
    }
    @GetMapping("/{userid}")
    public Collection<Poll> listPollsByUser(@PathVariable UUID userid) {
        return pollService.getPollsByUser(userid);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable UUID id) {
        jedis.del("poll:" + id);
        boolean hasBeenDeleted = pollService.deletePoll(id);
        return hasBeenDeleted ? ResponseEntity.noContent().build() :  ResponseEntity.notFound().build();
    }

    //Crud vote options

    @GetMapping("/{pollId}/options")
    public Collection<VoteOption> listPollOptions(@PathVariable UUID pollId) {
        return pollService.getPollVoteOptions(pollId);
    }

    @PostMapping("/{pollId}/options")
    public ResponseEntity<VoteOption> addPollOption(@PathVariable UUID pollId, @RequestBody VoteOption option)
    {
        return pollService.addVoteOption(pollId, option) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{pollId}/options/{optionId}")
    public  ResponseEntity<VoteOption> updatePollOption(@PathVariable UUID pollId,@PathVariable UUID optionId,@RequestBody VoteOption option){
        pollService.updateVoteOption(pollId,option);
        return ResponseEntity.ok(option);
    }

    @DeleteMapping("/{pollId}/options/{optionId}")
    public ResponseEntity<Void> deletePollOption(@PathVariable UUID pollId,@PathVariable UUID optionId) {
       boolean successFullDelete = pollService.deleteVoteOption(pollId,optionId);
        return successFullDelete ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{pollId}/results")
    public ResponseEntity<Map<String,Integer>> listPollResults(@PathVariable UUID pollId) {
        Map<String, String> redisResults = jedis.hgetAll("poll:" + pollId);
        Map<String, Integer> results = new HashMap<>();
        for(VoteOption option : pollService.getPollVoteOptions(pollId)) {
            String count = redisResults.get(option.getId().toString());
            int value = (count != null) ? Integer.parseInt(count) : 0;
            results.put(option.getCaption(), value);
        }
        return ResponseEntity.ok(results);
    }


    //Crud votes

    @GetMapping("/{pollId}/votes")
    public Collection<Vote> getPollVotes(@PathVariable UUID pollId) {
        return pollService.getVotes(pollId);
    }

    @PostMapping("/{pollId}/vote")
    public ResponseEntity<Poll> giveVote(@PathVariable UUID pollId, @RequestBody VoteRequest voteRequest) {
        User voter = userService.getUserById(voteRequest.getUserId());
        if (voter == null || !jedis.sismember("loggedInUsers", userService.getUserById(voteRequest.getUserId()).getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        VoteOption selectedOption = pollService.getVoteOptionById(voteRequest.getSelectedOptionId());
        if (selectedOption == null || !selectedOption.getPoll().getId().equals(pollId)) {
            return ResponseEntity.badRequest().build();
        }
        String userVoteKey = "poll:" + pollId + ":userVotes";
        String previousOptionId = jedis.hget(userVoteKey, voter.getId().toString());
        if(previousOptionId != null) {
            if(previousOptionId.equals(selectedOption.getId().toString())) {
                return ResponseEntity.ok(pollService.getPollById(pollId));
            } else{
                jedis.hincrBy("poll:" + pollId, previousOptionId, -1);
                jedis.hincrBy("poll:" + pollId, selectedOption.getId().toString(), 1);
                jedis.hset(userVoteKey, voter.getId().toString(), selectedOption.getId().toString());
            }
        } else{
            jedis.hincrBy("poll:" + pollId, selectedOption.getId().toString(), 1);
            jedis.hset(userVoteKey, voter.getId().toString(), selectedOption.getId().toString());
        }


        Vote vote = new Vote(selectedOption);
        vote.setVoter(voter);

        boolean success = pollService.giveVote(voter.getId(), pollId, vote);
        return success ? ResponseEntity.ok(pollService.getPollById(pollId)) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{pollId}/vote/{voteId}")
    public ResponseEntity<Poll> updateVote(@PathVariable UUID pollId, @RequestParam UUID userId, @RequestBody Vote vote,@PathVariable UUID voteId) {
        boolean success = pollService.updateVote(userId,pollId,vote);
        return success ? ResponseEntity.ok(pollService.getPollById(pollId)): ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{pollId}/vote/{voteId}")
    public ResponseEntity<Poll> deleteVote(@PathVariable UUID pollId, @PathVariable UUID voteId) {
        boolean success = pollService.deleteVote(pollId,voteId);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}
