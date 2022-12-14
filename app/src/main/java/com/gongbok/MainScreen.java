package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainScreen extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            moveTaskToBack(true); // ???????????? ?????????????????? ??????
            finishAndRemoveTask(); // ???????????? ?????? + ????????? ??????????????? ?????????
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    private final String TAG = "MainScreen";
    class ACProblem{
        String titleName;
        Long ACount;
        ACProblem(String titleName, Long ACount){
            this.titleName = titleName;
            this.ACount = ACount;
        }

    }
    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        Intent solvedDataIntent = new Intent(this, SolvedProblemScreen.class);
        // ?????? ????????? ?????? firebase ?????? ????????? ?????? ?????????
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Firebase ??????
        db = FirebaseFirestore.getInstance();

        if(user != null){
            userUid = user.getUid();
            Log.d(TAG, userUid);
        }

        //?????? userID
        DocumentReference docRef = db.collection("??????UID")
                .document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userID = document.getString("?????????");
                        Log.d(TAG, "???????????? ????????? ?????????1 : " + userID);
                        settingUser();
                    }
                }
            }
        });
    }
    
    public void logOut(View v) {
        // ?????????/???????????? ????????? ????????????
        firebaseAuth.signOut();
        // ?????? ????????? ????????????
        setResult(RESULT_OK);
        finish();
    }

    // firebase UUID ??????????????? ????????? ??????????????? ?????? ?????? ??????
    public void settingUser(){
        Log.d(TAG, "???????????? ????????? ?????????2 : " + userID);
        //?????? userID Intent ???????????? ??????
        db.collection("??????")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //????????? ????????? ??????
                            Map<String, Object> fieldValues = task.getResult().getData();
                            Long rate = (Long) fieldValues.get("?????????");
                            TextView ratingValue = (TextView) findViewById(R.id.ratingValue);
                            ratingValue.setText("Rating " + rate);

                            //????????? ????????? ??????
                            TextView nickName = (TextView) findViewById(R.id.nickName);
                            nickName.setText(userID);

                            TextView restOfRating = findViewById(R.id.restOfRating);
                            if (rate < 5) restOfRating.setText("Next " + ((int) (5-rate)));
                            else if (rate < 25) restOfRating.setText("Next " + ((int) (25-rate)));
                            else if (rate < 40) restOfRating.setText("Next " + ((int) (40-rate)));
                            else if (rate < 60) restOfRating.setText("Next " + ((int) (60-rate)));
                            else if (rate < 120) restOfRating.setText("Next " + ((int) (120-rate)));
                            else if (rate < 200) restOfRating.setText("Next " + ((int) (200-rate)));
                            else if (rate < 300) restOfRating.setText("Next " + ((int) (300-rate)));
                            else if (rate < 400) restOfRating.setText("Next " + ((int) (400-rate)));
                            else if (rate < 500) restOfRating.setText("Next " + ((int) (500-rate)));
                            else if (rate < 600) restOfRating.setText("Next " + ((int) (600-rate)));
                            else if (rate < 1000) restOfRating.setText("Next " + ((int) (1000-rate)));
                            else if (rate < 1400) restOfRating.setText("Next " + ((int) (1400-rate)));
                            else if (rate < 1800) restOfRating.setText("Next " + ((int) (1800-rate)));
                            else if (rate < 2200) restOfRating.setText("Next " + ((int) (2200-rate)));
                            else if (rate < 3000) restOfRating.setText("Next " + ((int) (3000-rate)));
                            else if (rate < 4000) restOfRating.setText("Next " + ((int) (4000-rate)));
                            else if (rate < 5000) restOfRating.setText("Next " + ((int) (5000-rate)));
                            else if (rate < 6000) restOfRating.setText("Next " + ((int) (6000-rate)));
                            else if (rate < 7000) restOfRating.setText("Next " + ((int) (7000-rate)));
                            else if (rate < 10000) restOfRating.setText("Next " + ((int) (10000-rate)));
                            else if (rate < 13000) restOfRating.setText("Next " + ((int) (13000-rate)));
                            else if (rate < 16000) restOfRating.setText("Next " + ((int) (16000-rate)));
                            else if (rate < 19000) restOfRating.setText("Next " + ((int) (19000-rate)));
                            else if (rate < 22000) restOfRating.setText("Next " + ((int) (22000-rate)));
                            else if (rate < 30000) restOfRating.setText("Next " + ((int) (30000-rate)));
                            else restOfRating.setText("Next 0");
                        }
                    }
                });
        db.collection("??????")
                .document(userID)
                .collection("??? ??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            List<RatingData> ratings = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document.getId().equals("base")) continue;
                                Long tier = document.getLong("?????????");
                                ratings.add(new RatingData("title", tier));
                            }
                            RecyclerView recyclerView = findViewById(R.id.mainRatingScreen);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainScreen.this, 13);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            MainScreenAdapter mainScreenAdapter = new MainScreenAdapter(ratings);
                            recyclerView.setAdapter(mainScreenAdapter);
                        }
                    }
                });
        db.collection("??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Long> onlyRating = new LinkedList<>();
                        Long curUserRating = 1L;
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String user = document.getId();
                            Long eachRating = document.getLong("?????????");
                            if (user.equals(userID))  curUserRating = eachRating;
                            onlyRating.add(eachRating);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            onlyRating.sort(new Comparator<Long>() {
                                @Override
                                public int compare(Long aLong, Long t1) {
                                    return (int) (-aLong+t1);
                                }
                            });
                        }
                        double rateByRank = (double)100*(onlyRating.indexOf(curUserRating)+1)/onlyRating.size();
                        TextView topRateValue = findViewById(R.id.topRateValue);
                        topRateValue.setText("?????? " + String.format("%.2f", rateByRank) + "%");
                        TextView userPlace = findViewById(R.id.userPlace);
                        userPlace.setText(""+(onlyRating.indexOf(curUserRating)+1)+" place");
                    }
                });

        db.collection("??????")
                .document(userID)
                .collection("?????? ??? ??? ??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "PASS";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<MainSubjectData> selectTitle = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document.getId().equals("base")) continue;
                                String problemName = document.getId();
                                Long ACount = document.getLong("??? ?????? ???");
                                // ?????? ????????? ?????? ??????
                                Long subjectRating = 0L;
                                selectTitle.add(new MainSubjectData(problemName, ACount, subjectRating));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                selectTitle.sort(new Comparator<MainSubjectData>() {
                                    @Override
                                    public int compare(MainSubjectData problems1, MainSubjectData problems2) {
                                        return (int) (-problems1.ACount + problems2.ACount);
                                    } //??? ??? ?????? ????????? ????????? ??????
                                });
                            }
                            else{
                                //API ????????? 24??? ????????? ?????? ??????
                            }
                            RecyclerView recyclerView = findViewById(R.id.mainSubjectTitle);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainScreen.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            MainSubjectSelectAdapter mainSubjectSelectAdapter = new MainSubjectSelectAdapter(selectTitle);

                            mainSubjectSelectAdapter.setOnItemClickListener(new MainSubjectSelectAdapter.MainSubjectClickListener() {
                                @Override
                                public void onItemClick(View v, MainSubjectData mainSubjectData) {
                                    String subjectName = mainSubjectData.subjectName;

                                    Intent intent = new Intent(MainScreen.this, SolvedProblemScreen.class);
                                    intent.putExtra("subjectName", mainSubjectData.subjectName);
                                    intent.putExtra("userName", userID);
                                    intent.putExtra("problemCount", (int)(mainSubjectData.ACount-0));
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(mainSubjectSelectAdapter);
                        }
                    }
                });
    }
    

    public void goToTotalRanking(View view) {
        startActivity(new Intent(this, TotalRankingScreen.class));
    }

    public void goToProblemSolve(View view) {
        Intent intent = new Intent(this, SubjectSelectScreen.class);

        intent.putExtra("userName", userID);
        startActivity(intent);
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToEnrollProblem(View view) {
        ArrayList<Map<String, Integer>> ratingMap = new ArrayList<Map<String, Integer>>();
        ratingMap.add(getTierRating());
        Intent intent = new Intent(this, EnrollProblemScreen.class);
        intent.putExtra("nickname", userID);
        intent.putExtra("tierRating", ratingMap);
        startActivity(intent);
    }

    public void goToMyProblem(View view){
        Intent intent = new Intent(this, MyProblem.class);
        intent.putExtra("nickname", userID);
        startActivity(intent);
    }

    public void goToMyLikeProblem(View v){
        Intent intent = new Intent(this, ProblemSelectScreen.class);
        intent.putExtra("subjectName", "?????? ????????? ??? ??????");
        intent.putExtra("userName", userID);
        startActivity(intent);
    }

    // ?????? ????????? Map ?????? ???????????? ??????
    public Map<String, Integer> getTierRating() {

        Map<String, Integer> Problemrating = new HashMap<>();

        Problemrating.put("?????????5", 1);
        Problemrating.put("?????????4", 2);
        Problemrating.put("?????????3", 3);
        Problemrating.put("?????????2", 4);
        Problemrating.put("?????????1", 5);

        Problemrating.put("??????5", 12);
        Problemrating.put("??????4", 14);
        Problemrating.put("??????3", 16);
        Problemrating.put("??????2", 18);
        Problemrating.put("??????1", 20);

        Problemrating.put("??????5", 50);
        Problemrating.put("??????4", 55);
        Problemrating.put("??????3", 60);
        Problemrating.put("??????2", 65);
        Problemrating.put("??????1", 70);

        Problemrating.put("????????????5", 200);
        Problemrating.put("????????????4", 210);
        Problemrating.put("????????????3", 220);
        Problemrating.put("????????????2", 230);
        Problemrating.put("????????????1", 240);

        Problemrating.put("???????????????5", 520);
        Problemrating.put("???????????????4", 540);
        Problemrating.put("???????????????3", 560);
        Problemrating.put("???????????????2", 580);
        Problemrating.put("???????????????1", 600);

        Problemrating.put("??????", 1000);

        return Problemrating;
    }

}