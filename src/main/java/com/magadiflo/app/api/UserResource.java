package com.magadiflo.app.api;

//Esta clase será el recurso o controlador, así que
//también podría haber sido llamado UserController


import com.magadiflo.app.domain.Role;
import com.magadiflo.app.domain.User;
import com.magadiflo.app.service.IUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserResource {

    private final IUserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(this.userService.getUsers());//Código de respuesta 200: ok
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(this.userService.saveUser(user)); //Código de respuesta 201: created
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(this.userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserFrom form) {
        this.userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

}

//Según los otros tutoriales llevados,
//esta clase podría ser un DTO, de tal forma que lo colocaríamos
//en un directorio y no en este archivo
@Data
class RoleToUserFrom {
    private String username;
    private String roleName;
}
