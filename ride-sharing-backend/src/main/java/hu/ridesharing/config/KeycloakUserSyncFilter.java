package hu.ridesharing.config;

import hu.ridesharing.entity.User;
import hu.ridesharing.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final UserService userService;

    public KeycloakUserSyncFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if the request has a valid jwt token
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            // fetch keycloak claims
            String username = jwt.getClaimAsString("preferred_username");
            String fullName = jwt.getClaimAsString("name");
            String email = jwt.getClaimAsString("email");

            if (username != null) {
                User user = new User();
                user.setUsername(username);
                user.setFullName(fullName != null ? fullName : username);
                user.setEmailAddress(email);

                // save if not exists
                userService.saveUser(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}