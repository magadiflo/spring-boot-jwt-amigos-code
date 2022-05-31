package com.magadiflo.app.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    /**
     * Con este método determinaremos si el usuario tiene o no acceso a la aplicación.
     * Este filtro interceptará cada solicitud que ingrese a la aplicación.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //Si el path es el del inicio de sesión (login), lo dejamos pasar, no necesitamos hacer nada, ya que
        //esto nos da a entender de que el usuario está tratando de iniciar sesión
        if (request.getServletPath().equals("/api/login")) {
            filterChain.doFilter(request, response);//se pasa la solicitud al siguiente filtro en la cadena de filtros
        } else { //Verificamos si tiene una autorización y luego configurar al usuario como el usuario que inició sesión
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
                try {
                    log.info("Verificando el token!!!");
                    String token = authorizationHeader.substring(BEARER.length());//Obtenemos el token eliminando la palabra Bearer + el espacio
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);

                    //Si el token es válido obtenemos los siguiente valores
                    String username = decodedJWT.getSubject();
                    String roles[] = decodedJWT.getClaim("roles").asArray(String.class);

                    //Convertiremos el arreglo de roles[] en una lista que extienda de GrantedAuthority
                    //Por regla, Spring Security espera algo que extienda de GrantedAuthority, ya que si revisamos
                    //la clase User de Spring Security veremos un atributo authorities de tipo Collection que
                    //espera una lista que extienda de GrantedAuthority. En nuestro caso, SimpleGrantedAuthority
                    //sí hereda de GrantedAuthority
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                    //No necesitamos la contraseña del usuario (tampoco la tenemos)
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    /**
                     * Así es como se le dice a Spring Security:
                     * Este es el usuario, estas son sus funciones y es lo que pueden hacer en la aplicación
                     * Entonces Spring observará al usuario, sus roles y determinará a qué recursos puede acceder
                     */
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    //Llamamos a la cadena de filtro y dejamos que el request continúe su curso
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error loggin in: {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());

                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    //Esto devolverá el error_message dentro del cuerpo de la respuesta en formato JSON
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                log.info("Dejando que la solicitud continúe...!!!");
                filterChain.doFilter(request, response);
            }
        }

    }

}
