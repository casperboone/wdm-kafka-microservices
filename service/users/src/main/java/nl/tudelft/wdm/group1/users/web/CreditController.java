package nl.tudelft.wdm.group1.users.web;

import nl.tudelft.wdm.group1.common.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.InsufficientCreditException;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.users.UserRepository;
import nl.tudelft.wdm.group1.users.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return userRepository.findOrElseThrow(id);
    }

    @PostMapping("/subtract/{amount}")
    public User subtractCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, InsufficientCreditException, CreditChangeInvalidException {
        User user = userRepository.findOrElseThrow(id);
        user.subtractCredit(amount);
        producer.emitCreditSubtracted(user);
        return user;
    }

    @PostMapping("/add/{amount}")
    public User addCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, CreditChangeInvalidException {
        User user = userRepository.findOrElseThrow(id);
        user.addCredit(amount);
        producer.emitCreditAdded(user);
        return user;
    }
}
