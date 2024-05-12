package ru.iu3.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import javax.validation.Valid;
import ru.iu3.backend.Tools.DataValidationException;
import ru.iu3.backend.Tools.Utils;


@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class UsersController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;

    /**
     * Метод, который возвращает список юзеров (не художников), которые есть в данной БД
     * @return - список пользователей в виде JSON
     */
    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userRepository.findAll(
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") Long userID)
            throws DataValidationException {
        User user = userRepository.findById(userID).
                orElseThrow(() -> new DataValidationException("User not founding"));

        return ResponseEntity.ok(user);
    }

    /**
     * Добавляем пользователя
     * @param User - JSON, который приходит из postman
     * @return - заголовок (JSON): 200, если ОК, в противном случае, будет ошибка с каким-либо описанием
     * @throws Exception - обязательное требование
     */
    @PostMapping("/users")
    public ResponseEntity<Object> createArtist(@RequestBody User User)
            throws DataValidationException {
        try {
            User nc = userRepository.save(User);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот пользователь есть в БД");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }

        }
    }

    /**
     * NEW!!! Добавляем музеи для конкретного пользователя. Но добавление осуществляется из-под пользователя
     * @param userID - ID пользователя, к которому необходимо обратиться
     * @param museums - список музеев для данного пользователя
     * @return - Поле cnt возвратит просто, где будет отображено 0, если не добавлено, 1 если добавлено
     */

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userID,
                                             @Validated @RequestBody Set<Museum> museums) {
        // Извлекаем пользователя по конкретному ID-шнику
        Optional<User> uu = userRepository.findById(userID);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            // Если музеев несколько (а такое может быть вполне, то тогда добавляем их поочерёдно)
            for(Museum m: museums) {
                // Если есть музей, то мы, конечно, добавим его. Защита от дурака
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    ++cnt;
                }
            }
            // Сохраняем
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("added", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }
    /**
     * NEW!!! Метод, который удаляет музей из-под класса пользователя
     * @param userId - ID по которому собственно должен быть найден
     * @param museums - Список удаляемых музеев
     * @return - ответ, который содержит количество удалённых музеев
     */
    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                                @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m: museums) {
                u.removeMuseum(m);
                ++cnt;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляем пользователя
     * @param userId - ID пользователя
     * @param userDetails - подробные сведения по пользователю
     * @return - хедер, где будет содержаться ответ по данному пользователю
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUsers(@PathVariable(value = "id") Long userId,
                                            @RequestBody User userDetails) throws DataValidationException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException("Пользователь с таким индексом не найден"));
            // Заполняем пользовательские данные
            user.login = userDetails.login;
            user.email = userDetails.email;

            String np = userDetails.np;
            System.out.println("Новый пароль (если вдруг забудешь): " + np);
            if (np != null && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }

            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот пользователь уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    @PostMapping("/deleteusers")
    public ResponseEntity deleteUsers(@Validated @RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Удаляем пользователя
     * @param userId - ID пользователя
     * @return - удалено/не удалено
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUsers(@PathVariable(value = "id") Long userId) {
        Optional<User> users = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();

        // Возвратит true, если объект существует (не пустой)
        if (users.isPresent()) {
            userRepository.delete(users.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}