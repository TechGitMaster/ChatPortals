package com.example.chatportal;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;


public class InformationUser extends Fragment implements ChildEventListener {


    private View viewFindIds;
    private androidx.constraintlayout.widget.ConstraintLayout constraintLayoutLoadProgress, informationLayout, linearImageUpload;
    private ProgressBar progressBar;
    private TextView textViewCantLoad, friends_textView, request_textView, camera_textView, gallery_textView, own_nameUser, emailAccountUser, usernameUser;
    private CardView cardView_friends, cardView_friendRequest, cardView_imagePhotoNew, cardView_galleryPhoto;
    private DatabaseReference databaseReferece, databaseRef;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private boolean conditionBoolToGetInfoAndNumber = true, conditionFinalToTimeout = true, conditionChooseFileProgressAndInformation = true,
    conditionToConnection = true;
    private String conditionFirst = "", UidUser;
    private final int image_request = 1;
    private ProgressDialog progressDialog;
    private ProgressBar progressBarImageSave;
    private TextView txtViewProgressBarSaveImage;
    private Uri dataImage;
    private Handler handleArea;
    private ImageView imgageUserFinal;
    private int[] arrayCheckIfExist =  new int[] {0, 0, 0};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFindIds =  inflater.inflate(R.layout.fragment_information_user, container, false);

        //connection in firebase________________________________________________
        databaseReferece = FirebaseDatabase.getInstance().getReference("MemberChatPortalInfoAccount");
        databaseRef = FirebaseDatabase.getInstance().getReference("ChatPortalUser");
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("image_user");



        imgageUserFinal = viewFindIds.findViewById(R.id.imageUserHome);


        UidUser = firebaseAuth.getUid();

        //loading page controllers__________________________________________________________________
        constraintLayoutLoadProgress = viewFindIds.findViewById(R.id.loadingProgress);
        progressBar = viewFindIds.findViewById(R.id.progressBar);
        textViewCantLoad = viewFindIds.findViewById(R.id.cantLoad);

        //information page controllers_________________________________________________
        informationLayout = viewFindIds.findViewById(R.id.informationLayout);
        friends_textView = viewFindIds.findViewById(R.id.friends_textView);
        request_textView = viewFindIds.findViewById(R.id.request_textView);
        camera_textView = viewFindIds.findViewById(R.id.camera_textView);
        gallery_textView = viewFindIds.findViewById(R.id.gallery_textView);


        //information account user________________________________________
        own_nameUser = viewFindIds.findViewById(R.id.own_nameUser);
        emailAccountUser = viewFindIds.findViewById(R.id.emailAccountUser);
        usernameUser = viewFindIds.findViewById(R.id.usernameUser);


        //clicked information controllers_____________________________________________
        cardView_friends = viewFindIds.findViewById(R.id.cardView_friends);
        cardView_friendRequest = viewFindIds.findViewById(R.id.cardView_requestFriends);
        cardView_imagePhotoNew = viewFindIds.findViewById(R.id.cardView_imagePhotoNew);
        cardView_imagePhotoNew.setOnClickListener(clickCreateNewImage);
        cardView_galleryPhoto = viewFindIds.findViewById(R.id.cardView_galleryPhoto);
        cardView_galleryPhoto.setOnClickListener(clickListenerGallery);
        //_______________________________________________________



        //progressDialog ImageSelected_______________________________________________________________
        progressDialog = new ProgressDialog(getActivity());


        handleArea = new Handler();
        if(onConnectWifi()) {
            //start calling ang getting the information______________________________________________
            new gettingInformationUser().execute();
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            textViewCantLoad.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Check your connection..", Toast.LENGTH_LONG).show();

            handleArea.postDelayed(runTimerCheckConnections, 5000);

        }



