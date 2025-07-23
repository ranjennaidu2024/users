package com.ranjen.reactive.ws.users.presentation;

import com.ranjen.reactive.ws.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/users") //   http://localhost:8080/users
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
//    Mondo: Returning a single String value in HTTP Response Body
//    @PostMapping
//    public Mono<String> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
//        return Mono.just("OK");
//    }


//    Mono: Returning a User object in HTTP Response body
//    @PostMapping
//    public Mono<UserRest> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
//        return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
//                request.getFirstName(),
//                request.getLastName(),
//                request.getEmail())
//        );
//    }

    //  ResponseEntity: Returning custom HTTP status code
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
//        return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
//                request.getFirstName(),
//                request.getLastName(),
//                request.getEmail())
//        ).map( userRest -> ResponseEntity
//                .status(HttpStatus.CREATED)
//                .location(URI.create("/users/" + userRest.getId()))
//                .body(userRest) );
//    }

    // update this method so that create user method invoke createUser method from service layer
    @PostMapping
    public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
        return userService.createUser(createUserRequest)
                .map(userRest -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(URI.create("/users/" + userRest.getId()))
                        .body(userRest));
    }

// update this method so that get user method invoke getUser method from service layer and get from database
//    @GetMapping("/{userId}")
//    public Mono<UserRest> getUser(@PathVariable("userId") UUID userId) {
//        return Mono.just(new UserRest(
//                userId,
//                "Ranjen2",
//                "Naidu2",
//                "test@test.com"
//        ));
//    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserRest>> getUser(@PathVariable("userId") UUID userId) {
        return userService.getUserById(userId)
                .map(userRest -> ResponseEntity.status(HttpStatus.OK).body(userRest))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

//    @GetMapping
//    public Flux<UserRest> getUsers() {
//        return Flux.just(
//                new UserRest(UUID.randomUUID(), "Sergey", "Kargopolov", "test@test.com"),
//                new UserRest(UUID.randomUUID(), "Alice", "Smith", "alice@test.com"),
//                new UserRest(UUID.randomUUID(), "Bob", "Johnson", "bob@test.com")
//        );
//    }

    //  @RequestParam: Returning a list of users with offset and limit
    @GetMapping
    public Flux<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page,
                                   @RequestParam(value="limit", defaultValue = "50") int limit) {
//        return Flux.just(
//                new UserRest(UUID.randomUUID(), "Sergey", "Kargopolov", "test@test.com"),
//                new UserRest(UUID.randomUUID(), "Alice", "Smith", "alice@test.com"),
//                new UserRest(UUID.randomUUID(), "Bob", "Johnson", "bob@test.com")
//        );
        return userService.findAll(page,limit);
    }
}
