package com.magadiflo.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/*
NOTA: Recordar que en Spring Security hay una Interfaz llamada User,
debemos tener mucho cuidado cuando hagamos las importaciones para
realmente llamar a la clase o interfaz (con el mismo nombre) que
realmente necesitamos. Algunos desarrolladores para no equivocarse nombran
a su clase User como AppUser, de esa forma lo diferencian de la que
Spring ya tiene definido. En nuestro caso tendremos cuidado.
 */

/**
 * Lombok, los creará sobre la marcha
 *
 * @Data, este proporcionará los Getters y Setters
 * @NoArgsConstructor, proporciona un constructor sin argumentos
 * @AllArgsConstructor, proporciona un constructor con todos los argumentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

}
