package edu.cnm.deepdive.blackjack.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;

public class MainViewModel extends AndroidViewModel {

  private final BlackjackDatabase database;
  private MutableLiveData<Shoe> shoe;
  private long shoeId;

  public MainViewModel(@NonNull Application application) {
    super(application);
    database = BlackjackDatabase.getInstance();
  }

  public LiveData<Shoe> getShoe() {
    return database.getShoeDao().getById(shoeId);
  }

}