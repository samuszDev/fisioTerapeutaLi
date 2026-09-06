package com.usta.fisioterapiali.security;


import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService  implements UserDetailsService {

    @Autowired
    private
    UserDao userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        UserEntity usuario = userDao.findByEmail(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("The user doesn't exist");
        }      System.out.println("Checking role...: " + usuario.getRol());
        return new User(usuario.getEmail(), usuario.getClave(), mapearAutoridadesRol(usuario.getRol())     );
    }
    private Collection<? extends GrantedAuthority> mapearAutoridadesRol(TipoUsuario rol)
    {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
}
