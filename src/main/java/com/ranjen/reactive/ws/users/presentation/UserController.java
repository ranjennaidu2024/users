package com.ranjen.reactive.ws.users.presentation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users") //   http://localhost:8080/users
public class UserController {

    @PostMapping
    public void createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
    }
}
