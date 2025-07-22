package com.ranjen.reactive.ws.users.service;
import com.ranjen.reactive.ws.users.presentation.CreateUserRequest;
import com.ranjen.reactive.ws.users.presentation.UserRest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono);
    Mono<UserRest> getUserById(UUID id);
}