        return viewFindIds;
    }






    //gallery clicked______________________________________________________________
    private View.OnClickListener clickListenerGallery = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), gallery_information.class);
            startActivity(intent);
        }
    };




    //AsyncTask_____________________________________________________

    //get the data into firebase and return it updated__________________________________________________________
    private class gettingInformationUser extends AsyncTask<Void, Void, Void>{

        private Runnable run;
        private Handler handler;
        private boolean conditions;
        private String stHandleEmail;
        int[] arrayCountNumber;
        long[] longGetData;
        boolean condition;
        public gettingInformationUser(){
            conditions = false;
            stHandleEmail = "";
            condition = true;
            arrayCountNumber = new int[] {0, 0, 0};
            longGetData = new long[] {0, 0, 0};
            handler = new Handler();

            run = new Runnable(){
                @Override
                public void run() {
                    if(!conditions) {
                        progressBar.setVisibility(View.INVISIBLE);
                        textViewCantLoad.setVisibility(View.VISIBLE);
                    }
                }
            };




        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(conditionFirst.equals("")) {
                handler.postDelayed(run, 10000);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
                try {
                    databaseReferece.child(UidUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (conditionFirst.equals("")) {
                                conditions = true;
                                conditionBoolToGetInfoAndNumber = false;
                                progressBar.setVisibility(View.VISIBLE);
                                textViewCantLoad.setVisibility(View.INVISIBLE);

                                DataSnapshot dataGetInformation = dataSnapshot.child(UidUser + "_informationUser");
                                String handleEmail = dataGetInformation.child("Email").getValue(String.class);
                                for (int numberCountEmail = 0; numberCountEmail < (handleEmail.length() - 10); numberCountEmail++) {

                                    if (arrayCountNumber[0] == 0) {
                                        arrayCountNumber[0]++;
                                    } else {
                                        if (condition) {
                                            arrayCountNumber[1]++;
                                            condition = false;
                                        } else {
                                            arrayCountNumber[2]++;
                                            condition = true;
                                        }
                                    }

                                    if (numberCountEmail + 1 >= dataGetInformation.child("Email").getValue(String.class).length() - 10) {
                                        for (int numberCountEmail2 = 0; numberCountEmail2 < (handleEmail.length() - 10); numberCountEmail2++) {
                                            char charToEmail = handleEmail.toCharArray()[numberCountEmail2];
                                            if (numberCountEmail2 + 1 <= arrayCountNumber[1]) {
                                                stHandleEmail += charToEmail;
                                            } else {
                                                if (numberCountEmail2 + 1 <= (arrayCountNumber[1] + arrayCountNumber[2])) {
                                                    stHandleEmail += "*";
                                                } else {
                                                    stHandleEmail += charToEmail;
                                                }
                                            }

                                            if ((numberCountEmail2 + 2) > (handleEmail.length() - 10)) {

                                                //set the name, email and username of USER!_________________________________________________________
                                                own_nameUser.setText(dataGetInformation.child("FirstNameAndLastName").getValue(String.class));
                                                emailAccountUser.setText(stHandleEmail + "@gmail.com");
                                                usernameUser.setText(dataGetInformation.child("Username").getValue(String.class));
                                                conditionBoolToGetInfoAndNumber = true;
                                            }
                                        }
                                    }
                                }
                            }


                            if (conditionBoolToGetInfoAndNumber) {
                                if (dataSnapshot.hasChild(UidUser + "_UserMultiTaskDatasInformation")) {
                                    conditions = false;
                                    progressBar.setVisibility(View.VISIBLE);
                                    textViewCantLoad.setVisibility(View.INVISIBLE);
                                    handler.postDelayed(run, 10000);

                                    if (dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").hasChild("friendsUser")) {

                                        longGetData[0] = dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").child("friendsUser").getChildrenCount();

                                        if (dataSnapshot.child(UidUser+ "_UserMultiTaskDatasInformation").hasChild("friendsRequestUser")) {
                                            longGetData[1] = dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").child("friendsRequestUser").getChildrenCount();

                                            if (dataSnapshot.child(UidUser+ "_UserMultiTaskDatasInformation").hasChild("imageUser")) {
                                                longGetData[2] = dataSnapshot.child(UidUser+ "_UserMultiTaskDatasInformation").child("imageUser").getChildrenCount();
                                                conditions = true;
                                            } else {
                                                //00
                                                longGetData[2] = 0;
                                                conditions = true;
                                            }
                                        } else {
                                            //00
                                            longGetData[1] = 0;

                                            if (dataSnapshot.child(UidUser+ "_UserMultiTaskDatasInformation").hasChild("imageUser")) {
                                                longGetData[2] = dataSnapshot.child(UidUser+ "_UserMultiTaskDatasInformation").child("imageUser").getChildrenCount();
                                                conditions = true;
                                            } else {
                                                //00
                                                longGetData[2] = 0;
                                                conditions = true;
                                            }

                                        }
                                    } else {
                                        //00
                                        longGetData[0] = 0;

                                        if (dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").hasChild("friendsRequestUser")) {
                                            longGetData[1] = dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").child("friendsRequestUser").getChildrenCount();

                                            if (dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").hasChild("imageUser")) {
                                                longGetData[2] = dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").child("imageUser").getChildrenCount();
                                                conditions = true;
                                            } else {
                                                //00
                                                longGetData[2] = 0;
                                                conditions = true;
                                            }
                                        } else {
                                            //00
                                            longGetData[1] = 0;
                                            if (dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").hasChild("imageUser")) {
                                                longGetData[2] = dataSnapshot.child(UidUser + "_UserMultiTaskDatasInformation").child("imageUser").getChildrenCount();
                                                conditions = true;
                                            } else {
                                                //00
                                                longGetData[2] = 0;
                                                conditions = true;
                                            }
                                        }
                                    }

                                    if (conditions) {
                                        //image get___________________________
                                        progressBar.setVisibility(View.VISIBLE);
                                        textViewCantLoad.setVisibility(View.INVISIBLE);

                                        friends_textView.setText((longGetData[0] <= 9 ? "0" + longGetData[0] : String.valueOf(longGetData[0])));
                                        request_textView.setText(longGetData[1] <= 9 ? "0" + longGetData[1] : String.valueOf(longGetData[1]));
                                        gallery_textView.setText(longGetData[2] <= 9 ? "0" + longGetData[2] : String.valueOf(longGetData[2]));


                                        if (conditionFirst != "hasButNoDatabases") {
                                            databaseReferece.child(UidUser).child(UidUser + "_UserMultiTaskDatasInformation").addChildEventListener(InformationUser.this);
                                        }
                                        conditionFirst = "hasButNoDatabases";


                                        //friendsUser__________________________________________________________________
                                        if (longGetData[0] != 0) {
                                            if (arrayCheckIfExist[0] == 0) {
                                                databaseReferece.child(UidUser).child(UidUser + "_UserMultiTaskDatasInformation")
                                                        .child("friendsUser").addChildEventListener(InformationUser.this);
                                                arrayCheckIfExist[0] = 1;
                                            }
                                        } else {
                                            arrayCheckIfExist[0] = 1;
                                        }


                                        //friendsRequestUser______________________________________________________
                                        if (longGetData[1] != 0) {
                                            if (arrayCheckIfExist[1] == 0) {
                                                databaseReferece.child(UidUser).child(UidUser + "_UserMultiTaskDatasInformation")
                                                        .child("friendsRequestUser").addChildEventListener(InformationUser.this);
                                                arrayCheckIfExist[1] = 1;
                                            }
                                        } else {
                                            arrayCheckIfExist[1] = 1;
                                        }




                                        //galleryUser___________________________________________________________
                                        if (longGetData[2] != 0) {
                                            if (arrayCheckIfExist[2] == 0) {
                                                databaseReferece.child(UidUser).child(UidUser + "_UserMultiTaskDatasInformation")
                                                        .child("imageUser").addChildEventListener(InformationUser.this);
                                                arrayCheckIfExist[2] = 1;
                                            }
                                        } else {
                                            arrayCheckIfExist[2] = 1;
                                        }


                                        if (conditionFinalToTimeout) {
                                                DatabaseReference datass = databaseReferece.child(UidUser).child("imageUsers");
                                                datass.addListenerForSingleValueEvent(new ValueEventListener(){
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).fit().centerCrop().into(imgageUserFinal);
                                                        conditionFinalToTimeout = false;
                                                        handlerSetTimeout();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                        }
                                    }
                                } else {
                                    conditionFirst = "hasButNoDatabases";
                                    progressBar.setVisibility(View.VISIBLE);
                                    textViewCantLoad.setVisibility(View.INVISIBLE);
                                    friends_textView.setText("00");
                                    request_textView.setText("00");
                                    gallery_textView.setText("00");

                                    if (conditionFirst != "hasButNoDatabase") {
                                        databaseReferece.child(UidUser).addChildEventListener(InformationUser.this);
                                    }
                                    conditionFirst = "hasButNoDatabase";

                                    if (conditionFinalToTimeout) {

                                        DatabaseReference datass = databaseReferece.child(UidUser).child("imageUsers");
                                        datass.addListenerForSingleValueEvent(new ValueEventListener(){
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                final String strings = dataSnapshot.getValue().toString();
                                                Picasso picasso = Picasso.with(getActivity());
                                                        picasso.load(strings).fit().centerCrop().into(imgageUserFinal);
                                                conditionFinalToTimeout = false;
                                                DatabaseReference datassSaves = databaseReferece.child(UidUser);
                                                datassSaves.child(UidUser+"_UserMultiTaskDatasInformation")
                                                        .child("imageUser")
                                                        .push().setValue(strings).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            databaseReferece.child(UidUser).child(UidUser + "_UserMultiTaskDatasInformation").addChildEventListener(InformationUser.this);
                                                            conditionFirst = "hasButNoDatabases";
                                                            handlerSetTimeout();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (RuntimeException e){
                  //  Fragment fragmentChatPortal = new ChatLogin();
                }

            return null;
        }




        private void handlerSetTimeout(){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    constraintLayoutLoadProgress.setVisibility(View.INVISIBLE);
                    informationLayout.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }
    }





    //upload new photo and update the photo of user to all connected with it____________________________________________________
    private class savingImageDataAndUpdateImageUser extends AsyncTask<Uri, Void, Void>{
        private Uri uriss;
        Calendar calendar;
        DateFormat dateFormat;
        @Override
        protected void onPreExecute() {
            calendar = Calendar.getInstance();
            dateFormat = DateFormat.getInstance();
            super.onPreExecute();
            progressBarImageSave.setMax(100);
        }

        @Override
        protected Void doInBackground(final Uri... uris) {

            final long currentMillis = System.currentTimeMillis();
            final String handleExtension = getPathImg(uris[0]);
            UploadTask uploadTask = storageReference.child(UidUser+"_imageUserOwn")
                    .child("IMG_"+currentMillis+'.'+handleExtension)
                    .putFile(uris[0]);



            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setContentView(R.layout.progress_dialog);
                    final TextView txtViewThis = progressDialog.findViewById(R.id.loadings);
                    txtViewThis.setText("Downloading the image...");
                    storageReference.child(UidUser+"_imageUserOwn")
                            .child("IMG_"+currentMillis+'.'+handleExtension).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uriss = uri;
                            txtViewThis.setText("Processing...");

                            new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    databaseRef.child(UidUser+"_infoSearchAndAdd")
                                            .child("image")
                                            .setValue(uriss.toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<Void>(){
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    txtViewThis.setText("Updating image from database...");

                                                    if(task.isSuccessful()){
                                                        databaseReferece.child(UidUser)
                                                                .child("imageUsers")
                                                                .setValue(uriss.toString().trim())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>(){
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> tasks) {
                                                                        if(tasks.isSuccessful()) {
                                                                            String stTime = dateFormat.format(calendar.getTime()), handleDataDateFinal = "";
                                                                            char[] charConvert = stTime.toCharArray();

                                                                            for(int numberCount = 0;numberCount < charConvert.length;numberCount++){
                                                                                if(!String.valueOf(charConvert[numberCount]).equals(" ")) {
                                                                                    handleDataDateFinal += charConvert[numberCount];
                                                                                }else{
                                                                                    break;
                                                                                }
                                                                            }

                                                                            HandlingDatasClass classData = new HandlingDatasClass();
                                                                            classData.DateAccountPublish = handleDataDateFinal;
                                                                            classData.image = uriss.toString().trim();
                                                                            String randomId = databaseReferece.push().getKey();
                                                                            databaseReferece.child(UidUser)
                                                                                    .child(UidUser + "_UserMultiTaskDatasInformation")
                                                                                    .child("imageUser")
                                                                                    .child(randomId).setValue(classData)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> taskss) {
                                                                                            if (taskss.isSuccessful()) {
                                                                                                txtViewThis.setText("Finalizing...");

                                                                                                Picasso.with(getActivity()).load(uris[0]).fit().centerCrop().into(imgageUserFinal);
                                                                                                new Handler().postDelayed(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        progressDialog.dismiss();
                                                                                                    }
                                                                                                }, 2000);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }else{
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getContext(), "Failed to upload your picture. Check your connection.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }, 2000);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double numberProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    int progressNumber = (int)numberProgress;
                    txtViewProgressBarSaveImage.setText((progressNumber <= 9 ?
                            (progressNumber != 0 ? "0"+progressNumber+"%":"0%"):progressNumber+"%"));
                    progressBarImageSave.setProgress((int)numberProgress);
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage().trim(), Toast.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }


    //AsyncTask END_____________________________________________________




    //Get the Extension of IMG_______________________________________________________
    private String getPathImg(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mime = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return mime;
    }






    //click to open to select run the fileOpenFunction_____________________________________________________________
    private View.OnClickListener clickCreateNewImage = new View.OnClickListener(){
        public void onClick(View views){
            conditionChooseFileProgressAndInformation = true;
            InformationUser.this.fileOpenFunction();
        }
    };

    //open for select image________________________________________________
    private void fileOpenFunction(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, image_request);
    }


    ImageView imageShow;
    private void loadProgressBar(){
        try{
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.image_user_select_create);
            progressBarImageSave = progressDialog.findViewById(R.id.progressBar2);
            linearImageUpload = progressDialog.findViewById(R.id.uploadLinearImage);
            txtViewProgressBarSaveImage = progressDialog.findViewById(R.id.progress_Percent);
            imageShow = progressDialog.findViewById(R.id.imageSelectedjar);
            Button bttnClickDecline = progressDialog.findViewById(R.id.bttn_declinePicture),
                    bttnClickSave = progressDialog.findViewById(R.id.bttn_savePicture),
                    bttnClickChooseFile = progressDialog.findViewById(R.id.chooseFile);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            //decline bttn___________________________________________________
            bttnClickDecline.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });


            //save picture bttn_______________________________________
            bttnClickSave.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(onConnectWifi()) {
                        LinearLayout linearLayoutProgressBar = progressDialog.findViewById(R.id.progressDownloads),
                                linearLayoutSaveAndDecline = progressDialog.findViewById(R.id.bttnSaveAndDecline);

                        linearLayoutProgressBar.setVisibility(View.VISIBLE);
                        linearLayoutSaveAndDecline.setVisibility(View.INVISIBLE);
                        new savingImageDataAndUpdateImageUser().execute(dataImage);
                    }else{
                        Toast.makeText(getContext(), "Check your connection...", Toast.LENGTH_LONG).show();
                    }
                }
            });


            //choose file bttn___________________________________________________________________________
            bttnClickChooseFile.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    conditionChooseFileProgressAndInformation = false;
                    InformationUser.this.fileOpenFunction();
                }
            });
        }catch(Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }






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




    //Runnable time connection____________________________________________________________________________________
    private Runnable runTimerCheckConnections = new Runnable(){
        @Override
        public void run() {
            if(conditionToConnection) {
                if (onConnectWifi()) {
                    conditionToConnection = false;
                    progressBar.setVisibility(View.VISIBLE);
                    textViewCantLoad.setVisibility(View.INVISIBLE);
                    new gettingInformationUser().execute();
                }else{
                    handleArea.postDelayed(runTimerCheckConnections, 5000);
                }
            }
        }
    };


    //OVERRIDES______________________________________________________________________________________

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == image_request && data != null && data.getData() != null){
            dataImage = data.getData();

            if(!conditionChooseFileProgressAndInformation){
                progressDialog.dismiss();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InformationUser.this.loadProgressBar();
                    Picasso.with(getActivity()).load(dataImage).fit().centerCrop().into(imageShow);
                }
            }, 300);
        }
    }


    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        new gettingInformationUser().execute();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        new gettingInformationUser().execute();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }





}
