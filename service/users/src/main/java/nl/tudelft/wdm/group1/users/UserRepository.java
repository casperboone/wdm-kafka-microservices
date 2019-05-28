package nl.tudelft.wdm.group1.users;

import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    default User findOrElseThrow(UUID id) throws ResourceNotFoundException {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " cannot be found."));
    }
}
