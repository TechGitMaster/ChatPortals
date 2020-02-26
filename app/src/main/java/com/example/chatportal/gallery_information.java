package com.example.chatportal;



import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class gallery_information extends AppCompatActivity{

    private DatabaseReference dataRef;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Button monthBttn, yearBttn;
    private List<HandlingDatasClass> dataClassImageHandle;
    private HorizontalScrollView scrollViewMonth, scrollViewYear;
    private LinearLayout linearLayoutYear, linearLayoutMonth, layoutJarDayImage, layoutLinearDayDay;
    private String handleBttnClickMonthAndYear;
    private boolean conditionToFetchImage, leapYear;
    private List<List<HandlingDatasClass>> classDataGetImageByYear, classDataGetImageByMonth;
    private String[] listOfDateMonth, listOfDateMonthNumber;
    private Integer[] intMonthNumber;
    private List<String> stringYear, arrayListMonth, ImageSelectedByDayMonth;
    private TextView txtMonthDate, txtYearDate;
    private Integer intHandleArrayYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_informations);

        //databases listen__________________________________________________
        dataRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //getting the imageAllUser______________________________________________________
        dataClassImageHandle = this.dataGetImageCloud(dataRef, firebaseAuth, getApplicationContext());


        conditionToFetchImage = true;
        leapYear = false;
        intHandleArrayYear = 0;
        handleBttnClickMonthAndYear = "monthClicked";
        intMonthNumber = new Integer[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        listOfDateMonthNumber = new String[]{ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        listOfDateMonth = new String[]{ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
        "November", "December" };

        //Bttn month and year__________________________________________________
        monthBttn = findViewById(R.id.monthBttn);
        yearBttn =  findViewById(R.id.yearBttn);
        monthBttn.setOnClickListener(clickListenerBttnYearMonth);
        yearBttn.setOnClickListener(clickListenerBttnYearMonth);
        txtMonthDate = (TextView)this.findViewById(R.id.MonthText);
        txtYearDate = (TextView)this.findViewById(R.id.YearText);

        //ScrollView month and year___________________________________________
        scrollViewMonth = this.findViewById(R.id.month_scrollBar);
        scrollViewYear = this.findViewById(R.id.year_scrollBar);

        //LinearLayout month, year and day________________________________________
        linearLayoutYear = this.findViewById(R.id.yearLinearLayout);
        linearLayoutMonth = this.findViewById(R.id.monthLinearLayout);
        layoutJarDayImage = this.findViewById(R.id.layoutJarDayImage);
    }



    //Button of month and year combined___________________________________________________________________________
    private View.OnClickListener clickListenerBttnYearMonth = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getContentDescription().toString().equals("monthDescription")){
                if(handleBttnClickMonthAndYear != "monthClicked"){
                    handleBttnClickMonthAndYear = "monthClicked";
                    monthBttn.setBackgroundResource(R.color.colorMonthAndYearBttnClicked);
                    yearBttn.setBackgroundResource(R.color.colorMonthAndYearBttnUnClicked);
                    scrollViewMonth.setVisibility(View.VISIBLE);
                    scrollViewYear.setVisibility(View.INVISIBLE);
                }
            }else if(v.getContentDescription().toString().equals("yearDescription")){
                if(handleBttnClickMonthAndYear != "yearClicked"){
                    handleBttnClickMonthAndYear = "yearClicked";
                    monthBttn.setBackgroundResource(R.color.colorMonthAndYearBttnUnClicked);
                    yearBttn.setBackgroundResource(R.color.colorMonthAndYearBttnClicked);
                    scrollViewYear.setVisibility(View.VISIBLE);
                    scrollViewMonth.setVisibility(View.INVISIBLE);
                }
            }
        }
    };



    //Get all image of user in firebase_______________________________________________________________
    public List<HandlingDatasClass> dataGetImageCloud(DatabaseReference dataReference, FirebaseAuth firebaseAuths, final Context context){
        final List<HandlingDatasClass> dataClassAccess = new ArrayList<HandlingDatasClass>();

        DatabaseReference databases = dataReference.child("MemberChatPortalInfoAccount")
                .child(firebaseAuths.getUid())
                .child(firebaseAuths.getUid()+"_UserMultiTaskDatasInformation")
                .child("imageUser");

        databases.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numberCount = 0;
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    HandlingDatasClass handlingA = new HandlingDatasClass();
                    handlingA.image = snap.child("image").getValue(String.class);
                    handlingA.DateAccountPublish = snap.child("DateAccountPublish").getValue(String.class);

                    dataClassAccess.add(handlingA);

                    numberCount++;
                    if(numberCount >= dataSnapshot.getChildrenCount()){
                        if(conditionToFetchImage) {
                            conditionToFetchImage = false;
                            gallery_information.this.flipperView(dataClassAccess);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Error_________________________________________________________
            }
        });

        return dataClassAccess;
    }




    //show the image in ViewFlipper____________________________________________________________
    private void flipperView(List<HandlingDatasClass> listHandleImageClass){
        ViewFlipper flipperView = this.findViewById(R.id.flipperView);

        for(HandlingDatasClass list: listHandleImageClass){
            ImageView image = new ImageView(this);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            flipperView.addView(image);
            Picasso.with(this).load(list.image).fit().centerCrop().into(image);

        }


        flipperView.setFlipInterval(4000);
        flipperView.startFlipping();
        flipperView.setAutoStart(true);
        flipperView.setInAnimation(this, android.R.anim.slide_in_left);
        flipperView.setOutAnimation(this, android.R.anim.slide_out_right);

        this.MonthAndYear(listHandleImageClass);
        return;
    }


    //Seperating the year per year_________________________________________________
    private void MonthAndYear(List<HandlingDatasClass> listHandleImageClass){
        stringYear = new ArrayList<>();
        classDataGetImageByYear = new ArrayList<>();

        //try soon will be deleted_____________________________________________________________
        HandlingDatasClass classDatass = new HandlingDatasClass();
        classDatass.image = listHandleImageClass.get(0).image;
        classDatass.DateAccountPublish =  "23/12/2021";
        listHandleImageClass.add(classDatass);

        HandlingDatasClass classDatasss = new HandlingDatasClass();
        classDatasss.image = listHandleImageClass.get(1).image;
        classDatasss.DateAccountPublish =  "23/01/2022";
        listHandleImageClass.add(classDatasss);

        HandlingDatasClass classDatassss = new HandlingDatasClass();
        classDatassss.image = listHandleImageClass.get(1).image;
        classDatassss.DateAccountPublish =  "23/02/2022";
        listHandleImageClass.add(classDatassss);
        //____________________________________________________________________________________


        linearLayoutYear.removeAllViews();

        int numberCountDoneGetDateYear = 0;
        for(HandlingDatasClass dataImage: listHandleImageClass){
            if(stringYear.toArray().length == 0){
                char[] charConvert = dataImage.DateAccountPublish.toCharArray();
                String stDateYear = "";
                for(int numberCountImageDate = charConvert.length-4;numberCountImageDate < charConvert.length;numberCountImageDate++){
                    stDateYear += String.valueOf(charConvert[numberCountImageDate]);
                }
                stringYear.add(stDateYear);
            }else{
                char[] charConvert = dataImage.DateAccountPublish.toCharArray();
                String stDateYear = "";
                for(int numberCountImageDate = charConvert.length-4;numberCountImageDate < charConvert.length;numberCountImageDate++){
                    stDateYear += String.valueOf(charConvert[numberCountImageDate]);

                    if(numberCountImageDate+1 >= dataImage.DateAccountPublish.length()){
                        Object[] objectConvert = stringYear.toArray();
                        boolean conditionCheck = true;
                        for(int numberCountScanner = 0;numberCountScanner < objectConvert.length;numberCountScanner++){
                            String st = (String)objectConvert[numberCountScanner];
                            if(st.equals(stDateYear)){
                                conditionCheck = false;
                            }

                            if(numberCountScanner+1 >= objectConvert.length){
                                if(conditionCheck){
                                    stringYear.add(stDateYear);
                                }
                            }
                        }
                    }
                }
            }

            numberCountDoneGetDateYear++;
            if(numberCountDoneGetDateYear >= listHandleImageClass.toArray().length){
                int numberCountDoneGetByYearImage = 0;
              //  Toast.makeText(getApplicationContext(), String.valueOf(stringYear.size()), Toast.LENGTH_SHORT).show();
                for(String Year: stringYear){
                    List<HandlingDatasClass> handleGetImage = new ArrayList<>();
                    for(HandlingDatasClass classDataImage:listHandleImageClass){
                        char[] charConvert = classDataImage.DateAccountPublish.toCharArray();
                        String stDateYear = "";
                        for(int numberCountImageDate = charConvert.length-4;numberCountImageDate < charConvert.length;numberCountImageDate++){
                            stDateYear += String.valueOf(charConvert[numberCountImageDate]);
                        }


                        if(stDateYear.equals(Year)){
                            handleGetImage.add(classDataImage);
                        }
                    }
                    classDataGetImageByYear.add(handleGetImage);
                    numberCountDoneGetByYearImage++;
                    if(numberCountDoneGetByYearImage >= stringYear.size()){
                       // Toast.makeText(getApplicationContext(), String.valueOf(classDataGetImageByYear.size()), Toast.LENGTH_SHORT).show();
                        this.showTheCardView(stringYear, stringYear.size()-1, "year");
                    }
                }
            }
        }
    }



    //Show the controllers of year and month___________________________________________________________________
    private void showTheCardView(final List<String> stringYearMonth, int numberForList, String yearAndMonthCondition){
        String stHandleDate = "";

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(325, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins((numberForList == 0 ? 3:0), 3,7, 3);
        cardView.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams linearLayoutImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(linearLayoutImage);
        imageView.setElevation(0);


        if(yearAndMonthCondition.equals("month")){
            for(int numberCountDate = 0;numberCountDate < listOfDateMonth.length;numberCountDate+=1){
                if(stringYearMonth.get(numberForList).equals(listOfDateMonthNumber[numberCountDate])){
                    stHandleDate = listOfDateMonth[numberCountDate];
                }
            }
        }


        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setBackgroundResource(R.color.colorImageYearAndMonthOpacity);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setElevation(10);
        textView.setGravity(Gravity.CENTER);
        textView.setText((yearAndMonthCondition.equals("year") ? stringYearMonth.get(numberForList):stHandleDate));
        textView.setTextSize(17);
        textView.setContentDescription(""+numberForList+","+yearAndMonthCondition);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int numberForArrays = 0;
                String stForMonthOrYear = "year";
                char[] charres = v.getContentDescription().toString().toCharArray();
                String sts = "";
                for(int numberCountss = 0;charres.length > numberCountss;numberCountss++){
                    if(!String.valueOf(charres[numberCountss]).equals(",")){
                        sts += String.valueOf(charres[numberCountss]);
                    }else{
                        numberForArrays = Integer.parseInt(sts);
                        sts = "";
                    }

                    if(numberCountss+1 >= charres.length){

                        stForMonthOrYear = sts;
                        if(stForMonthOrYear.equals("year")){
                            leapYear = false;
                            this.leapYearCheck(numberForArrays);

                            txtYearDate.setText("| "+stringYearMonth.get(numberForArrays));
                             gallery_information.this.monthSeperating(numberForArrays);
                        }else if(stForMonthOrYear.equals("month")){
                            gallery_information.this.Day_per_Day_picture(numberForArrays);
                        }
                    }
                }
            }


            private void leapYearCheck(int numberForArrays){
                //Leap year____________________________________________________
                boolean conditionToWhile = true;
                int numberYear = 1996;
                while(conditionToWhile){
                    numberYear += 4;


                    if(numberYear >= Integer.parseInt(stringYearMonth.get(numberForArrays))){
                        conditionToWhile = false;
                        if(numberYear == Integer.parseInt(stringYearMonth.get(numberForArrays))){
                            leapYear = true;
                        }
                    }
                }
            }
        });


        cardView.addView(imageView);
        cardView.addView(textView);

        Random random = new Random();
        if(yearAndMonthCondition.equals("year")) {
            linearLayoutYear.addView(cardView);
            List<HandlingDatasClass> handlingDataImage = classDataGetImageByYear.get(numberForList);
            int randomFinal = random.nextInt((handlingDataImage.size() != 1 ? handlingDataImage.size() - 1 : handlingDataImage.size()));
            HandlingDatasClass randomFinalClass = handlingDataImage.get(randomFinal);
            Picasso.with(this).load(randomFinalClass.image).fit().centerCrop().into(imageView);
        }else{
            linearLayoutMonth.addView(cardView);
            List<HandlingDatasClass> handlingImageMonth = classDataGetImageByMonth.get(numberForList);
            int numberRandomFinal = random.nextInt((handlingImageMonth.toArray().length != 1 ? handlingImageMonth.size()-1:handlingImageMonth.size()));
            HandlingDatasClass dataClassGetImageMonthFinal = handlingImageMonth.get(numberRandomFinal);
            Picasso.with(this).load(dataClassGetImageMonthFinal.image).fit().centerCrop().into(imageView);
        }

        if(numberForList != 0){
            numberForList -= 1;
            this.showTheCardView(stringYearMonth, numberForList, yearAndMonthCondition);
        }else{
            if(yearAndMonthCondition.equals("year")) {

                //Leap year____________________________________________________
                boolean conditionToWhile = true;
                int numberYear = 1996;
                while(conditionToWhile){
                    numberYear += 4;


                    if(numberYear >= Integer.parseInt(stringYearMonth.get(stringYearMonth.toArray().length - 1))){
                        conditionToWhile = false;
                        if(numberYear == Integer.parseInt(stringYearMonth.get(stringYearMonth.size() - 1))){
                            leapYear = true;
                        }
                    }
                }
                Object[] stringMonth = stringYearMonth.toArray();
                txtYearDate.setText("| "+stringMonth[stringYearMonth.toArray().length-1].toString());

                this.monthSeperating(classDataGetImageByYear.toArray().length - 1);
            }else{
                this.Day_per_Day_picture(classDataGetImageByMonth.toArray().length-1);
            }
        }
    }


    //Seperating month for month_____________________________________________________________________
    private void monthSeperating(int numberForList){
        List<HandlingDatasClass> handlingClassGetMax = classDataGetImageByYear.get(numberForList);
        arrayListMonth = new ArrayList<>();
        classDataGetImageByMonth = new ArrayList<>();
        int numberCountForDoneScanning = 0;

        linearLayoutMonth.removeAllViews();

        for(HandlingDatasClass classDataImage:handlingClassGetMax){
            if(arrayListMonth.toArray().length == 0) {
                char[] stDate = classDataImage.DateAccountPublish.toCharArray();
                String st = "";
                for (int numberCount = stDate.length - 7; numberCount < stDate.length - 5; numberCount++) {
                    st += String.valueOf(stDate[numberCount]);
                }
                arrayListMonth.add(st);
            }else{
                String st = "";
                boolean boolConditionScanning = true;
                int numbeberCountListMonth = 0;

                char[] stDate = classDataImage.DateAccountPublish.toCharArray();
                for (int numberCount = stDate.length - 7; numberCount < stDate.length - 5; numberCount++) {
                    st += String.valueOf(stDate[numberCount]);
                }

                for(String handleDateList: arrayListMonth){
                    if(handleDateList.equals(st)){
                        boolConditionScanning = false;
                    }

                    numbeberCountListMonth++;
                    if(numbeberCountListMonth >= arrayListMonth.toArray().length){
                        if(boolConditionScanning){
                            arrayListMonth.add(st);
                        }
                    }
                }
            }

            if(numberCountForDoneScanning >= handlingClassGetMax.size()-1){
                int numberCountForDoneScanningMonthImage = 0;
                for(String handleDateMonth:arrayListMonth){
                    List<HandlingDatasClass> datasTemporaryDataStoreImage = new ArrayList<>();
                    for(HandlingDatasClass datasClassImage:handlingClassGetMax){
                        char[] stDate = datasClassImage.DateAccountPublish.toCharArray();
                        String st = "";
                        for (int numberCount = stDate.length - 7; numberCount < stDate.length - 5; numberCount++) {
                            st += String.valueOf(stDate[numberCount]);
                        }

                        if(handleDateMonth.equals(st)){
                            datasTemporaryDataStoreImage.add(datasClassImage);
                        }
                    }

                    classDataGetImageByMonth.add(datasTemporaryDataStoreImage);
                    numberCountForDoneScanningMonthImage++;
                    if(numberCountForDoneScanningMonthImage >= arrayListMonth.toArray().length){
                        this.showTheCardView(arrayListMonth, arrayListMonth.toArray().length-1, "month");
                    }
                }

            }
            numberCountForDoneScanning++;
        }
    }



    //Showing the day per day picture_____________________________________________________
    private void Day_per_Day_picture(int numberCountForMonth){
        String handleMonth = "";
        intHandleArrayYear = numberCountForMonth;
        int numberHandleMonthDay = 0;
        if(leapYear){
            intMonthNumber[1] = 29;
        }else{
            intMonthNumber[1] = 28;
        }

        for(int numberCountMonth = 0;numberCountMonth <= listOfDateMonthNumber.length-1;numberCountMonth++){
            if(listOfDateMonthNumber[numberCountMonth].equals(arrayListMonth.get(numberCountForMonth))){
                handleMonth = listOfDateMonth[numberCountMonth];
                numberHandleMonthDay = numberCountMonth;
            }

            if(numberCountMonth >= listOfDateMonthNumber.length-1){
               txtMonthDate.setText(handleMonth+" ");

                layoutJarDayImage.removeAllViews();
               this.showTheDay( intMonthNumber[numberHandleMonthDay], classDataGetImageByMonth.get(numberCountForMonth), 1, 8);
            }
        }

    }



    private void showTheDay(int lengthOfMonth, List<HandlingDatasClass> handleDataClass, int numberCountMonth, int numberForAddView){

        String handleNumber = (numberCountMonth <= 9 ? "0"+numberCountMonth:""+numberCountMonth);
        if(numberForAddView > 7) {
            numberForAddView = 1;
            layoutLinearDayDay = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutLinearDayDay.setLayoutParams(layoutParams);
            layoutLinearDayDay.setPadding(5, 5, 5, 5);
            layoutLinearDayDay.setOrientation(LinearLayout.HORIZONTAL);
            layoutLinearDayDay.setGravity(Gravity.CENTER);
        }

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParamsCardView = new LinearLayout.LayoutParams(130, 130);
        layoutParamsCardView.setMargins(5, 5, 5, 5);
        cardView.setLayoutParams(layoutParamsCardView);


        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(21);
        textView.setText(String.valueOf(numberCountMonth));


        Object[] obj = handleDataClass.toArray();
        int numberCounts = 0;
        for(int numberCount = 0;numberCount < obj.length;numberCount++){
            HandlingDatasClass classes = (HandlingDatasClass)obj[numberCount];
            char[] chars = classes.DateAccountPublish.toCharArray();
            String st = "";
            for(int numbersCount = 0;numbersCount < chars.length-8;numbersCount++){
                st += ""+chars[numbersCount];
            }

            if(st.equals(handleNumber)){
                numberCounts++;
            }

            if(numberCount+2 > obj.length){
                if(numberCounts >= 1){
                    if(numberCounts == 1){
                        textView.setBackgroundResource(R.color.picture1HaveInOneDay);
                    }else{
                        textView.setBackgroundResource(R.color.pictureHaveMultipleOneDay);
                    }

                    textView.setContentDescription(handleNumber);
                    textView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ImageSelectedByDayMonth = new ArrayList<>();
                            List<HandlingDatasClass> dataClass = classDataGetImageByMonth.get(intHandleArrayYear);
                            Object[] arr = dataClass.toArray();
                            for(int numberCount = 0;numberCount < arr.length;numberCount++){
                                HandlingDatasClass handlingClass = (HandlingDatasClass)arr[numberCount];
                                char[] chars = handlingClass.DateAccountPublish.toCharArray();
                                String st = "";
                                for(int numberCounts = 0; numberCounts < chars.length-8;numberCounts++){
                                    st += chars[numberCounts];
                                }

                                if(v.getContentDescription().toString().equals(st)){
                                    Toast.makeText(getApplicationContext(), handlingClass.image, Toast.LENGTH_LONG).show();
                                    ImageSelectedByDayMonth.add(handlingClass.image);
                                }
                            }
                        }
                    });
                }else{
                    textView.setBackgroundResource(android.R.color.white);
                }
            }
        }

        cardView.addView(textView);

        layoutLinearDayDay.addView(cardView);

        numberCountMonth++;
        if(numberCountMonth <= lengthOfMonth) {
            if (numberForAddView == 7) {
                layoutJarDayImage.addView(layoutLinearDayDay);
                numberForAddView += 1;
                this.showTheDay(lengthOfMonth, handleDataClass, numberCountMonth, numberForAddView);
            } else {
                numberForAddView += 1;
                this.showTheDay(lengthOfMonth, handleDataClass, numberCountMonth, numberForAddView);
            }
        }else{
            layoutJarDayImage.addView(layoutLinearDayDay);
        }
    }




}
