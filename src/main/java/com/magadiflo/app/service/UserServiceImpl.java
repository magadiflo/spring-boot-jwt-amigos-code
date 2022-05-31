package com.magadiflo.app.service;

import com.magadiflo.app.domain.Role;
import com.magadiflo.app.domain.User;
import com.magadiflo.app.repository.IRolRepository;
import com.magadiflo.app.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
public class UserServiceImpl implements IUserService, UserDetailsService {

    private final IUserRepository userRepository;

    private final IRolRepository rolRepository;

    private final PasswordEncoder passwordEncoder;

    //Método que usa Spring para cargar los usuarios desde la BD o desde donde sea que estén
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);
        if(user == null){
            log.error("User {} not found in the database", username);
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User {} found in the database", username);
        }

        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        //ingresamos el nombre calificado completo para poder diferenciarnos
        //del nuestra propia clase User usada en este servicio
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
