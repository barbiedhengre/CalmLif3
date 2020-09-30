package co.in.nextgencoder.calmlif3.ui.my_moments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import co.in.nextgencoder.calmlif3.R;

public class MyMomentsFragment extends Fragment {

    private MyMomentsViewModel myMomentsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myMomentsViewModel =
                ViewModelProviders.of(this).get(MyMomentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_moments, container, false);

        myMomentsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}