package com.example.flight_booking_backend.security;

import com.example.flight_booking_backend.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        // Skip JWT filter entirely for auth endpoints and OPTIONS preflight
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/auth")
                || method.equals("OPTIONS");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.extractClaims(jwt).getSubject();
            } catch (Exception e) {
                // Token is invalid or expired — do NOT block, just continue unauthenticated
                // Spring Security will enforce access control based on the route rules
                chain.doFilter(request, response);
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (Exception e) {
                // User not found or token invalid — continue without setting auth
            }
        }

        chain.doFilter(request, response);
    }
}