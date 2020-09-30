package co.in.nextgencoder.calmlif3.ui.my_moments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyMomentsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyMomentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}