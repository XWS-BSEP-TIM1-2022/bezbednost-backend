package xwsbsep.bezbednostbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(value = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(String username, String password){
        User logedUser = userService.loggedIn(username, password);
        if(logedUser != null){
            return new ResponseEntity<>(logedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers(){
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }
}
