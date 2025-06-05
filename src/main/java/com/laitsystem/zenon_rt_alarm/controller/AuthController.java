package com.laitsystem.zenon_rt_alarm.controller;

import com.laitsystem.zenon_rt_alarm.component.UserRepository;
import com.laitsystem.zenon_rt_alarm.config.AES256Util;

import com.laitsystem.zenon_rt_alarm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AES256Util aes256Util;


//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
//        String username = body.get("username");
//        String password = body.get("password");
//
//        try {
//            String encrypted = aes256Util.encrypt(password);
//            User user = new User();
//            user.setUsername(username);
//            user.setEncryptedPassword(encrypted);
//            userRepository.save(user);
//            return ResponseEntity.ok("User registered");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Encryption error");
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        return userRepository.findByUsername(username).map(user -> {
            try {
                String decrypted = aes256Util.decrypt(user.getEncryptedPassword());
                if (password.equals(decrypted)) {
                    return ResponseEntity.ok("Login success");
                } else {
                    return ResponseEntity.status(401).body("Wrong password");
                }
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Decryption error");
            }
        }).orElse(ResponseEntity.status(404).body("User not found"));
    }

}

