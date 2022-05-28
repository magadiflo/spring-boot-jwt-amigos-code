package com.magadiflo.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
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