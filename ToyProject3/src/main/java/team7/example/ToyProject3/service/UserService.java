package team7.example.ToyProject3.service;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team7.example.ToyProject3.domain.Role;
import team7.example.ToyProject3.domain.User;
import team7.example.ToyProject3.dto.UserDto;
import team7.example.ToyProject3.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor

public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    public Long joinUser(UserDto userDto){
        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        userRepository.save(userDto.toEntity());
        return userRepository.save(userDto.toEntity()).getId();
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userWrapper = userRepository.findByEmail(email);
        User user = userWrapper.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (("admin@example.com").equals(email)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.USER.getValue()));

        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
