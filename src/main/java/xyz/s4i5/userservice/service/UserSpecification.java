package xyz.s4i5.userservice.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import xyz.s4i5.userservice.model.entity.role.Role;
import xyz.s4i5.userservice.model.entity.role.RoleName;
import xyz.s4i5.userservice.model.entity.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String LOGIN_FIELD_NAME = "login";
    private static final String FULL_FIELD_NAME = "fullName";
    private static final String ROLES_FIELD_NAME = "roles";
    private static final String ROLE_NAME_FIELD_NAME = "roleName";

    public static Specification<User> getSpecificationForEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(EMAIL_FIELD_NAME), "%" + email + "%");
    }

    public static Specification<User> getSpecificationForLogin(String login) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(LOGIN_FIELD_NAME), "%" + login + "%");
    }

    public static Specification<User> getSpecificationForFullName(String fullName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(FULL_FIELD_NAME), "%" + fullName + "%");
    }

    public static Specification<User> getSpecificationForRoles(List<RoleName> roles) {
        return (root, query, criteriaBuilder) -> {
            Predicate finalPredicate = criteriaBuilder.conjunction();

            for (var role : roles) {
                var subQuery = query.subquery(User.class);
                var subRoot = subQuery.from(User.class);

                Join<User, Role> join = subRoot.join(ROLES_FIELD_NAME, JoinType.LEFT);
                subQuery.select(subRoot.get("id"))
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                                criteriaBuilder.equal(join.get(ROLE_NAME_FIELD_NAME), role)
                        ));

                finalPredicate = criteriaBuilder.and(finalPredicate,
                        criteriaBuilder.exists(subQuery));
            }

            return finalPredicate;
        };
    }
}
