package com.example.chatportal;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class searchFragmet extends Fragment {

    private View view;
    private DatabaseReference dataRef;
    private FirebaseAuth firebaseAuth;
    private static String StringHandle;
    private EditText txtSearch;
    private TextView txtLoadingData;
    private LinearLayout layoutLoading;
    private ScrollView scrollData;
    private FragmentManager fragmentManager;
    private boolean conditionToSearchBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_fragmet, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();

        dataRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        StringHandle = "";
        layoutLoading = view.findViewById(R.id.loadingForSearchData);
        scrollData = (ScrollView)view.findViewById(R.id.ScrollViewData);
        txtLoadingData = (TextView)view.findViewById(R.id.textLoadingUpdatingData);



        txtSearch = (EditText)view.findViewById(R.id.editTextForSearch);
        ImageView bttnSearch = (ImageView)view.findViewById(R.id.bttnForSearching);

        bttnSearch.setOnClickListener(listenerClickSearch);

        conditionToSearchBox = true;

        return view;
    }


    private View.OnClickListener listenerClickSearch = new View.OnClickListener(){
        public void onClick(View v){
            if(onConnectWifi()) {
                if(!txtSearch.getText().toString().equals("")) {
                    if (StringHandle == "") {
                        StringHandle = txtSearch.getText().toString();
                        conditionToSearchBox = false;
                        searchingDatas dataClass = new searchingDatas();
                        dataClass.execute(StringHandle);
                    } else {
                        if (!txtSearch.getText().toString().equals(StringHandle)) {
                            StringHandle = txtSearch.getText().toString();
                            conditionToSearchBox = false;
                            new searchingDatas().execute(StringHandle);
                        } else {
                            if(!conditionToSearchBox) {
                                Toast.makeText(getContext(), "It's trying to fetch your Query please wait...", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }else{
                Toast.makeText(getContext(), "Check your connection.", Toast.LENGTH_LONG).show();
            }
        }
    };


    public class searchingDatas extends AsyncTask<String, Void, List<HandlingDatasClass>> {
        private String handleData;
        private List<Integer> integersForAddFind = new ArrayList<>();
        private List<Integer> integersForAddFindFinal = new ArrayList<>();
        private List<HandlingDatasClass> handlingClassFinfoPending = new ArrayList<HandlingDatasClass>();
        private List<HandlingDatasClass> handlingFinalClassInfoUser = new ArrayList<HandlingDatasClass>();
        private int handleIntCountChild = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scrollData.setVisibility(View.INVISIBLE);
            layoutLoading.setVisibility(View.VISIBLE);
            txtLoadingData.setText("Starting to fetch data...");
        }

        @Override
        protected List<HandlingDatasClass> doInBackground(String... strings) {
            handleData = strings[0];
            dataRef.child("ChatPortalUser").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        handleIntCountChild++;

                        if (!snap.getKey().equals(firebaseAuth.getUid()+"_infoSearchAndAdd")) {
                            int numberCountForEqual = 0, numberHandleSecondary = 0;
                            boolean conditionToZero = true, conditionToBackZero = true;
                            String handleFromBase = snap.child("FirstNameAndLastName").getValue(String.class);
                            for (int numberCountForTxt = 0; numberCountForTxt <= handleData.length() - 1; numberCountForTxt++) {

                                if (conditionToZero) {
                                    if (handleFromBase.length() - 1 >= numberCountForTxt) {
                                        char charsTextSearch = handleData.toCharArray()[numberCountForTxt];
                                        char charsTextData = handleFromBase.toCharArray()[numberCountForTxt];
                                        if (!String.valueOf(charsTextSearch).toLowerCase().equals(" ")) {
                                            if (numberCountForTxt != 0) {
                                                if (!conditionToBackZero) {
                                                    numberHandleSecondary = numberCountForEqual;
                                                    numberCountForEqual = 0;
                                                }

                                                if (String.valueOf(charsTextData).toLowerCase().equals(String.valueOf(charsTextSearch).toLowerCase())) {
                                                    numberCountForEqual++;
                                                    conditionToBackZero = true;
                                                } else {
                                                    if (!conditionToBackZero) {
                                                        conditionToBackZero = false;
                                                    } else if (!String.valueOf(charsTextData).toLowerCase().equals(String.valueOf(charsTextSearch).toLowerCase())) {
                                                        numberCountForEqual = 0;
                                                        conditionToZero = false;
                                                    }
                                                }
                                            } else {
                                                if (!String.valueOf(charsTextData).toLowerCase().equals(String.valueOf(charsTextSearch).toLowerCase())) {
                                                    conditionToZero = false;
                                                } else {
                                                    numberCountForEqual++;
                                                }
                                            }
                                        } else {
                                            conditionToBackZero = false;
                                        }
                                    }
                                }

                                if (numberCountForTxt + 1 >= handleData.length()) {
                                    if (numberCountForEqual != 0) {
                                        HandlingDatasClass handleData = new HandlingDatasClass();
                                        handleData.FirstNameAndLastName = snap.child("FirstNameAndLastName").getValue(String.class);
                                        handleData.HashPasswordUser = snap.child("HashPasswordUser").getValue(String.class);
                                        handlingClassFinfoPending.add(handleData);
                                        if (numberHandleSecondary != 0) {
                                            integersForAddFind.add((numberCountForEqual + numberHandleSecondary));
                                        } else {
                                            integersForAddFind.add(numberCountForEqual);
                                        }
                                    }
                                }
                            }

                            if (handleIntCountChild == dataSnapshot.getChildrenCount()) {
                                if (integersForAddFind.size() != 0) {
                                    for (int numberCountFounded = 0; integersForAddFind.size() > numberCountFounded; numberCountFounded++) {
                                        int numberHandle = 0;
                                        for (int numberCountFounded1 = 0; integersForAddFind.size() > numberCountFounded1; numberCountFounded1++) {
                                            boolean condiTionScanner = true;
                                            if (integersForAddFindFinal.size() == 0) {
                                                if (numberHandle == 0) {
                                                    numberHandle = integersForAddFind.get(numberCountFounded1);
                                                } else {
                                                    if (numberHandle < integersForAddFind.get(numberCountFounded1)) {
                                                        numberHandle = integersForAddFind.get(numberCountFounded1);
                                                    }
                                                }
                                            } else {

                                                for (int numberFinding = 0; integersForAddFindFinal.size() > numberFinding; numberFinding++) {
                                                    if (integersForAddFindFinal.get(numberFinding) == integersForAddFind.get(numberCountFounded1)) {
                                                        condiTionScanner = false;
                                                    }

                                                    if (numberFinding + 1 >= integersForAddFindFinal.size()) {
                                                        if (condiTionScanner) {
                                                            if (numberHandle == 0) {
                                                                numberHandle = integersForAddFind.get(numberCountFounded1);
                                                            } else {
                                                                if (numberHandle < integersForAddFind.get(numberCountFounded1)) {
                                                                    numberHandle = integersForAddFind.get(numberCountFounded1);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (numberCountFounded1 + 1 >= integersForAddFind.size()) {
                                                for (int numberCountFounded2 = 0; integersForAddFind.size() > numberCountFounded2; numberCountFounded2++) {
                                                    if (numberHandle == integersForAddFind.get(numberCountFounded2)) {
                                                        HandlingDatasClass handlePending = handlingClassFinfoPending.get(numberCountFounded2);
                                                        HandlingDatasClass handleDataAddClassFinal = new HandlingDatasClass();
                                                        handleDataAddClassFinal.FirstNameAndLastName = handlePending.FirstNameAndLastName;
                                                        handleDataAddClassFinal.HashPasswordUser = handlePending.HashPasswordUser;
                                                        handlingFinalClassInfoUser.add(handleDataAddClassFinal);
                                                        integersForAddFindFinal.add(numberHandle);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (handleIntCountChild == dataSnapshot.getChildrenCount()) {

                                if (handlingFinalClassInfoUser.size() != 0) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtLoadingData.setText("Finalizing the Query...");

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txtLoadingData.setText("Ready to show...");

                                                    showingTheQuerysSearch(handlingFinalClassInfoUser, handleData);
                                                }
                                            }, 2000);
                                        }
                                    }, 1000);
                                } else {
                                    LinearLayout layout = (LinearLayout) view.findViewById(R.id.dataLayoutHandle);
                                    layout.removeAllViews();
                                    layoutLoading.setVisibility(View.INVISIBLE);
                                    scrollData.setVisibility(View.VISIBLE);
                                    txtLoadingData.setText("Loading...");
                                    Toast.makeText(getContext(), "No found in your query...", Toast.LENGTH_LONG).show();
                                    conditionToSearchBox = true;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "asd", Toast.LENGTH_SHORT).show();
                }
            });

            return handlingFinalClassInfoUser;
        }

        @Override
        protected void onPostExecute(List<HandlingDatasClass> handlingDatasClasses) {
            super.onPostExecute(handlingDatasClasses);
        }
    }



    private void showingTheQuerysSearch(List<HandlingDatasClass> classForClass, String handleData){
        int numberCountToShow = 0;
        boolean conditionFoundEquals = false;
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.dataLayoutHandle);
        layout.removeAllViews();

        //THIS IS FOR EQUALS FIND USER_______________________________________

        for(HandlingDatasClass readyClass: classForClass){
            if(handleData.toLowerCase().equals(readyClass.FirstNameAndLastName.toLowerCase())) {
                conditionFoundEquals = true;
                LinearLayout layoutNew = new LinearLayout(getActivity());
                layoutNew.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamss.setMargins(0, 10, 0, 0);
                layoutNew.setLayoutParams(layoutParamss);
                layoutNew.setBackgroundResource(R.drawable.radiusblack);

                TextView txtViewNew = new TextView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                layoutParams.setMargins(10, 0, 0, 0);
                txtViewNew.setLayoutParams(layoutParams);
                txtViewNew.setTextColor(Color.parseColor("#FFFFFF"));
                txtViewNew.setTextSize(19);
                txtViewNew.setText(readyClass.FirstNameAndLastName);

                Button bttnNew = new Button(getActivity());
                bttnNew.setContentDescription(readyClass.HashPasswordUser);
                bttnNew.setLayoutParams(new LinearLayout.LayoutParams(
                        300, 120, 1.0f));
                bttnNew.setTransformationMethod(null);
                bttnNew.setText("Visit");
                bttnNew.setOnClickListener(clikingBttnSearch);

                layoutNew.addView(txtViewNew);
                layoutNew.addView(bttnNew);
                layout.addView(layoutNew);

            }

            if (numberCountToShow >= classForClass.size() - 1) {
                conditionToSearchBox = true;
                layoutLoading.setVisibility(View.INVISIBLE);
                scrollData.setVisibility(View.VISIBLE);
            }
            numberCountToShow++;

        }



        //THIS IS FOR NO EQUALS FINAL FOUND USER_____________________________________________________________
        if(!conditionFoundEquals) {
            for (HandlingDatasClass readyClass : classForClass) {

                LinearLayout layoutNew = new LinearLayout(getActivity());
                layoutNew.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamss.setMargins(0, 10, 0, 0);
                layoutNew.setLayoutParams(layoutParamss);
                layoutNew.setBackgroundResource(R.drawable.radiusblack);

                TextView txtViewNew = new TextView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                layoutParams.setMargins(10, 0, 0, 0);
                txtViewNew.setLayoutParams(layoutParams);
                txtViewNew.setTextColor(Color.parseColor("#FFFFFF"));
                txtViewNew.setTextSize(19);
                txtViewNew.setText(readyClass.FirstNameAndLastName);

                Button bttnNew = new Button(getContext());
                bttnNew.setContentDescription(readyClass.HashPasswordUser);
                bttnNew.setLayoutParams(new LinearLayout.LayoutParams(
                        300, 120, 1.0f));
                bttnNew.setTransformationMethod(null);
                bttnNew.setText("Visit");
                bttnNew.setOnClickListener(clikingBttnSearch);

                layoutNew.addView(txtViewNew);
                layoutNew.addView(bttnNew);
                layout.addView(layoutNew);

                if (numberCountToShow >= classForClass.size() - 1) {
                    conditionToSearchBox = true;
                    layoutLoading.setVisibility(View.INVISIBLE);
                    scrollData.setVisibility(View.VISIBLE);
                }
                numberCountToShow++;
            }
        }

        conditionToSearchBox = true;
    }


    public View.OnClickListener clikingBttnSearch = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("hashData", v.getContentDescription().toString());
            Fragment fragment = new UserSearchFound();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_search, fragment, "fragmentData");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("fragmentData");
            fragmentTransaction.commit();
        }
    };


    //Checking if the wifi or mobile data is connected________________________________________
    public boolean onConnectWifi(){
        boolean connection = false;
        ConnectivityManager connectTivityManager = (ConnectivityManager)getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netWorkInfo = connectTivityManager.getAllNetworkInfo();
        for(NetworkInfo netW: netWorkInfo){
            if(netW.getTypeName().equalsIgnoreCase("WIFI")){
                if(netW.isConnected()){
                    connection = true;
                    break;
                }
            }else if(netW.getTypeName().equalsIgnoreCase("MOBILE")){
                if(netW.isConnected()){
                    connection = true;
                    break;
                }
            }
        }
        return connection;
    }
}
