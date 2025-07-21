package com.ranjen.reactive.ws.users.presentation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RestController
@RequestMapping("/users") //   http://localhost:8080/users
public class UserController {

//    @PostMapping
//    public Mono<String> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
//        return Mono.just("OK");
//    }

    @PostMapping
    public Mono<UserRest> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
        return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail())
        );
    }
}
