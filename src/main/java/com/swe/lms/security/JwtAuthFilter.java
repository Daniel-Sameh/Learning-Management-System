package com.swe.lms.security;

import com.swe.lms.userManagement.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        token=authHeader.substring(7);//starts after the word Bearer (jwt token)
        username= jwtService.extractUsername(token);//extract username from jwt token

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails= userService.userDetailsService().loadUserByUsername(username);
            if(jwtService.isTokenValid(token,userDetails)){

//                String role = jwtService.extractRole(token);
//                if (request.getRequestURI().contains("/admin/") && !"ADMIN".equals(role)) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    response.getWriter().write("Access Denied: Insufficient permissions");
//                    return;
//                }
                String role = jwtService.extractRole(token);
                String prefixedRole = "ROLE_" + role;

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority(prefixedRole)));
                authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }

        }
        filterChain.doFilter(request,response);
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            username = jwtService.extractUsername(token);
//        }
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            if (jwtService.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/signup") || path.equals("/api/signin");
    }
}
