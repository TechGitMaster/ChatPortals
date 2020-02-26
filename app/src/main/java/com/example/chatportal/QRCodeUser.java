package com.example.chatportal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class QRCodeUser extends Fragment {
    private View viewFindIds;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFindIds = inflater.inflate(R.layout.fragment_qrcode_user, container, false);
        return viewFindIds;
    }

}
