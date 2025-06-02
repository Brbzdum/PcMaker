package ru.bek.compshp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bek.compshp.model.User;
import ru.bek.compshp.repository.UserRepository;
import ru.bek.compshp.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        
        // Проверяем, активирован ли пользователь
        if (!user.getActive()) {
            throw new UsernameNotFoundException("Пользователь не активирован. Проверьте вашу почту для подтверждения регистрации.");
        }
        
        return new CustomUserDetails(user);
    }

} 