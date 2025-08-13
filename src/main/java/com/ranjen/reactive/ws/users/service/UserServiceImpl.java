package com.ranjen.reactive.ws.users.service;

import com.ranjen.reactive.ws.users.data.UserEntity;
import com.ranjen.reactive.ws.users.data.UserRepository;
import com.ranjen.reactive.ws.users.presentation.CreateUserRequest;
import com.ranjen.reactive.ws.users.presentation.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {
        //readable way for below
        //mapNotNull is used to filter out null values
//        return createUserRequestMono
//                .mapNotNull(createUserRequest ->  convertToEntity(createUserRequest))
//                .flatMap(userEntity ->  userRepository.save(userEntity))
//                .mapNotNull(userEntity-> convertToRest(userEntity));

//        return createUserRequestMono
//                .mapNotNull(this::convertToEntity)
//                .flatMap(userRepository::save)
//                .mapNotNull(this::convertToRest);

        //after convertToEntity changes to Mono<UserEntity> , we can use flatMap instead of mapNotNull
        return createUserRequestMono
                .flatMap(this::convertToEntity)
                .flatMap(userRepository::save)
                .mapNotNull(this::convertToRest);
    }

    @Override
    public Mono<UserRest> getUserById(UUID id) {
        return userRepository
                .findById(id)
                .mapNotNull(userEntity -> convertToRest(userEntity));
    }

    @Override
    public Flux<UserRest> findAll(int page, int limit) {
        if(page>0) page = page -1;
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findAllBy(pageable)
                .map(userEntity->convertToRest(userEntity));
    }

//    private UserEntity convertToEntity(CreateUserRequest createUserRequest) {
//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(createUserRequest, userEntity);
//        return userEntity;
//    }

    private Mono<UserEntity> convertToEntity(CreateUserRequest createUserRequest) {
        // Using Mono.fromCallable to create a UserEntity in a non-blocking way , setting up task that will run it only when needed
        // password encoding is cpu intensive operation, by combining fromCallable and subscribeOn , we use separate thread from the pool
        return Mono.fromCallable(() -> {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(createUserRequest, userEntity);
            userEntity.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            return userEntity;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private UserRest convertToRest(UserEntity userEntity) {
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userEntity, userRest);
        return userRest;
    }
}

