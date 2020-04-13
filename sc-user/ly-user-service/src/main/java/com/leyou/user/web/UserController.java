package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean bool = this.userService.checkData(data,type);
        if(bool == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone){
        this.userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code")String code){
        this.userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/query")
    public ResponseEntity<User> queryUserByUsernameAndPassword(@RequestParam("username")String username,@RequestParam("password") String password){
        User user = this.userService.queryUserByUsernameAndPassword(username,password);
        if(user == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }

}



