package com.example.auto_ria.filters;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.auto_ria.dto.responces.ApiError;
import com.example.auto_ria.exceptions.user.UserNotFoundException;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.auth.AuthenticationService;
import com.example.auto_ria.services.auth.JwtService;
import com.example.auto_ria.services.auth.UserDetailsServiceImpl;
import com.example.auto_ria.services.user.UsersServiceSQL;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UsersServiceSQL usersService;
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper; // Injected ObjectMapper with JavaTimeModule

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authorizationHeader.substring(7);
            String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (userDetails == null) {
                    sendJsonErrorResponse(response, HttpStatus.UNAUTHORIZED, "User authentication failed", "Unauthorized");
                    return;
                }

                var userSQL = usersService.getByEmail(userEmail)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (jwtService.isTokenValid(jwt, userDetails) && isInDbAndActivated(userSQL, jwt)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    sendJsonErrorResponse(response, HttpStatus.UNAUTHORIZED, "Access token is invalid or malformed", "Token invalid");
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            sendJsonErrorResponse(response, HttpStatus.UNAUTHORIZED, "Access token is expired", "Token expired");
        } catch (MalformedJwtException e) {
            sendJsonErrorResponse(response, HttpStatus.UNAUTHORIZED, "Access token is invalid or malformed", "Token invalid");
        } catch (Exception e) {
            sendJsonErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred, try later", "Server error");
        }
    }

    private boolean isInDbAndActivated(UserSQL userSQL, String jwt) {
        return Boolean.TRUE.equals(userSQL.isActivated()) && authenticationService.findByAccessToken(jwt) != null;
    }

    private void sendJsonErrorResponse(HttpServletResponse response, HttpStatus status, String message, String title) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiError apiError = new ApiError(status.value(), title, message);
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
