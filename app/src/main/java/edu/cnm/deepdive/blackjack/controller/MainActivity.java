package edu.cnm.deepdive.blackjack.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Card.Suit;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button addDeck = findViewById(R.id.add_deck);
    addDeck.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        new Thread(() -> createDeck()).start();
      }
    });
  }

  private void createDeck() {
    BlackjackDatabase db = BlackjackDatabase.getInstance();
    Shoe shoe = new Shoe();
    long shoeId = db.getShoeDao().insert(shoe);
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
    Collections.shuffle(cards);
    db.getCardDao().insert(cards);
  }
}
