package com.magadiflo.app.service;

import com.magadiflo.app.domain.Role;
import com.magadiflo.app.domain.User;
import com.magadiflo.app.repository.IRolRepository;
import com.magadiflo.app.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @RequiredArgsConstructor, Lombok creará un constructor cuyos argumentos serán
 * pasados a todos los atributos declarados (userRepository, rolRepository) de esa manera
 * se hará la inyección de dependencia.
 * NOTA: Recordar que en otros tutoriales, se usa el @Autowired
 */
@Slf4j //Proporciona la variable estática log
@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    private final IRolRepository rolRepository;

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return this.userRepository.findAll();
    }

    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return this.userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        return this.userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return this.rolRepository.save(role);
    }

    /**
     * Nota: Como la clase está anotada con @Transactional, una vez
     * finalizada el flujo del método addRoleToUser, guardará en automático
     * el rol asignado al usuario. Es decir, no se tiene que llamar
     * al método save, para que guarde porque ya lo hace en automático.
     */
    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = this.userRepository.findByUsername(username);
        Role role = this.rolRepository.findByName(roleName);

        user.getRoles().add(role);
    }

}
