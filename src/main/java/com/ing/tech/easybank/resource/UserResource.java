package com.ing.tech.EasyBank.resource;

import com.ing.tech.EasyBank.dto.*;
import com.ing.tech.EasyBank.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserResource {
    private final UserService userService;

    @PostMapping // make another entity for response, where we don;t show the password
    public ResponseEntity<CreateNewUserDtoOutput> createNewUser(@Valid @RequestBody CreateNewUserDto createNewUserDto) {
        return ResponseEntity.ok(userService.createNewUser(createNewUserDto));
    }


    @PostMapping(value = "/sessions")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody @Valid UserDto request
    ) {
        String jwt = userService.authenticate(request.getUsername(), request.getPassword());

        return ResponseEntity.ok().body(new AuthResponseDto(jwt));
    }







}
















