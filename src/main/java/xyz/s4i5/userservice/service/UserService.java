package xyz.s4i5.userservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.s4i5.userservice.encoder.PasswordEncoder;
import xyz.s4i5.userservice.exceptions.UserApiException;
import xyz.s4i5.userservice.mapper.UserMapper;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserDto;
import xyz.s4i5.userservice.model.dto.user.UserSearchDto;
import xyz.s4i5.userservice.model.entity.user.User;
import xyz.s4i5.userservice.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public UserDto createUser(CreateUserDto createUserDto) {
        return userMapper.toUserDto(saveUser(
                        User.builder()
                                .email(createUserDto.getEmail())
                                .login(createUserDto.getLogin())
                                .password(passwordEncoder.encode(createUserDto.getPassword()))
                                .build()
                )
        );
    }

    public Page<UserDto> searchUsers(UserSearchDto searchDto, Pageable pageable) {
        var query = new Query().with(pageable);

        if (!StringUtils.isBlank(searchDto.getEmail())) {
            query.addCriteria(UserCriteria.getCriteriaForEmail(searchDto.getEmail()));
        }

        if (!StringUtils.isBlank(searchDto.getLogin())) {
            query.addCriteria(UserCriteria.getCriteriaForLogin(searchDto.getLogin()));
        }

        if (!StringUtils.isBlank(searchDto.getFullName())) {
            query.addCriteria(UserCriteria.getCriteriaForFullName(searchDto.getFullName()));
        }

        if (!CollectionUtils.isEmpty(searchDto.getRoles())) {
            query.addCriteria(UserCriteria.getCriteriaForRoles(searchDto.getRoles()));
        }

        var result = mongoTemplate.find(query, User.class);

        return new PageImpl<>(result, pageable, result.size())
                .map(userMapper::toUserDto);
    }

    public UserDto getUser(String id) {
        return userRepository.findById(id).map(userMapper::toUserDto)
                .orElseThrow(() -> new UserApiException("api.user.notFound", List.of(id)));
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserApiException("api.user.notFound", List.of(id));
        }

        userRepository.deleteById(id);
    }

    public UserDto updateUser(UpdateUserDto updateUserDto, String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserApiException("api.user.notFound", List.of(id)));

        userMapper.update(user, updateUserDto);

        return userMapper.toUserDto(saveUser(user));
    }

    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new UserApiException("api.user.create.uniqueFieldDuplicate",
                    List.of(String.valueOf(Arrays.stream(e.getCause().getMessage().split(":")).reduce((x, y) -> y))));
        }
    }
}
