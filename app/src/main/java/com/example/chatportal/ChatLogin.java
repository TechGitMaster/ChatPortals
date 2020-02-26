package com.example.chatportal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;


public class ChatLogin extends Fragment implements OnCompleteListener<AuthResult> {

    private Button bttn;
    private boolean conditionBttn = true;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressBar;

    private View vChatLoginAccessLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vChatLoginAccessLayout = inflater.inflate(R.layout.fragment_chat_login, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        bttn = (Button) vChatLoginAccessLayout.findViewById(R.id.bttnSignIn);
        bttn.setOnClickListener(listenerClick);
        TextView txtV = (TextView) vChatLoginAccessLayout.findViewById(R.id.SignUpTxtView);
        txtV.setOnClickListener(listenerClick);

        return vChatLoginAccessLayout;
    }




    //Click sign in_______________________________

    private View.OnClickListener listenerClick = new View.OnClickListener(){
        public void onClick(View v) {
            if (v.getContentDescription().toString().equals("SignIn")) {
                if (conditionBttn) {
                    conditionBttn = false;
                    EditText txtE = (EditText) vChatLoginAccessLayout.findViewById(R.id.EmailSignIn);
                    EditText txtP = (EditText) vChatLoginAccessLayout.findViewById(R.id.PasswordSignIn);

                    if (txtE.getText().toString().length() != 0 && txtP.getText().toString().length() != 0) {
                        if(onConnectWifi()) {
                            progressBar = new ProgressDialog(getContext());
                            progressBar.show();
                            progressBar.setCancelable(false);
                            progressBar.setContentView(R.layout.progress_dialog);
                            progressBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            firebaseAuth.signInWithEmailAndPassword(txtE.getText().toString(), txtP.getText().toString()).addOnCompleteListener(ChatLogin.this);
                        }else{
                            Toast.makeText(getContext(), "Check your connection.", Toast.LENGTH_LONG).show();
                            conditionBttn = true;
                        }
                    } else {
                        if (txtE.getText().toString().length() == 0 && txtP.getText().toString().length() == 0) {
                            Toast.makeText(getContext(), "Empty the Email and Password text", Toast.LENGTH_LONG).show();
                            conditionBttn = true;
                        } else {
                            if (txtE.getText().toString().length() == 0) {
                                Toast.makeText(getContext(), "Empty the Email text", Toast.LENGTH_LONG).show();
                                conditionBttn = true;
                            } else {
                                Toast.makeText(getContext(), "Empty the Password text", Toast.LENGTH_LONG).show();
                                conditionBttn = true;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Wait for a while.", Toast.LENGTH_LONG).show();
                }
            }else if(v.getContentDescription().toString().equals("SignUp")){
                startActivity(new Intent(getActivity(), ChatSignUp.class));
            }
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


    //Complete Task SignIn Check_____________________________________________________
    @Override
    public void onComplete(Task<AuthResult> task){
        conditionBttn = true;
        progressBar.dismiss();
        if(task.isSuccessful()){
            Fragment homeUser = new HomeUser();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragments, homeUser);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }else{
            Toast.makeText(getContext(), "The email or password is badly formatted", Toast.LENGTH_LONG).show();
        }
    }
}
