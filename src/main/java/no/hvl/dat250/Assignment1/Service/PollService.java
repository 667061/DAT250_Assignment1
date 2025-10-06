package no.hvl.dat250.Assignment1.Service;


import lombok.AllArgsConstructor;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.Vote;
import no.hvl.dat250.Assignment1.Entities.VoteOption;
import no.hvl.dat250.Assignment1.Repos.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import redis.clients.jedis.UnifiedJedis;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PollService {

    private Storage storage;

    @Autowired
    private UnifiedJedis jedis;


    public Poll createPoll(Poll poll, UUID creatorId) throws InstanceAlreadyExistsException {
        if (storage.getPolls().containsKey(poll.getId()))
            throw new InstanceAlreadyExistsException("There is already such a poll"); //Poll already exists

        poll.getOptions().forEach(voteOption -> storage.getVoteOptions().put(voteOption.getId(), voteOption));
        storage.getPolls().put(poll.getId(), poll);
        return poll;
    }

    public Poll getPollById(UUID pollID){
        return storage.getPolls().get(pollID);
    }

    public Collection<Poll> getPollsByUser(UUID userID){
        return storage.getPolls().values().stream().filter(poll -> poll.createdBy.getId() == userID).collect(Collectors.toSet());
    }

    public boolean giveVote(UUID userId, UUID pollId, Vote vote) {
        Poll poll = storage.getPolls().get(pollId);
        if (poll == null) return false;

        // persist vote
        storage.getVotes().put(vote.getId(), vote);
        vote.getVotesOn().getVotes().add(vote);

        // invalidate cache
        jedis.del("poll:" + pollId + ":results");

        return true;
    }


    public boolean updateVote(UUID userId, UUID pollId, Vote vote){
        if(storage.getVotes().get(pollId) == null)
            return false;
        storage.getVotes().replace(vote.getId(), vote);
        return true;
    }

    public VoteOption getVoteOptionById(UUID voteOptionId){
        return storage.getVoteOptions().get(voteOptionId) == null ? null : storage.getVoteOptions().get(voteOptionId);

    }

    public boolean deleteVote(UUID pollId, UUID voteId){
       if(storage.getVotes().get(voteId) == null)
           return false;
        storage.getVotes().remove(voteId);
        return true;
    }

    public Map<String,Integer> getPollResults(UUID pollId){
        String redisKey = "poll:" + pollId + ":results";

        Map<String, String> cached = jedis.hgetAll("poll:" + pollId);
        if (!cached.isEmpty()){
            return cached.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Integer.parseInt(entry.getValue())));
        }

        Poll poll = storage.getPolls().get(pollId);
        if  (poll == null){ return Collections.emptyMap();}


        Map<String,Integer> results = new LinkedHashMap<>();

        for (VoteOption option : poll.getOptions()) {
           int count = option.getVotes().size();
           results.put(option.getCaption(), count);
           jedis.hset(redisKey, option.getCaption(), String.valueOf(count));
        }

        jedis.expire("poll:" + pollId, 3600); // optional TTL

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

    public boolean deletePoll(UUID id) {
        if (!storage.getPolls().containsKey(id))
            return false;
        storage.getPolls().remove(id);
        // --- ADD HERE: remove from Redis ---
        jedis.del("poll:" + id);
        return true;
    }



}
