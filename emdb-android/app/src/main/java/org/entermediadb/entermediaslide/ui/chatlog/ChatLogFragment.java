package org.entermediadb.entermediaslide.ui.chatlog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.quickstart.auth.R;

public class ChatLogFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatlog, container, false);
    }
    */


    //https://developer.android.com/guide/navigation/navigation-pass-data
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView tv = view.findViewById(R.id.text_slideshow);
       // int amount = ConfirmationFragmentArgs.fromBundle(getArguments()).getAmount();
       // tv.setText(amount + "");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chatlog, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}