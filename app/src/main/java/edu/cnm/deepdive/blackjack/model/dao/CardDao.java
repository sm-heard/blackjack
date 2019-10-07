package edu.cnm.deepdive.blackjack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import java.util.Collection;
import java.util.List;

@Dao
public interface CardDao {

  @Insert
  List<Long> insert(Collection<Card> cards);

  @Query("SELECT * FROM Card WHERE shoe_id = :shoeId ORDER BY card_id ASC LIMIT 1")
  Card getTopCardInShoe(long shoeId);

  @Update
  int update(Card card);

  @Update
  int update(Card... cards);

}