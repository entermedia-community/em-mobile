package org.entermediadb.chat2.ui.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import org.entermediadb.chat2.R;

public class Myfragment extends Fragment {

    LinearLayout linearLayout;
    View rootView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //linearLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        return rootView;
    }

    public void addPlaces() {
        Button button = new Button(getActivity()); // needs activity context
        button.setText("button name");
       // linearLayout.addView(button);
    }
}