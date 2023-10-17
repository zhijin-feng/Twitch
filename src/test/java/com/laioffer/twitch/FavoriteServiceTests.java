package com.laioffer.twitch;


import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.favorite.FavoriteService;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.ItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)//Test Setup: 这是unittest的mockito library,方便做mocking;
public class FavoriteServiceTests {

    //一般都是mock constructors传进来的dependency;
    @Mock private ItemRepository itemRepository; //Mock:在unittest里面abstract掉一些因素的干扰;
    //Mock可以传进来一个假的repository,不需要连db; 好处是减少了dependency; 并且假设itemRepository是百分之百没问题的;
    @Mock private FavoriteRecordRepository favoriteRecordRepository;


    @Captor ArgumentCaptor<FavoriteRecordEntity> favoriteRecordArgumentCaptor;


    private FavoriteService favoriteService;


    @BeforeEach
    public void setup() {
        favoriteService = new FavoriteService(itemRepository, favoriteRecordRepository);
    }


    @Test
    //确认item存在的时候要保存;
    public void whenItemNotExist_setFavoriteItem_shouldSaveItem() throws DuplicateFavoriteException {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(null);
        //当itemRepository findByTwitchId的时候，请你return null;
        Mockito.when(itemRepository.save(item)).thenReturn(persisted);
        //当itemRepository save的时候，请你return一个保存好的带id的item;
        favoriteService.setFavoriteItem(user, item);
        Mockito.verify(itemRepository).save(item);
    }


    @Test
    //确认item存在的时候不要保存;
    public void whenItemExist_setFavoriteItem_shouldNotSaveItem() throws DuplicateFavoriteException {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);
        favoriteService.setFavoriteItem(user, item);
        Mockito.verify(itemRepository, Mockito.never()).save(item);
    }


    @Test
    public void setFavoriteItem_shouldCreateFavoriteRecord() throws DuplicateFavoriteException {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);


        favoriteService.setFavoriteItem(user, item);


        Mockito.verify(favoriteRecordRepository).save(favoriteRecordArgumentCaptor.capture());
        FavoriteRecordEntity favorite = favoriteRecordArgumentCaptor.getValue();


        Assertions.assertEquals(1L, favorite.itemId());
        Assertions.assertEquals(1L, favorite.userId());
    }


    @Test
    public void whenItemNotExist_unsetFavoriteItem_shouldNotDeleteFavoriteRecord() {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(null);


        favoriteService.unsetFavoriteItem(user, "twitchId");


        Mockito.verifyNoInteractions(favoriteRecordRepository);
    }


    @Test
    public void whenItemExist_unsetFavoriteItem_shouldDeleteFavoriteRecord() {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);


        favoriteService.unsetFavoriteItem(user, "twitchId");


        Mockito.verify(favoriteRecordRepository).delete(1L, 1L);
    }
}

