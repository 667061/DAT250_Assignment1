package no.hvl.dat250.Assignment1.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.hvl.dat250.Assignment1.Entities.Poll;
import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Repos.Storage;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
@AllArgsConstructor
@Service
public class UserService {


    private final Storage storage;



    public boolean createUser(User user){

        if(storage.getUsers().containsKey(user.getUserID()))
            return false;

        storage.getUsers().put(user.getUserID(),user);
        return true;
    }

    public User createUser(String username, String email){
        User user = new User(username,email);
        if(storage.getUsers().containsKey(user.getUserID()))
            return storage.getUsers().get(user.getUserID());

        storage.getUsers().put(user.getUserID(),user);
        return user;
    }

    public User findByUsername(String username){
       return storage.getUsers().values().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }



    public User getUserByUsername(String username){
        return storage.getUsers().values().stream().filter(u->u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public User getUserById(UUID id){return storage.getUsers().get(id);}

    public Collection<User> getUsers(){
        return storage.getUsers().values();
    }

    public void updateUser(User user){
         storage.getUsers().replace(user.getUserID(),user);
    }

    public void deleteUser(User user) {
        storage.getUsers().remove(user.getUserID());

    }

    public boolean deleteUser(UUID id) {
       if(!storage.getUsers().containsKey(id))
           return false;
        storage.getUsers().remove(id);
        return true;
    }

}
