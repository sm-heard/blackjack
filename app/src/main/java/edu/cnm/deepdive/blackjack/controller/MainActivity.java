package edu.cnm.deepdive.blackjack.controller;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    viewModel.getRound().observe(this, (round) -> {
    });
    Button addDeck = findViewById(R.id.start_round);
    addDeck.setOnClickListener((view) -> viewModel.startRound());
    setupFragments();
  }

  private void setupFragments() {
    Fragment dealerFragment = new DealerHandFragment();
    Fragment playerFragment = new PlayerHandFragment();
    FragmentManager manager = getSupportFragmentManager();
    manager.beginTransaction()
        .replace(R.id.dealer_hand, dealerFragment)
        .replace(R.id.player_hand, playerFragment)
        .commit();
  }

}