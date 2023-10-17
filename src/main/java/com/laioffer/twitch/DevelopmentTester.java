package com.laioffer.twitch;


import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.UserRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.external.TwitchService;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Game;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.model.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.List;


@Component //annotation，表示运行application要顺带运行这个；
public class DevelopmentTester implements ApplicationRunner {
//springboot 提供了application runner；

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentTester.class);

    //public DevelopmentTester(TwitchService twitchService) {
        //this.twitchService = twitchService;
    //}

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;


    public DevelopmentTester(
            UserRepository userRepository,
            ItemRepository itemRepository,
            FavoriteRecordRepository favoriteRecordRepository
    ) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.favoriteRecordRepository = favoriteRecordRepository;
    }




    @Override
    public void run(ApplicationArguments args) {
//      把五个api都call了；
        //List<Game> games = twitchService.getGames("fortnite");
        //List<Game> topGames = twitchService.getTopGames();
        //List<Video> videos = twitchService.getVideos("21779", 2);
        //List<Clip> clips = twitchService.getClips("21779", 2);
        //List<Stream> streams = twitchService.getStreams(List.of("18122", "21779", "33214"), 10);

        //logger.info(games.toString());
        //System.out.println(games.toString()); 没有logger方便debug;
        //logger.info(topGames.toString());
        //logger.info(videos.toString());
        //logger.info(clips.toString());
        //logger.info(streams.toString());
        // ******************

        //save一个object
        UserEntity newUser = new UserEntity(null, "user0", "Foo", "Bar", "password"); //只是创建了一个object;
        userRepository.save(newUser); //这一步才是经过repository来把object存进去；


        UserEntity user1 = new UserEntity(null, "user1", "John", "Smith", "password");
        UserEntity user2 = new UserEntity(null, "user2", "Jane", "Smith", "password");
        UserEntity user3 = new UserEntity(null, "user3", "John", "Doe", "password");

        //save一个list of object;
        userRepository.saveAll(List.of(user1, user2, user3));//save all users;

        UserEntity user = userRepository.findById(1L).get(); //findById(Long)；如果不是long怎么办？就要在api的args里面specify类型；不加L就变成了int;
        logger.info("User: "+user);

        //创建单个item:
        ItemEntity newItem = new ItemEntity(null, "abc0", "Title0", "url0", "thumb0", "name0", "game0", ItemType.CLIP);
        itemRepository.save(newItem);

        //创建list of items:
        List<ItemEntity> newItems = List.of(
                new ItemEntity(null, "abc1", "Title1", "url1", "thumb1", "name1", "game1", ItemType.VIDEO),
                new ItemEntity(null, "abc2", "Title2", "url2", "thumb2", "name2", "game2", ItemType.VIDEO),
                new ItemEntity(null, "abc3", "Title3", "url3", "thumb3", "name3", "game3", ItemType.STREAM),
                new ItemEntity(null, "abc4", "Title4", "url4", "thumb4", "name4", "game4", ItemType.STREAM)
        );
        itemRepository.saveAll(newItems);


        favoriteRecordRepository.saveAll(
                List.of(
                        new FavoriteRecordEntity(null, 1L, 1L, Instant.now()),//User 1 likes item 1;
                        new FavoriteRecordEntity(null, 1L, 3L, Instant.now()),
                        new FavoriteRecordEntity(null, 1L, 4L, Instant.now()),
                        new FavoriteRecordEntity(null, 2L, 2L, Instant.now())
                        //破坏foreign key怎么办？
                        //new FavoriteRecordEntity(null, 20L, 2L, Instant.now()) 用户不存在;
                )
        );

        // user0 update to Far Boo;
        userRepository.updateNameByUsername("user0", "Far", "Boo");
        List<UserEntity> johns = userRepository.findByFirstName("John");
        logger.info("John: " + johns);
        List<UserEntity> smiths = userRepository.findByLastName("Smith");
        logger.info("Smith: " + smiths);


        UserEntity user0 = userRepository.findByUsername("user0");
        logger.info("user0: " + user0);


        boolean item1Exists = itemRepository.existsById(1L);
        boolean item100Exists = itemRepository.existsById(100L);
        logger.info("Item1exists: " + item1Exists);
        logger.info("Item100exists: " + item100Exists);


        itemRepository.deleteById(2L);


        boolean item2Exists = itemRepository.existsById(2L);
        ItemEntity abc1 = itemRepository.findByTwitchId("abc1");


        List<FavoriteRecordEntity> user1Favorites = favoriteRecordRepository.findAllByUserId(1L);
        List<FavoriteRecordEntity> user2Favorites = favoriteRecordRepository.findAllByUserId(2L);


        List<Long> user1FavoriteItemIds = favoriteRecordRepository.findFavoriteItemIdsByUserId(1L);
        List<Long> user2FavoriteItemIds = favoriteRecordRepository.findFavoriteItemIdsByUserId(2L);


        favoriteRecordRepository.delete(1L, 3L);
        List<FavoriteRecordEntity> user1FavoritesAfterDelete = favoriteRecordRepository.findAllByUserId(1L);



    }

}

