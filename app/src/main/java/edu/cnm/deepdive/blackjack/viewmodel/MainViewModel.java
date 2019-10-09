package edu.cnm.deepdive.blackjack.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import edu.cnm.deepdive.blackjack.model.dao.CardDao;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Card.Suit;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import edu.cnm.deepdive.blackjack.model.entity.Round;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.model.pojo.RoundWithDetails;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainViewModel extends AndroidViewModel {

  private final BlackjackDatabase database;
  private long shoeId;
  private long markerId;
  private boolean shuffleNeeded;
  private Random rng;
  private MutableLiveData<Long> roundId;
  private LiveData<RoundWithDetails> round;
  private MutableLiveData<Long> dealerHandId;
  private MutableLiveData<Long> playerHandId;
  private LiveData<HandWithCards> dealerHand;
  private LiveData<HandWithCards> playerHand;

  public MainViewModel(@NonNull Application application) {
    super(application);
    database = BlackjackDatabase.getInstance();
    rng = new SecureRandom(); // TODO Use Mersenne Twister.
    roundId = new MutableLiveData<>();
    round = Transformations.switchMap(roundId,
        (id) -> database.getRoundDao().getRoundWithDetails(id));
    dealerHandId = new MutableLiveData<>();
    dealerHand = Transformations.switchMap(dealerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
    playerHandId = new MutableLiveData<>();
    playerHand = Transformations.switchMap(playerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
  }

  public LiveData<RoundWithDetails> getRound() {
    return round;
  }

  public LiveData<HandWithCards> getDealerHand() {
    return dealerHand;
  }

  public LiveData<HandWithCards> getPlayerHand() {
    return playerHand;
  }

  private void createShoe() {
    Shoe shoe = new Shoe();
    shoeId = database.getShoeDao().insert(shoe);
    List<Card> cards = new ArrayList<>();
    for (int i = 0; i < 6; i++) { // Repeat for # of decks in shoe
      for (Rank rank : Rank.values()) { // Repeat for each rank
        for (Suit suit : Suit.values()) { // Repeat for each suit
          Card card = new Card();
          card.setShoeId(shoeId);
          card.setRank(rank);
          card.setSuit(suit);
          cards.add(card);
        }
      }
    }
    Collections.shuffle(cards, rng);
    int startIndex = cards.size() * 2 / 3;
    int endIndex = cards.size() * 3 / 4;
    int markerPosition = startIndex + rng.nextInt(endIndex - startIndex);
    List<Long> cardIds = database.getCardDao().insert(cards);
    markerId = cardIds.get(markerPosition);
    shoe.setMarkerId(markerId);
    database.getShoeDao().update(shoe);
    shuffleNeeded = false;
  }

  public void startRound() {
    new Thread(() -> {
      Round round = new Round();
      if (shoeId == 0 || shuffleNeeded) {
        createShoe();
      }
      round.setShoeId(shoeId);
      long roundId = database.getRoundDao().insert(round);
      Hand dealer = new Hand();
      dealer.setRoundId(roundId);
      dealer.setDealer(true);
      Hand player = new Hand();
      player.setRoundId(roundId);
      long[] handIds = database.getHandDao().insert(dealer, player);
      for (long handId : handIds) {
        for (int i = 0; i < 2; i++) {
          Card card = getTopCard(handId);
        }
      }
      this.roundId.postValue(roundId);
      this.dealerHandId.postValue(handIds[0]);
      this.playerHandId.postValue(handIds[1]);
    }).start();
  }

  public void hitPlayer() {
    new Thread(() -> {
      CardDao dao = database.getCardDao();
      Long handId = playerHandId.getValue();
      Card card = getTopCard(handId);
      playerHandId.postValue(handId);
    }).start();
  }

  private Card getTopCard(long handId) {
    CardDao cardDao = database.getCardDao();
    Card card = cardDao.getTopCardInShoe(shoeId);
    card.setShoeId(null);
    card.setHandId(handId);
    cardDao.update(card);
    if (card.getId() == markerId) {
      shuffleNeeded = true;
    }
    return card;
  }

}
