package com.laioffer.twitch.external;

import com.laioffer.twitch.external.model.Game;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//告诉springboot是一个controller：
@RestController
//需要用twitch service来找api，game controller depends on TwitchService:

public class GameController {

    private final TwitchService twitchService;
    //成员变量;如果只有这个会标红;因为final没有被初始化;
    //为什么要写final?好处是安全;

    //这个constructor作为参数把twitchService传进来;
    //谁来call constructor? 是springboot在运行的时候创建的;
    public GameController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }


    @GetMapping("/game")
    public List<Game> getGames(@RequestParam(value = "game_name", required = false) String gameName) {
        if (gameName == null) {
            return twitchService.getTopGames();
        } else {
            return twitchService.getGames(gameName);
        }
    }

}
