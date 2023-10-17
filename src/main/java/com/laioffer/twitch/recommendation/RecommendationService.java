package com.laioffer.twitch.recommendation;


import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.external.TwitchService;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.favorite.FavoriteService;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;


@Service
public class RecommendationService {


    private static final int MAX_GAME_SEED = 3;
    private static final int PER_PAGE_ITEM_SIZE = 20;


    private final TwitchService twitchService;
    private final FavoriteService favoriteService;


    public RecommendationService(TwitchService twitchService, FavoriteService favoriteService) {
        this.twitchService = twitchService;
        this.favoriteService = favoriteService;
    }


@Cacheable("recommend_items")//也可以加一个key，但是底下的函数只有一个参数，所以会自动作为key;
    //只有一个public API, recommendItems; private functions是给public用的;
    public TypeGroupedItemList recommendItems(UserEntity userEntity) {
        List<String> gameIds;
        Set<String> exclusions = new HashSet<>();
        if (userEntity == null) {//为什么会有if else的情况？当你想去获取recommendation,但是没有注册的时候，就是TopGameIds;
            gameIds  = twitchService.getTopGameIds();
        } else {
            List<ItemEntity> items = favoriteService.getFavoriteItems(userEntity);
            if (items.isEmpty()) { //什么都没收藏;
                gameIds = twitchService.getTopGameIds();
            } else {
                Set<String> uniqueGameIds = new HashSet<>();//去重;
                for (ItemEntity item : items) {
                    uniqueGameIds.add(item.gameId());
                    exclusions.add(item.twitchId()); //防止推荐一样的视频;
                }
                gameIds = new ArrayList<>(uniqueGameIds);
            }
        }
        //上面的都是figure out每个人的gameId是什么;


        int gameSize = Math.min(gameIds.size(), MAX_GAME_SEED); //当gameId数量超过3个的时候，取3; 防止响应速度过长;
        int perGameListSize = PER_PAGE_ITEM_SIZE / gameSize; //所有game一共20个;


        List<ItemEntity> streams = recommendStreams(gameIds, exclusions);//因为没有用for loop来做; getStream可以直接用List;
        List<ItemEntity> clips = recommendClips(gameIds.subList(0, gameSize), perGameListSize, exclusions);//subList拿前gameSize个;
        List<ItemEntity> videos = recommendVideos(gameIds.subList(0, gameSize), perGameListSize, exclusions);


        return new TypeGroupedItemList(streams, videos, clips);
    }

    //根据gameId，return对应的strings; 根据收藏的游戏，推荐新的游戏；
    private List<ItemEntity> recommendStreams(List<String> gameIds, Set<String> exclusions) {
        List<Stream> streams = twitchService.getStreams(gameIds, PER_PAGE_ITEM_SIZE);
        List<ItemEntity> resultItems = new ArrayList<>();
        for (Stream stream: streams) { //stream可以specify 100个;
            if (!exclusions.contains(stream.id())) { //exclusions: 把收藏过的排除掉;
                resultItems.add(new ItemEntity(stream));
            }
        }
        return resultItems;
    }

    //recommendVideos可以和recommendClips可以merge成一个function，需要java里面的general模版;
    private List<ItemEntity> recommendVideos(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItems = new ArrayList<>();
        for (String gameId : gameIds) { //为啥需要两个for loop? 因为getVideos没有直接输入list;
            List<Video> listPerGame = twitchService.getVideos(gameId, perGameListSize); //perGameListSize:范围缩小一下;
            for (Video video : listPerGame) {
                if (!exclusions.contains(video.id())) {
                    resultItems.add(new ItemEntity(gameId, video));
                }
            }
        }
        return resultItems;
    }


    private List<ItemEntity> recommendClips(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItem = new ArrayList<>();
        for (String gameId : gameIds) {
            List<Clip> listPerGame = twitchService.getClips(gameId, perGameListSize);
            for (Clip clip : listPerGame) {
                if (!exclusions.contains(clip.id())) {
                    resultItem.add(new ItemEntity(clip));
                }
            }
        }
        return resultItem;
    }
}
