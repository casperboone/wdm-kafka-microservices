package nl.tudelft.wdm.group1.rest.web;

import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.common.payload.UserCreatePayload;
import nl.tudelft.wdm.group1.common.payload.UserDeletePayload;
import nl.tudelft.wdm.group1.common.payload.UserGetPayload;
import nl.tudelft.wdm.group1.rest.events.KafkaInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Throwable ex) {
        return ex.getMessage();
    }
}
