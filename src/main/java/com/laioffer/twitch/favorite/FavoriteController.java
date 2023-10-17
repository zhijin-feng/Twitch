package com.laioffer.twitch.favorite;


import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.FavoriteRequestBody;
import com.laioffer.twitch.model.TypeGroupedItemList;
import com.laioffer.twitch.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController //告诉springBoot知道找get,delete
@RequestMapping("/favorite") //Get,Post,Delete共用一个api;
public class FavoriteController {

    //dependency:
    private final FavoriteService favoriteService;

    private final UserService userService;

    // Hard-coded user for temporary use, will be replaced in future
    // private final UserEntity userEntity = new UserEntity(1L, "user0", "Foo", "Bar", "password");
    // 这里的userEntity没有save, 所以必须指定id;

    //Inject一个

    public FavoriteController(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }


    @GetMapping //这里没有路径了，写在14行了。也可以写在这里，不过简便的是可以把公共的写在14行；
    public TypeGroupedItemList getFavoriteItems(@AuthenticationPrincipal User user) {
        //给一个argument user是从security这里import的；UserDetails下面的user;
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        return favoriteService.getGroupedFavoriteItems(userEntity);
    }


    @PostMapping
    public void setFavoriteItem(@AuthenticationPrincipal User user,@RequestBody FavoriteRequestBody body) { //@RequestBody必须要写;
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        try {
            favoriteService.setFavoriteItem(userEntity, body.favorite());
        } catch (DuplicateFavoriteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate entry for favorite record", e);
        }
        //throw new exception: bad request; 因为function return void，要知道出错必须throw exception;
        //或者return -1; 比如go语言没有exception;
        //throw exception最大的好处是极大地减少call chain,直接到最外层catch;
    }


    @DeleteMapping
    public void unsetFavoriteItem(@AuthenticationPrincipal User user,@RequestBody FavoriteRequestBody body) { //FavoriteRequestBody是一个class;
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        favoriteService.unsetFavoriteItem(userEntity, body.favorite().twitchId());
    }
}
