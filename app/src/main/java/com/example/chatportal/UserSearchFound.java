package com.example.chatportal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class UserSearchFound extends Fragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_search_found, container, false);


        Bundle bundleGetData = getArguments();
        Toast.makeText(getContext(), bundleGetData.getString("hashData"), Toast.LENGTH_LONG).show();

        return view;
    }
}
