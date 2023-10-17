package com.laioffer.twitch.user;


import com.laioffer.twitch.model.RegisterBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {


    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.OK) //这行代码可以取消掉; 因为默认是返回200 OK；有10中成功的;
    //201 ok: 创建resource成功；202 ok: submit成功; 203 ok: no content; 204 ok: service为了让前端省略parse result;节省带宽;
    //如果代码出错，就会return global exception handler;
    public void register(@RequestBody RegisterBody body) {
        userService.register(body.username(), body.password(), body.firstName(), body.lastName());
    }
}

