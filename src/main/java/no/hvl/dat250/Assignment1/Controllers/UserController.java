package no.hvl.dat250.Assignment1.Controllers;

import no.hvl.dat250.Assignment1.Entities.User;
import no.hvl.dat250.Assignment1.Repos.Storage;
import no.hvl.dat250.Assignment1.Service.PollManager;
import no.hvl.dat250.Assignment1.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.UnifiedJedis;

import java.util.Collection;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    UnifiedJedis jedis;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user.getUsername(), user.getEmail());
        jedis.sadd("loggedInUsers", createdUser.getUsername());
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/loggedin")
    public Collection<User> listLoggedInUsers() {
        return userService.getLoggedInUsers();
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        jedis.sadd("loggedInUsers", username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String username) {
        jedis.srem("loggedInUsers", username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Collection<User> listUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ?  ResponseEntity.ok(userService.getUserById(id)): ResponseEntity.notFound().build();
    }

}
