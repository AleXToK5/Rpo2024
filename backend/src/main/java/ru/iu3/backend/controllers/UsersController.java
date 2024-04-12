package ru.iu3.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class UsersController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/users")
    public List getAllartists() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createArtist(@RequestBody User User)
            throws Exception {
        try {
            User nc = userRepository.save(User);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.name_UNIQUE")) {
                error = "county already exists";
            } else {
                error = "undefined error";
            }
            Map<String, String>
                    map = new HashMap<>();
            map.put("error", error);
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateArtist(@PathVariable(value = "id") Long userId,
                                             @RequestBody User userDetails) {
        User user;
        Optional<User>
                cc = userRepository.findById(userId);
        if (cc.isPresent()) {
            user = cc.get();
            user.login = userDetails.login;
            user.email = userDetails.email;
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long userId) {
        Optional<User>
                User = userRepository.findById(userId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (User.isPresent()) {
            userRepository.delete(User.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                             @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum>
                        mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                                @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : u.museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }
}