package nl.tudelft.wdm.group1.users.web;

import nl.tudelft.wdm.group1.users.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.users.InsufficientCreditException;
import nl.tudelft.wdm.group1.users.ResourceNotFoundException;
import nl.tudelft.wdm.group1.users.User;
import nl.tudelft.wdm.group1.users.UserRepository;
import nl.tudelft.wdm.group1.users.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/users/{id}/credit")
public class CreditController {
    private final UserRepository userRepository;
    private final Producer producer;

    @Autowired
    CreditController(UserRepository userRepository, Producer producer) {
        this.userRepository = userRepository;
        this.producer = producer;
    }

    @GetMapping
    public User getCredit(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return userRepository.find(id);
    }

    @PostMapping("/subtract/{amount}")
    public User subtractCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, InsufficientCreditException, CreditChangeInvalidException {
        User user = userRepository.find(id);
        user.subtractCredit(amount);
        producer.send(user);
        return user;
    }

    @PostMapping("/add/{amount}")
    public User addCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, CreditChangeInvalidException {
        User user = userRepository.find(id);
        user.addCredit(amount);
        producer.send(user);
        return user;
    }
}
