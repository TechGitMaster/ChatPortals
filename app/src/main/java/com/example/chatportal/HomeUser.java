package com.example.chatportal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class HomeUser extends Fragment {

    private View viewFindIds;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static String handleBttn3 = "informationUser";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFindIds = inflater.inflate(R.layout.fragment_home_user, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        ImageView imageInformation = (ImageView)viewFindIds.findViewById(R.id.informationUser);
        ImageView messangerUser = (ImageView) viewFindIds.findViewById(R.id.messangerUser);
        ImageView QRCodeUser  = (ImageView)viewFindIds.findViewById(R.id.QRCodeUser);
        imageInformation.setOnClickListener(listenerBttnHead);
        messangerUser.setOnClickListener(listenerBttnHead);
        QRCodeUser.setOnClickListener(listenerBttnHead);

        Button bttnLogOut = (Button)viewFindIds.findViewById(R.id.logOut);
        bttnLogOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Fragment fragment = new ChatLogin();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragments, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();

                FirebaseAuth firebase = FirebaseAuth.getInstance();
                firebase.signOut();
            }
        });


        Button bttnSearch = viewFindIds.findViewById(R.id.searchBttn);
        bttnSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(getActivity(), searchActivityForUser.class));
            }
        });



        return viewFindIds;
    }


    private View.OnClickListener listenerBttnHead = new View.OnClickListener(){
        @SuppressLint("ResourceAsColor")
        public void onClick(View view){
            if(view.getContentDescription().toString().equals("informationUser")){
                if(!view.getContentDescription().toString().equals(handleBttn3)) {

                    Fragment fragment = new InformationUser();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHomeUser, fragment);
                   // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();

                    handleBttn3 = view.getContentDescription().toString();
                }
            }else if(view.getContentDescription().toString().equals("messangerUser")){
                if(!view.getContentDescription().toString().equals(handleBttn3)) {


                    Fragment fragment = new MessangerUser();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHomeUser, fragment);
                   // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();

                    handleBttn3 = view.getContentDescription().toString();
                }
            }else if(view.getContentDescription().toString().equals("QRCodeUser")){
                if(!view.getContentDescription().toString().equals(handleBttn3)) {

                    Fragment fragment = new QRCodeUser();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHomeUser, fragment);
                    //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();

                    handleBttn3 = view.getContentDescription().toString();
                }
            }
        }
    };


}
