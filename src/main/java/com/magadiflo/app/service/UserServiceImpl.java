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
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    private final IRolRepository rolRepository;

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getUser(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
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
        User user = this.userRepository.findByUsername(username);
        Role role = this.rolRepository.findByName(roleName);

        user.getRoles().add(role);
    }

}
