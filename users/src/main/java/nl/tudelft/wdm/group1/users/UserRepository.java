package nl.tudelft.wdm.group1.users;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {
    private Map<UUID, User> users = new HashMap<>();

    public User add(User user) {
        users.putIfAbsent(user.getId(), user);

        return user;
    }

    public User addOrReplace(User user) {
        users.put(user.getId(), user);

        return user;
    }

    public User find(UUID id) throws ResourceNotFoundException {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("User with ID " + id + " cannot be found.");
        }
        return users.get(id);
    }

    public void remove(UUID id) throws ResourceNotFoundException {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("User with ID " + id + " cannot be found.");
        }
        users.remove(id);
    }
}
