package nl.tudelft.wdm.group1.users.web;

import nl.tudelft.wdm.group1.users.ResourceNotFoundException;
import nl.tudelft.wdm.group1.users.User;
import nl.tudelft.wdm.group1.users.UserRepository;
import nl.tudelft.wdm.group1.users.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final Producer producer;
    private final UserRepository userRepository;

    @Autowired
    UserController(Producer producer, UserRepository userRepository) {
        this.producer = producer;
        this.userRepository = userRepository;
    }

    @PostMapping
    public User addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("street") String street,
            @RequestParam("zip") String zip,
            @RequestParam("city") String city
    ) {
        User user = new User(firstName, lastName, street, zip, city);

        producer.send(user);

        return user;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return userRepository.find(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        userRepository.remove(id);
    }
}
