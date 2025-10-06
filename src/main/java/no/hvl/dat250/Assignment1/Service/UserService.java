package no.hvl.dat250.Assignment1.Service;

import lombok.AllArgsConstructor;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Repos.Storage;

import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
@AllArgsConstructor
@Service
public class UserService {


    private final Storage storage;
    private final UnifiedJedis jedis;

    public Collection<User> getLoggedInUsers(){
        Set<String> loggedInUsernames = jedis.smembers("loggedInUsers");
        return storage.getUsers().values().stream().filter(u -> loggedInUsernames.contains(u.getUsername())).toList();
    }

    public boolean createUser(User user){

        if(storage.getUsers().containsKey(user.getId()))
            return false;

        storage.getUsers().put(user.getId(),user);
        return true;
    }

    public User createUser(String username, String email){
        User user = new User(username,email);
        user.setId(UUID.randomUUID());
        if(storage.getUsers().containsKey(user.getId()))
            return storage.getUsers().get(user.getId());

        storage.getUsers().put(user.getId(),user);
        return user;
    }


    public User getUserByUsername(String username){
        return storage.getUsers().values().stream().filter(u->u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public User findByUsername(String username){
       return storage.getUsers().values().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }




    public User getUserById(UUID id){return storage.getUsers().get(id);}

    public Collection<User> getUsers(){
        return storage.getUsers().values();
    }

    public void updateUser(User user){
         storage.getUsers().replace(user.getId(),user);
    }

    public void deleteUser(User user) {
        storage.getUsers().remove(user.getId());

    }

    public boolean deleteUser(UUID id) {
       if(!storage.getUsers().containsKey(id))
           return false;
        storage.getUsers().remove(id);
        return true;
    }

}
