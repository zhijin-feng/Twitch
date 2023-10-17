package com.laioffer.twitch.db;


import com.laioffer.twitch.db.entity.ItemEntity;
import org.springframework.data.repository.ListCrudRepository;


public interface ItemRepository extends ListCrudRepository<ItemEntity, Long> {
    //<>里面是type; 操作空间是ItemEntity,对应item表. 所以不需要写where...

    //自己写：SELECT * FROM items WHERE twitch_id = ?
    //ItemEntity findItemEntityByTwitchIdStartWith(String ?);

    //ItemEntity findItemByBroadcastNameStartingWithAndTitleContaining(String broadcastName, String title);
    ItemEntity findByTwitchId(String twitchId); //找到符合id的一行;

}


