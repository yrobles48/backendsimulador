package com.prestaclub.prestaclub_backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prestaclub.prestaclub_backend.dto.PublicRoutes;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UserDetailsServiceImpl; 
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null; 
        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher(); // Agrega esto para manejar rutas dinámicas

    boolean isPublic = PublicRoutes.PUBLIC_URLS.stream()
        .anyMatch(publicUrl -> pathMatcher.match(publicUrl, requestURI));
        if (isPublic) { 
            // Si es pública, continúa con la cadena sin aplicar autenticación
            filterChain.doFilter(request, response);
            return; // Salir del método para no aplicar autenticación
        } 

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); 
            try {
                username = this.jwtUtils.extractUsername(jwtToken);
            } catch (ExpiredJwtException expiredJwtException) {
                System.out.println("El token ah expirado");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(requestURI + " : ruta sin token");
            System.out.println("Token invalido, El token no inicia con Bearer");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (this.jwtUtils.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken );     
            }else{
                System.out.println("Token no es valido");
            }
        }
        filterChain.doFilter(request, response);
    }

}
