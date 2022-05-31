package com.magadiflo.app.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //Necesitamos traer el administrador de autenticación (AuthenticationManager)
    //porque lo vamos a llamar para autenticar al usuario.
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //Método que se llamará cada vez que el usuario intente autenticarse
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username is: {}", username);
        log.info("Password is: {}", password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return this.authenticationManager.authenticate(authenticationToken);
    }

    /**
     * Se llamará este método cuando la autenticación sea exitosa.
     * Lo que debemos hacer es darle al usuario su Token de acceso y su Refresh Token después
     * de que haya iniciado sesión correctamente.
     * Se usará la librería externa para generar el token: auth0
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal(); //Principio de obtención de autenticación devuelve el usuario que inicio sesión
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); //Algoritmo para firmar el JWT y el Refresh Token
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String access_token = JWT.create()
                .withSubject(user.getUsername()) //Cualquier cadena
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) //Hora actual (milisegundos) + 600000 milisegundos (10minutos * 60 segundos * 1000 milisegundos)
                .withIssuer(request.getRequestURL().toString())//Nombre de la empresa o autor del token, en nuestro caso la url de la aplicación
                .withClaim("roles", roles) //Todos los roles para este usuario específico
                .sign(algorithm); //Firmar el token

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) //Le damos más tiempo que al access_token
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        //response.setHeader("access_token", access_token);
        //response.setHeader("refresh_token", refresh_token);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //Esto devolverá el access_token y refresh_token dentro del cuerpo de la respuesta en formato JSON
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
