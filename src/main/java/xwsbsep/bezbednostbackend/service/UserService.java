package xwsbsep.bezbednostbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

@Service
public class UserService {
    private UserRepository userRepository;
    private MessageDigest sha256;

    @Autowired
    public UserService(UserRepository userRepository) throws NoSuchAlgorithmException {
        this.userRepository = userRepository;

        this.sha256 = MessageDigest.getInstance("SHA-256");
    }

    public User loggedIn(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user.getPassword().equals(hash(password))){
            return user;
        }
        return null;
    }

    public Collection<User> getAll(){
        return userRepository.findAll();
    }

    private String hash(String data){
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] dataHash = sha256.digest(data.getBytes());
            return new String(dataHash, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
