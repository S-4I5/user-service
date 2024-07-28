package xyz.s4i5.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.s4i5.userservice.model.entity.user.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Page<User> findAll(Specification<User> specification, Pageable pageable);
}
