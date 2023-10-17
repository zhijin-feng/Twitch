package com.laioffer.twitch.favorite;


import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;


@Service
public class FavoriteService {

    //点赞、删除favorites都由
    //dependency injection:
    private final ItemRepository itemRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;


    public FavoriteService(ItemRepository itemRepository,
                           FavoriteRecordRepository favoriteRecordRepository) {
        this.itemRepository = itemRepository;
        this.favoriteRecordRepository = favoriteRecordRepository;
    }

    @CacheEvict(cacheNames = "recommend_items", key = "#user")//key: userEntity, 当userA发request，会给userA发一个新的版本;
    //#表示user要和下面的保持一致;
    //如果和下面的函数没有同一个type的key，要inject cache manager;
    @Transactional
    //为什么会有这么一个annotation? 因为有exception,为了保证操作的原子性ACID,throw了exception会进行回滚;
    //原理：写到一个缓存的地方，没有直接写到db里面;
    public void setFavoriteItem(UserEntity user, ItemEntity item) throws DuplicateFavoriteException {
        ItemEntity persistedItem = itemRepository.findByTwitchId(item.twitchId());
        //查一下item是否存在；(因为db里面没有存所有的item)
        if (persistedItem == null) {
            persistedItem = itemRepository.save(item); //null代表之前没有存过；
        }
        if (favoriteRecordRepository.existsByUserIdAndItemId(user.id(), persistedItem.id())) {
            throw new DuplicateFavoriteException();//出现了收藏的重复;直接return也可以; 项目越大，用exception越方便，直接跳到最外面;
        }
        FavoriteRecordEntity favoriteRecord = new FavoriteRecordEntity(null, user.id(), persistedItem.id(), Instant.now());
        //这里的id是null是因为会自动帮忙创建;如果id不是null会不让创建; spring JDBC也有方法实现，比如auto-increment;
        favoriteRecordRepository.save(favoriteRecord);//实现save favorite record;
    }
    @CacheEvict(cacheNames = "recommend_items", key = "#user")
    //去掉favoriteItem; 这个function没有transactional; 因为写的操作只有delete一步;
    public void unsetFavoriteItem(UserEntity user, String twitchId) {
        ItemEntity item = itemRepository.findByTwitchId(twitchId);
        if (item != null) {
            favoriteRecordRepository.delete(user.id(), item.id());
        }
    }
    //不要在这里删除item,可能其他人也点了赞;


    public List<ItemEntity> getFavoriteItems(UserEntity user) {
        List<Long> favoriteItemIds = favoriteRecordRepository.findFavoriteItemIdsByUserId(user.id());
        return itemRepository.findAllById(favoriteItemIds);
    }


    public TypeGroupedItemList getGroupedFavoriteItems(UserEntity user) { //交给TypedGroupedItemList来分类;
        List<ItemEntity> items = getFavoriteItems(user); //call了上面的api;
        return new TypeGroupedItemList(items);
    }
}

