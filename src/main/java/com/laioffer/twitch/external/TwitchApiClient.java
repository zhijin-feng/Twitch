package com.laioffer.twitch.external;

import com.laioffer.twitch.external.model.ClipResponse;
import com.laioffer.twitch.external.model.GameResponse;
import com.laioffer.twitch.external.model.StreamResponse;
import com.laioffer.twitch.external.model.VideoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//加入feignclient, 注意不要加分号！
@FeignClient(name = "twitch-api")
public interface TwitchApiClient {
//    getmapping括号里面是url里面不一样的部分；post的话就是postMapping;
    @GetMapping("/games")
//    做好name的对应；
    GameResponse getGames(@RequestParam("name") String name);

    @GetMapping("/games/top")
    GameResponse getTopGames();


    @GetMapping("/videos/")
    VideoResponse getVideos(@RequestParam("game_id") String gameId, @RequestParam("first") int first);


    @GetMapping("/clips/")
    ClipResponse getClips(@RequestParam("game_id") String gameId, @RequestParam("first") int first);


    @GetMapping("/streams/")
    StreamResponse getStreams(@RequestParam("game_id") List<String> gameIds, @RequestParam("first") int first);








}
