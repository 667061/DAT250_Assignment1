package no.hvl.dat250.Assignment1.Service;


import lombok.AllArgsConstructor;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import no.hvl.dat250.Assignment1.Repos.Storage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PollService {


    private Storage storage;



    public boolean createPoll(Poll poll, UUID creatorId) {
        if(storage.getPolls().containsKey(poll.getId()))
            return false; //Poll already exists

        poll.getOptions().forEach(voteOption -> storage.getVoteOptions().put(voteOption.getId(), voteOption));
        storage.getPolls().put(poll.getId(), poll);
        return true;
    }

    public Poll getPollById(UUID pollID){
        return storage.getPolls().get(pollID);
    }

    public Collection<Poll> getPollsByUser(UUID userID){
        return storage.getPolls().values().stream().filter(poll -> poll.createdBy.getId() == userID).collect(Collectors.toSet());
    }

    public boolean giveVote(UUID userId, UUID pollId, Vote vote) {
        if(!storage.getUsers().containsKey(userId))
            return false;
        vote.setVoter(storage.getUsers().get(userId));
        storage.getVotes().put(pollId, vote);
        VoteOption voteOption = vote.getVotesOn();

        //Increment if exists
        if (storage.getVoteOptions().values().stream().anyMatch(v -> v.hasSameCaption(voteOption))){

        }else { //create new if it doesn't exist.


        };
        return true;

    }

    public boolean updateVote(UUID userId, UUID pollId, Vote vote){
        if(storage.getVotes().get(pollId) == null)
            return false;
        storage.getVotes().replace(vote.getId(), vote);
        return true;
    }

    public boolean deleteVote(UUID pollId, UUID voteId){
       if(storage.getVotes().get(voteId) == null)
           return false;
        storage.getVotes().remove(voteId);
        return true;
    }

    public Map<String,Integer> getPollResults(UUID pollId){
       Collection<VoteOption> options = storage.getPolls().get(pollId).getOptions();
       Map<String,Integer> results = new HashMap<>();
       for (VoteOption option : options) {
           results.put(option.getCaption(),option.getVotes().size());
       }
       return results;
    }



    public Poll getPollByQuestion(String question){
        return storage.getPolls().values().stream().filter(poll -> poll.getQuestion().equals(question)).findFirst().orElse(null);
    }

    public Collection<Poll> getPolls(){return storage.getPolls().values();}


    public boolean addVoteOption(UUID pollID, VoteOption option){
        if(storage.getPolls().get(pollID).hasVoteOption(option))
            return false;

        storage.getPolls().get(pollID).addVoteOption(option);
        return true;
    }

    public Collection<Vote> getVotes(UUID pollId){
        return storage.getPolls().get(pollId).getOptions().stream().map(VoteOption::getVotes).flatMap(Collection::stream).collect(Collectors.toSet());
    }


    public Poll updateVoteOption(UUID pollID, VoteOption voteOption){
        if(storage.getPolls().get(pollID).hasVoteOption(voteOption)) {
            storage.getPolls().get(pollID).getOptions().remove(voteOption);
            storage.getPolls().get(pollID).getOptions().add(voteOption);
        }
        return storage.getPolls().get(pollID);
    }

    public boolean deleteVoteOption(UUID pollID, UUID optionId){
        if(!storage.getPolls().get(pollID).hasVoteOption(optionId))
            return false;

        storage.getPolls().get(pollID).getOptions().removeIf(voteOption -> voteOption.getId().equals(optionId));
        return true;
    }


    public Collection<VoteOption> getPollVoteOptions(UUID pollID){
        return storage.getPolls().get(pollID).getOptions();
    }

    public void updatePoll(Poll poll){
       storage.getPolls().replace(poll.getId(),poll);
    }

    public void deletePoll(Poll poll){
        storage.getPolls().remove(poll.getId());
    }

    public boolean deletePoll(UUID id){

        if(!storage.getPolls().containsKey(id))
            return false;

        storage.getPolls().remove(id);
        return true;
    }


}
