package com.example.chatportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

import static androidx.core.content.ContextCompat.getSystemService;

public class ChatSignUp extends AppCompatActivity {


    private EditText FirstName, LastName, Username, EmailSignUp, PasswordSignUp, PasswordReT;
    private Button bttnSignUp;
    private boolean conditionBttn = true;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressBar;
    private String[][] arrayS;
    private boolean boolConditionNullText, boolConditionDataFirstEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_sign_up);

        firebaseAuth  = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        bttnSignUp = (Button) findViewById(R.id.SignUpBttn);
        bttnSignUp.setOnClickListener(listenerSignUp);


        FirstName = (EditText) findViewById(R.id.FirstName);
        LastName = (EditText)findViewById(R.id.LastName);
        Username = (EditText) findViewById(R.id.Username);
        EmailSignUp = (EditText)findViewById(R.id.EmailSignUp);
        PasswordSignUp = (EditText)findViewById(R.id.PasswordSignUp);
        PasswordReT = (EditText)findViewById(R.id.PasswordReT);

    }

    //Click Sign up click___________________________________________________________
    private View.OnClickListener listenerSignUp = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            boolConditionNullText = true; boolConditionDataFirstEmail = false;
            String handlePassword = "", handleLastStringEmail = "";
            int numberCountIfDuplicateInEmail = 0;
            arrayS = new String[][]{ { "FirstName", FirstName.getText().toString() }, {"LastName", LastName.getText().toString()},
                    {"Username", Username.getText().toString()}, {"Email", EmailSignUp.getText().toString()},
                    {"Password", PasswordSignUp.getText().toString()}, {"PasswordRT", PasswordReT.getText().toString()} };

            for(int numberCount = 0;numberCount < arrayS.length;numberCount++){
                if(boolConditionNullText){
                    if(arrayS[numberCount][1].length() == 0){
                        boolConditionNullText = false;
                        Toast.makeText(getApplicationContext(), "Fill up the "+arrayS[numberCount][0], Toast.LENGTH_LONG).show();
                    }else{
                        if(numberCount == 3){
                            for(int numberCountEmail = 0;numberCountEmail < arrayS[numberCount][1].length();numberCountEmail++){
                                if((arrayS[numberCount][1].toCharArray())[numberCountEmail] == '@' || (arrayS[numberCount][1].toCharArray())[numberCountEmail] == '.'){
                                    numberCountIfDuplicateInEmail++;
                                }

                                if(!boolConditionDataFirstEmail) {
                                    if ((arrayS[numberCount][1].toCharArray())[numberCountEmail] == '@') {
                                        handleLastStringEmail = "@";
                                        boolConditionDataFirstEmail = true;
                                    }
                                }else{
                                    handleLastStringEmail += (arrayS[numberCount][1].toCharArray())[numberCountEmail];
                                }
                            }

                            if(numberCountIfDuplicateInEmail == 2){
                                if(!handleLastStringEmail.equals("@gmail.com")) {
                                    boolConditionNullText = false;
                                    Toast.makeText(getApplicationContext(), "Please check your "+arrayS[numberCount][0]+" carefully.", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                boolConditionNullText = false;
                                Toast.makeText(getApplicationContext(), "Please check your "+arrayS[numberCount][0]+" carefully.", Toast.LENGTH_LONG).show();
                            }

                        }else if(numberCount == 4){
                            handlePassword = arrayS[numberCount][1];
                        }else if(numberCount == 5){
                            if(arrayS[numberCount][1].equals(handlePassword)){
                                if(handlePassword.length() < 8){
                                    boolConditionNullText = false;
                                    Toast.makeText(getApplicationContext(), "The length of Password must have 8 above characters.", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                boolConditionNullText = false;
                                Toast.makeText(getApplicationContext(), "The Password and the PasswordRT must be same.", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }

                if(numberCount+1 >= arrayS.length){
                    if(boolConditionNullText){
                        boolean connection = onConnectWifi();
                        if(connection) {
                            new ChatSignUp.SavingAndCheckingDatas().execute();
                        }else{
                            Toast.makeText(getApplicationContext(), "Check your connection.", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
        }
    };






    public class SavingAndCheckingDatas extends AsyncTask<Void, Void, Void> {


        private boolean conditionHasInDatabase;
        HandlingDatasClass dataClass;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(ChatSignUp.this);
            progressBar.show();
            progressBar.setCancelable(false);
            progressBar.setContentView(R.layout.progress_dialog);
            progressBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            databaseReference.child("ChatPortalUser").addListenerForSingleValueEvent(new ValueEventListener(){
                public void onDataChange(DataSnapshot snap){
                    conditionHasInDatabase = true;
                    for(DataSnapshot snapGet: snap.getChildren()){
                        if(snapGet.child("Email").getValue(String.class).equals(arrayS[3][1])){
                            Toast.makeText(getApplicationContext(), "Your Email had in Database", Toast.LENGTH_LONG).show();
                            conditionHasInDatabase = false;
                            break;
                        }else if(snapGet.child("Username").getValue(String.class).equals(arrayS[2][1])){
                            Toast.makeText(getApplicationContext(), "Your Username had in Database", Toast.LENGTH_LONG).show();
                            conditionHasInDatabase = false;
                            break;
                        }
                    }


                    if(conditionHasInDatabase){
                        Calendar calendar = Calendar.getInstance();
                        DateFormat date = DateFormat.getInstance();

                        dataClass = new HandlingDatasClass();
                        dataClass.FirstName = arrayS[0][1];
                        dataClass.LastName = arrayS[1][1];
                        dataClass.Username = arrayS[2][1];
                        dataClass.Email = arrayS[3][1];
                        dataClass.Password = arrayS[4][1];
                        dataClass.FirstNameAndLastName =  dataClass.FirstName+" "+dataClass.LastName;
                        dataClass.DateAccountPublish = date.format(calendar.getTime());

                        firebaseAuth.createUserWithEmailAndPassword(dataClass.Email, dataClass.Password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                    @Override
                                    public void onComplete(Task<AuthResult> task){
                                        if(task.isSuccessful()){
                                            dataClass.HashPasswordUser = firebaseAuth.getUid();

                                            HandlingDatasClass classDatasNewForChatPortalUser = new HandlingDatasClass();
                                            classDatasNewForChatPortalUser.Email = dataClass.Email;
                                            classDatasNewForChatPortalUser.Username = dataClass.Username;
                                            classDatasNewForChatPortalUser.FirstNameAndLastName = dataClass.FirstNameAndLastName;
                                            classDatasNewForChatPortalUser.HashPasswordUser = dataClass.HashPasswordUser;
                                            classDatasNewForChatPortalUser.image = "https://firebasestorage.googleapis.com/v0/b/chatconversationfirebase.appspot.com/o/image_user%2FuserImageForAllFirst%2FFB_IMG_1580009626444.jpg?alt=media&token=31ca5155-e541-457e-b2a4-8f732ae71571";

                                            databaseReference.child("ChatPortalUser").child(""+firebaseAuth.getUid()+"_infoSearchAndAdd")
                                                    .setValue(classDatasNewForChatPortalUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>(){
                                                        @Override
                                                        public void onComplete(Task<Void> task){
                                                            if(task.isSuccessful()){
                                                                databaseReference.child("MemberChatPortalInfoAccount")
                                                                        .child(firebaseAuth.getUid())
                                                                        .child(firebaseAuth.getUid()+"_informationUser")
                                                                        .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                                    @Override
                                                                    public void onComplete(Task<Void> task){
                                                                        if(!task.isSuccessful()){
                                                                            Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_LONG).show();
                                                                        }else{
                                                                            databaseReference.child("MemberChatPortalInfoAccount")
                                                                                    .child(firebaseAuth.getUid())
                                                                                    .child("imageUsers").setValue("https://firebasestorage.googleapis.com/v0/b/chatconversationfirebase.appspot.com/o/image_user%2FuserImageForAllFirst%2FFB_IMG_1580009626444.jpg?alt=media&token=31ca5155-e541-457e-b2a4-8f732ae71571")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                firebaseAuth.signOut();
                                                                                                ChatSignUp.this.finish();
                                                                                                progressBar.dismiss();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                            }else{
                                                                Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    }else{
                        progressBar.dismiss();
                    }
                }

                public void onCancelled(DatabaseError err) {
                    Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_LONG).show();
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }



    //Checking if the wifi or mobile data is connected________________________________________
    public boolean onConnectWifi(){
        boolean connection = false;
        ConnectivityManager connectTivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
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
