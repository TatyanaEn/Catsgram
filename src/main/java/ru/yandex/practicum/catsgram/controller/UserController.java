package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        for (User item : users.values()) {
            if (item.getEmail().equals(user.getEmail()))
                throw new DuplicatedDataException("Этот имейл уже используется");

        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            for (User item : users.values()) {
                if (item.getEmail().equals(newUser.getEmail()))
                    throw new DuplicatedDataException("Этот имейл уже используется");

            }
            User oldUser = users.get(newUser.getId());
            if (!(newUser.getEmail() == null || newUser.getEmail().isBlank()))
                oldUser.setEmail(newUser.getEmail());
            if (!(newUser.getUsername() == null || newUser.getUsername().isBlank()))
                oldUser.setUsername(newUser.getUsername());
            if (!(newUser.getPassword() == null || newUser.getPassword().isBlank()))
                oldUser.setPassword(newUser.getPassword());
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

}
