package com.example.backendservice1.services;

import com.example.backendservice1.dto.UserDTO;
import com.example.backendservice1.entities.User;
import com.example.backendservice1.mapper.UserMapper;
import com.example.backendservice1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cerca l'utente nel database tramite il nome utente (cname)
        User user = userRepository.findByCname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        if(user != null){
            logger.info("log in user: {}", user.getCname());
            logger.info("log in password: {}", user.getCpwd());
            // Costruisce e ritorna l'oggetto UserDetails
            return new org.springframework.security.core.userdetails.User(
                    user.getCname(), // Username
                    user.getCpwd(),  // Password (deve essere cifrata)
                    getAuthorities(user) // Ruoli (può essere customizzato)
            );
        }
        return null;
    }

    // Metodo per assegnare ruoli o permessi (per semplicità qui ritorniamo un ruolo utente standard)
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Puoi aggiungere logica qui per gestire ruoli diversi, ma per ora assegniamo un ruolo generico
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }
    public User authenticate(String username, String password) throws Exception {
        User user = userRepository.findByCname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user != null && verifyPassword(password, user.getCpwd())){
            return user;
        }
        return null;
    }

    private boolean verifyPassword(String providedPassword, String storedPassword) throws Exception {
        logger.info("Provided password: {}", providedPassword);
        logger.info("Stored password: {}", storedPassword);

        // La password memorizzata è nel formato PBKDF2HMACSHA1<iterations>.<salt>.<hash>
        String[] parts = storedPassword.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Stored password format is incorrect.");
        }

        int iterations = getNumeroIterazioni(parts[0]); // estrai il numero di iterazioni
        byte[] salt = Base64.getDecoder().decode(parts[1]); // decodifica il salt da Base64
        byte[] storedHash = Base64.getDecoder().decode(parts[2]); // decodifica l'hash memorizzato da Base64

        // Genera il hash della password fornita usando il salt
        PBEKeySpec spec = new PBEKeySpec(providedPassword.toCharArray(), salt, iterations, storedHash.length * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashBytes = factory.generateSecret(spec).getEncoded();

        // Confronta il hash generato con il hash memorizzato
        final boolean hashesEqual = MessageDigest.isEqual(storedHash, hashBytes);
        logger.info("Hashes are equal: {}", hashesEqual);
        return hashesEqual;
    }

    private int getNumeroIterazioni(String input) {
        int iter = 0;
        // Rimuovi "PBKDF2HMACSHA1" dalla stringa
        String withoutPBKDF2HMACSHA1 = input.replace("PBKDF2HMACSHA1", "");

        // Rimuovi l'ultima lettera
        if (withoutPBKDF2HMACSHA1.length() > 0) {
            withoutPBKDF2HMACSHA1 = withoutPBKDF2HMACSHA1.substring(0, withoutPBKDF2HMACSHA1.length() - 1);
        }
        iter = Integer.parseInt(withoutPBKDF2HMACSHA1);
        return iter;
    }


    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase(); // usa maiuscole per la comparazione
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    public void saveUser(UserDTO userDTO) {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
        userRepository.save(user);
    }
}
