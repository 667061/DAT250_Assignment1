package no.hvl.dat250.Assignment1.Controllers;

import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PollManager pm;


    @PostMapping
    public UUID createUser(@RequestBody User user) {
        UUID id = UUID.randomUUID();
        pm.addUser(id, user);
        return id;
    }

    @GetMapping
    public Collection<User> listUsers() {
        return pm.getUsers().values();
    }


}
