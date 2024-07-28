package xyz.s4i5.userservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import xyz.s4i5.userservice.model.entity.user.User;

public interface UserRepository extends MongoRepository<User, String> {
}
