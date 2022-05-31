package com.magadiflo.app.security;

import com.magadiflo.app.filter.CustomAuthenticationFilter;
import com.magadiflo.app.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @RequiredArgsConstructor, Creará un constructor para nosotros sobre la marcha y luego inyectará
 * a este tipo UserDetailsService dentro del constructor, ya que es un atributo de la clase.
 * Esta es la forma en la que hacemos nuestra inyección de dependencia.
 * El atributo bCryptPasswordEncoder, también será inyectado vía constructor (similar al userDetailsService)
 * ya que en la clase principal del proyecto se definió el @Bean
 * NOTA: Recordar que otra forma es haciendo uso de la anotación @Autowired
 */
@Configuration //Porque estamos en una clase de configuración
@EnableWebSecurity //Habilitamos la seguridad web
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //UserDetailsService, lo proporciona Spring Security
    //Si revisamos nuestra propia clase de servicio creada UserServiceImpl
    //Vemos que hemos implementado la interfaz UserDetailsService quien nos obliga
    //a implementar su método loadUserByUsername(...). Por lo tanto, nuestra clase
    //de servicio UserServiceImpl, al tener la anotación @Service, se generará un
    //objeto en el contenedor de spring, y en este atributo privado declarado
    //aquí abajo (userDetailsService) se le hará la inyección de ese objeto. Tal como hemos venido
    //trabajando normalmente, es decir, se genera una clase que implementa una interfaz "X"
    //y esa clase es anotada con @Service, @Component, etc.. para hacer la inyección de dependencia
    //podemos usar la interfaz "X" implementada y con el @Autowired inyectarla.
    //En este caso concreto, no usamos el @Autowired, sino la inyección por constructor
    //el cual nos es facilitada por el @RequiredArgsConstructor de lombok
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //Configuración del manejo de usuarios.
    // Hay muchas maneras de decirle a Spring cómo buscar a los usuarios.
    // 1° Está en la memoria
    // 2° Usando JDBC
    // 3° JPA con el UserDetailsService
    // etc...
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
    }

    //Configuración de la seguridad Global del sistema
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Creando nuestra propio path de login que por defecto en Spring es /login
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(this.authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");


        http.csrf().disable(); //Deshabilitamos la falsificación de solicitudes entre sitios porque no estamos trabajando con formularios
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//No estamos trabajando con sesiones que es como se trabaja cuando en el servidor se renderizan las vistas
        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().anyRequest().authenticated(); //Autorice las solicitudes y que esté autenticado
        //Agregamos un filtro de autenticación para poder verificar al usuario cada vez que intenta iniciar sesión
        http.addFilter(customAuthenticationFilter);

        //Agregamos un filtro de autorización. Debemos asegurarnos que este filtro esté antes que los
        //demás filtros porque debemos interceptar todas las solicitudes antes que cualquier otro.
        //En el segundo parámetro se usa la clase UsernamePasswordAuthenticationFilter.class, ya que según entiendo,
        //nuestro filtro (CustomAuthorizationFilter) debe estar antes de cualquier otro filtro, y esos otros filtros
        //hereden de UsernamePasswordAuthenticationFilter, tal como lo hace el CustomAuthenticationFilter
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
/**
 * Para decirle a Spring lo que estamos tratando de hacer,
 * necesitamos sobreescribir ciertos métodos de la seguridad web en el adaptador,
 * que es la clase principal de seguridad, es por eso que necesitamos
 * heredar de la clase WebSecurityConfigurerAdapter.
 * Luego le diremos a Spring, cómo queremos administrar los usuarios,
 * la seguridad y la aplicación.
 */