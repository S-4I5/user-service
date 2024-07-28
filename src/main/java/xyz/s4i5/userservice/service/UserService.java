package xyz.s4i5.userservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(CreateUserDto createUserDto) {
        var user = userMapper.toUser(createUserDto);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().forEach(role -> role.setUser(user));

        return userMapper.toUserDto(userRepository.save(user));
    }

    public Page<UserDto> searchUsers(UserSearchDto searchDto, Pageable pageable) {
        List<Specification<User>> specs = new LinkedList<>();

        if (!StringUtils.isBlank(searchDto.getEmail())) {
            specs.add(UserSpecification.getSpecificationForEmail(searchDto.getEmail()));
        }

        if (!StringUtils.isBlank(searchDto.getLogin())) {
            specs.add(UserSpecification.getSpecificationForLogin(searchDto.getLogin()));
        }

        if (!StringUtils.isBlank(searchDto.getFullName())) {
            specs.add(UserSpecification.getSpecificationForFullName(searchDto.getFullName()));
        }

        if (!CollectionUtils.isEmpty(searchDto.getRoles())) {
            specs.add(UserSpecification.getSpecificationForRoles(searchDto.getRoles()));
        }

        return userRepository.findAll(Specification.allOf(specs), pageable)
                .map(userMapper::toUserDto);
    }

    public UserDto getUser(UUID id) {
        return userRepository.findById(id).map(userMapper::toUserDto)
                .orElseThrow(() -> new UserApiException("api.user.notFound", List.of(id.toString())));
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserApiException("api.user.notFound", List.of(id.toString()));
        }

        userRepository.deleteById(id);
    }

    public UserDto updateUser(UpdateUserDto updateUserDto, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserApiException("api.user.notFound", List.of(id.toString())));

        userMapper.update(user, updateUserDto);

        return userMapper.toUserDto(userRepository.save(user));
    }
}
