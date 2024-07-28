package xyz.s4i5.userservice.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import xyz.s4i5.userservice.model.entity.user.Role;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCriteria {
    private static final String EMAIL_FIELD = "email";
    private static final String LOGIN_NUMBER_FIELD = "login";
    private static final String FULL_NAME_FIELD = "fullName";
    private static final String ROLES_FIELD = "roles";

    public static Criteria getCriteriaForEmail(String email) {
        return Criteria.where(EMAIL_FIELD).regex(email);
    }

    public static Criteria getCriteriaForLogin(String login) {
        return Criteria.where(LOGIN_NUMBER_FIELD).regex(login);
    }

    public static Criteria getCriteriaForFullName(String fullName) {
        return Criteria.where(FULL_NAME_FIELD).regex(fullName);
    }

    public static Criteria getCriteriaForRoles(List<Role> roles) {
        return Criteria.where(ROLES_FIELD).all(roles);
    }
}
