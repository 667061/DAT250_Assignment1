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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //Crud polls
    @PostMapping
    public ResponseEntity<Poll> createPoll(@RequestBody PollRequest request) {
        User creator = userService.getUserById(request.getCreatorId());
        if (creator == null) return ResponseEntity.badRequest().build();

        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion());
        poll.setPublishedAt(request.getPublishedAt());
        poll.setValidUntil(request.getValidUntil());
        poll.setCreator(creator);
        poll.setVoteOptions(request.getOptions());

        pollService.createPoll(poll, creator.getUserID());
        return ResponseEntity.ok(poll);
    }






    @PutMapping("/{pollId}")
    public ResponseEntity<Poll> updatePoll(@RequestBody Poll poll, @PathVariable UUID pollId) {
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
           return ResponseEntity.ok(pollService.getPollResults(pollId));
    }


    //Crud votes

    @GetMapping("/{pollId}/votes")
    public Collection<Vote> getPollVotes(@PathVariable UUID pollId) {
        return pollService.getVotes(pollId);
    }

    @PostMapping("/{pollId}/vote")
    public ResponseEntity<Poll> giveVote(@PathVariable UUID pollId, @RequestBody VoteRequest voteRequest) {
        boolean success = pollService.giveVote(voteRequest.getUserId(), pollId, new Vote(voteRequest.getSelectedOptions()));
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
