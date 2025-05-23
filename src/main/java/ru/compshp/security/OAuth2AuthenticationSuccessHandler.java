package ru.compshp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isEmpty()) {
            // Создаем нового пользователя
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setActive(true);
            userRepository.save(user);
        } else {
            user = userOpt.get();
        }

        // Генерируем JWT токен
        String token = tokenProvider.generateToken(authentication);

        // Добавляем токен в заголовок ответа
        response.setHeader("Authorization", "Bearer " + token);
        
        // Перенаправляем на главную страницу
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
} 