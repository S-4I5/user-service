package xyz.s4i5.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.s4i5.userservice.model.entity.role.Role;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
