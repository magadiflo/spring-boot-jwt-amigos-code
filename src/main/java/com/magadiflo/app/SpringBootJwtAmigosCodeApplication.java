package com.magadiflo.app;

import com.magadiflo.app.domain.Role;
import com.magadiflo.app.domain.User;
import com.magadiflo.app.service.IUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class SpringBootJwtAmigosCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJwtAmigosCodeApplication.class, args);
    }

    /**
     * Estamos inyectando el servicio (IUserService) dentro de esta línea de comandos.
     * Todo el código que esté dentro de las llaves args -> {},
     * será ejecutado después de que la aplicación se haya iniciado.
     * Entonces podremos usar el servicio inyectado para realizar
     * las operaciones que se muestran. Es decir, cada vez que se inicia la aplicación
     * ejecutaremos esas consultas
     * 1° Se crean los los roles
     * 2° Se crean los usuarios
     * 3° Se le asignan roles a los usuarios
     */
    @Bean
    CommandLineRunner run(IUserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new User(null, "Tinkler", "tinkler", "12345", new ArrayList<>()));
            userService.saveUser(new User(null, "Raúl", "raul", "12345", new ArrayList<>()));
            userService.saveUser(new User(null, "Abraham", "abraham", "12345", new ArrayList<>()));
            userService.saveUser(new User(null, "Alicia", "alicia", "12345", new ArrayList<>()));
            userService.saveUser(new User(null, "Gabriel", "gabriel", "12345", new ArrayList<>()));
            userService.saveUser(new User(null, "Martín", "magadiflo", "12345", new ArrayList<>()));

            userService.addRoleToUser("tinkler", "ROLE_USER");
            userService.addRoleToUser("raul", "ROLE_USER");
            userService.addRoleToUser("abraham", "ROLE_MANAGER");
            userService.addRoleToUser("alicia", "ROLE_ADMIN");
            userService.addRoleToUser("gabriel", "ROLE_ADMIN");
            userService.addRoleToUser("magadiflo", "ROLE_USER");
            userService.addRoleToUser("magadiflo", "ROLE_ADMIN");
            userService.addRoleToUser("magadiflo", "ROLE_SUPER_ADMIN");
        };
    }

}
