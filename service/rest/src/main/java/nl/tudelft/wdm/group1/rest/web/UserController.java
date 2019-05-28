package nl.tudelft.wdm.group1.rest.web;

import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.rest.events.KafkaInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private KafkaInteraction<User> kafka;

    @Autowired
    UserController(KafkaInteraction<User> kafka) {
        this.kafka = kafka;
    }

    @PostMapping
    public CompletableFuture<User> addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("street") String street,
            @RequestParam("zip") String zip,
            @RequestParam("city") String city
    ) {
        return kafka.performAction(new UserCreatePayload(firstName, lastName, street, zip, city));
    }

    @GetMapping("/{id}")
    public CompletableFuture<User> getUser(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new UserGetPayload(id));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<User> removeUser(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new UserDeletePayload(id));
    }

    @PostMapping("/{id}/credit/subtract/{amount}")
    public CompletableFuture<User> subtractCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) {
        return kafka.performAction(new UserCreditSubtractPayload(id, amount));
    }

    @PostMapping("/{id}/credit/add/{amount}")
    public CompletableFuture<User> addCredit(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) {
        return kafka.performAction(new UserCreditAddPayload(id, amount));
    }
}