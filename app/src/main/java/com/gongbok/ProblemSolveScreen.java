package com.gongbok;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProblemSolveScreen extends AppCompatActivity {
    String subjectName;
    String problemName;
    String userName;
    String path;
    Long rating;
    Long tier;
    Long likeNum;
    Long tryNum;
    Boolean isLike = false;
    DocumentReference problemNameDocRef;
    TextView likeCountTv;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_solve_screen);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent getIntent = getIntent();
        subjectName = getIntent.getStringExtra("subjectName");
        problemName = getIntent.getStringExtra("problemName");
        userName = getIntent.getStringExtra("userName");

        TextView subjectNameTextView = findViewById(R.id.subjectName);
        subjectNameTextView.setText(subjectName);
        likeCountTv = findViewById(R.id.likeCount);



        //?????? ????????? ??? ???????????? ????????? ??????
        db.collection("??????")
                .document(userName)
                .collection("????????? ??? ??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String likedProblemName = document.getId();

                            if(likedProblemName.equals(subjectName+" "+problemName)) {
                                isLike = true;
                                break;
                            }
                        }

                        //?????? ????????? ??? ???????????? ??? ????????? ??? ????????? ??????
                        if(isLike){
                            ImageView likeImage = findViewById(R.id.heart);
                            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));
                        }
                    }
                });

        problemNameDocRef = db.collection("??????")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName);

        //?????? ???????????? ?????? ??? ?????? ??????
        problemNameDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    path = document.getString("??????");
                    tier = document.getLong("?????????");
                    rating = document.getLong("?????????");
                    likeNum = document.getLong("????????? ???");
                    tryNum = document.getLong("?????? ??????");
                    Long trialCount = document.getLong("?????? ??????");
                    Long solvedCount = document.getLong("?????? ??????");

                    ImageView iv = findViewById(R.id.problemImage);
                    TextView trialCountTextView = findViewById(R.id.trialCount);
                    TextView solvedCountTextView = findViewById(R.id.solvedCount);
                    TextView likeCountTextView = findViewById(R.id.likeCount);

                    trialCountTextView.setText(String.valueOf(trialCount));
                    solvedCountTextView.setText(String.valueOf(solvedCount));
                    likeCountTextView.setText(String.valueOf(likeNum));

                    StorageReference storageRef = storage.getReference();
                    StorageReference pathReference = storageRef.child(path);

                    Glide.with(ProblemSolveScreen.this)
                            .load(pathReference)
                            .into(iv);
                }
            }
        });
    }

    public void goToMain(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
        builder.setTitle("????????? ?????????????????????????");

        builder.setPositiveButton("?????? ??????", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos) {

            }
        });
        builder.setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent homeIntent = new Intent(ProblemSolveScreen.this, MainScreen.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void submitButtonClicked(View view){
        EditText inputAnswer = findViewById(R.id.inputAnswer);
        String userAnswer = inputAnswer.getText().toString().trim();

        problemNameDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                String answer = document.getString("??????").trim();
                Long trialCount = document.getLong("?????? ??????");

                //????????? ?????? ?????? ??????
                trialCount += 1;
                problemNameDocRef.update("?????? ??????", trialCount);

                if(userAnswer.equals(answer))
                    answerIsRight();
                else{
                    // ?????? ?????? ???????????? ????????? ??????
                    db.collection("??????")
                            .document(userName)
                            .collection("??? ??????")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    boolean isExistInRight = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getId().equals("base"))
                                            continue;

                                        String existProblemName = document.getString("?????? ??????");

                                        if(existProblemName.equals(problemName))
                                            isExistInRight = true;
                                    }
                                    if(isExistInRight){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                                        builder.setTitle("??????????????? ?????? ????????? ???????????????.");

                                        builder.setPositiveButton("?????? ????????????", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int pos) {

                                            }
                                        });
                                        builder.setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                            }
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else{
                                        //?????? ?????? ???????????? ??????
                                        Map<String, Object> problemBase = new HashMap<>();
                                        problemBase.put("??????", path);
                                        problemBase.put("??????", subjectName);
                                        problemBase.put("?????? ??????", problemName);
                                        problemBase.put("?????????", tier);
                                        problemBase.put("?????????", rating);
                                        problemBase.put("?????? ??????", tryNum);

                                        db.collection("??????")
                                                .document(userName)
                                                .collection("?????? ??????")
                                                .document(subjectName+" "+problemName)
                                                .set(problemBase);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                                        builder.setTitle("??????????????????.");

                                        builder.setPositiveButton("?????? ????????????", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int pos) {

                                            }
                                        });
                                        builder.setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                            }
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void answerIsRight(){
        //????????? ????????? ?????? ?????? ?????? ??????????????? ??????
        db.collection("??????")
                .document(userName)
                .collection("?????? ??????")
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals("base"))
                                        continue;

                                    String existProblemName = document.getString("?????? ??????");

                                    if(existProblemName.equals(problemName)){
                                        db.collection("??????")
                                                .document(userName)
                                                .collection("?????? ??????")
                                                .document(document.getId())
                                                .delete();
                                    }
                                }
                            }
                        });

        // ????????? ????????? ?????? ????????? "????????? ??? ??????" collection??? "?????? ??????" document ??????
        db.collection("??????")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName)
                .collection("????????? ??? ??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Map<String, Object> solvedUserBase = new HashMap<>();
                        solvedUserBase.put("base", 0);

                        db.collection("??????")
                                .document(subjectName)
                                .collection(subjectName)
                                .document(problemName)
                                .collection("????????? ??? ??????")
                                .document(userName)
                                .set(solvedUserBase);
                    }
                });

        db.collection("??????")
                .document(userName)
                .collection("??? ??????")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isExist = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String existProblemName = document.getString("?????? ??????");

                            if(document.getId().equals("base"))
                                continue;
                            if(existProblemName.equals(problemName))
                                isExist = true;
                        }

                        if(isExist){
                            // ?????? ?????? ???????????? ?????? ??????????????? ??????
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                            builder.setTitle("?????? ??? ???????????????.");

                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int pos) {
                                    startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                }
                            });
                            builder.setNegativeButton("????????? ?????? & ?????? ??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intentToEnrollExplanation = new Intent(ProblemSolveScreen.this, EnrollExplanationScreen.class);

                                    intentToEnrollExplanation.putExtra("userName", userName);
                                    intentToEnrollExplanation.putExtra("problemName", problemName);
                                    intentToEnrollExplanation.putExtra("subjectName", subjectName);

                                    startActivity(intentToEnrollExplanation);
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else{
                            //??? ?????? ???????????? ??????
                            Map<String, Object> problemBase = new HashMap<>();
                            problemBase.put("??????", path);
                            problemBase.put("??????", subjectName);
                            problemBase.put("?????? ??????", problemName);
                            problemBase.put("?????????", tier);
                            problemBase.put("?????????", rating);
                            problemBase.put("?????? ??????", tryNum);

                            //?????? ?????? ??????
                            db.collection("??????")
                                    .document(subjectName)
                                    .collection(subjectName)
                                    .document(problemName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();

                                            Long trueNum = document.getLong("?????? ??????");

                                            db.collection("??????")
                                                    .document(subjectName)
                                                    .collection(subjectName)
                                                    .document(problemName)
                                                    .update("?????? ??????", trueNum+1);
                                        }
                                    });

                            db.collection("??????")
                                    .document(userName)
                                    .collection("??? ??????")
                                    .document(subjectName+" "+problemName)
                                    .set(problemBase);

                            //?????? ??? ??? ????????? ?????? ??????
                            db.collection("??????")
                                    .document(userName)
                                    .collection("?????? ??? ??? ??????")
                                    .document(subjectName)
                                    .collection(subjectName)
                                    .document(subjectName+" "+problemName)
                                    .set(problemBase);

                            //?????? ??? ??? ????????? ??? ?????? ??? ??????
                            db.collection("??????")
                                    .document(userName)
                                    .collection("?????? ??? ??? ??????")
                                    .document(subjectName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();

                                            Long problemCount = document.getLong("??? ?????? ???");
                                            db.collection("??????")
                                                    .document(userName)
                                                    .collection("?????? ??? ??? ??????")
                                                    .document(subjectName)
                                                    .update("??? ?????? ???", problemCount+1);
                                        }
                                    });


                            //????????? ?????? ????????? ?????? ?????? ????????? ??????
                            db.collection("??????")
                                    .document(userName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();

                                            Long point = document.getLong("?????????");
                                            Long userRating = point + rating;

                                            db.collection("??????")
                                                    .document(userName)
                                                    .update("?????????", userRating);

                                            //???????????? ????????? ????????? ???????????? ??????
                                            tierCheck();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                                            builder.setTitle("????????? ??????????????????.");
                                            builder.setMessage("Rating + "+rating +" = " + userRating);

                                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                                                public void onClick(DialogInterface dialog, int pos) {
                                                    startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                                }
                                            });
                                            builder.setNegativeButton("????????? ?????? & ?????? ??????", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intentToEnrollExplanation = new Intent(ProblemSolveScreen.this, EnrollExplanationScreen.class);

                                                    intentToEnrollExplanation.putExtra("userName", userName);
                                                    intentToEnrollExplanation.putExtra("problemName", problemName);
                                                    intentToEnrollExplanation.putExtra("subjectName", subjectName);

                                                    startActivity(intentToEnrollExplanation);
                                                }
                                            });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    });
                        }
                    }
                });
    }
    public void likeButtonClicked(View v){
        //?????? ????????? ??? ???????????? ????????? ????????? ????????? firestore??? ????????? ??? ?????? ??????????????? ??????
        if(isLike){
            isLike = false;
            //????????? ??? ??????
            problemNameDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            Long like = document.getLong("????????? ???");
                            like = like - 1;
                            problemNameDocRef.update("????????? ???", like);

                            likeCountTv.setText(like.toString());
                        }
                    });

            ImageView likeImage = findViewById(R.id.heart);
            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.empty_heart));

            db.collection("??????")
                    .document(userName)
                    .collection("????????? ??? ??????")
                    .document(subjectName+" "+problemName)
                    .delete();
        }
        //????????? ?????? ????????? ???????????? ????????? ????????? ????????? ????????? ??? ?????? ???????????? ??????
        else{
            isLike = true;
            //????????? ??? ??????
            problemNameDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            Long like = document.getLong("????????? ???");
                            like = like + 1;
                            problemNameDocRef.update("????????? ???", like);

                            likeCountTv.setText(like.toString());
                        }
                    });

            ImageView likeImage = findViewById(R.id.heart);
            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));

            Map<String, Object> problemBase = new HashMap<>();
            problemBase.put("??????", path);
            problemBase.put("??????", subjectName);
            problemBase.put("?????? ??????", problemName);
            problemBase.put("?????????", tier);
            problemBase.put("????????? ???", likeNum);
            problemBase.put("?????? ??????", tryNum);

            db.collection("??????")
                    .document(userName)
                    .collection("????????? ??? ??????")
                    .document(subjectName+" "+problemName)
                    .set(problemBase);
        }
    }
    public void tierCheck(){
        db.collection("??????")
                .document(userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        Long rating = document.getLong("?????????");
                        if(rating < 5)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 1);
                        else if(rating < 25)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 2);
                        else if(rating < 40)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 3);
                        else if(rating < 60)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 4);
                        else if(rating < 120)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 5);
                        else if(rating < 200)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 6);
                        else if(rating < 300)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 7);
                        else if(rating < 400)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 8);
                        else if(rating < 500)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 10);
                        else if(rating < 1000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 11);
                        else if(rating < 1400)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 12);
                        else if(rating < 1800)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 13);
                        else if(rating < 2200)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 14);
                        else if(rating < 3000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 15);
                        else if(rating < 4000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 16);
                        else if(rating < 5000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 17);
                        else if(rating < 6000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 18);
                        else if(rating < 7000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 19);
                        else if(rating < 10000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 20);
                        else if(rating < 13000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 21);
                        else if(rating < 16000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 22);
                        else if(rating < 19000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 23);
                        else if(rating < 22000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 24);
                        else if(rating < 30000)
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 25);
                        else
                            db.collection("??????")
                                    .document(userName)
                                    .update("??????", 26);
                    }
                });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
        builder.setTitle("????????? ?????????????????????????");

        builder.setPositiveButton("?????? ??????", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos) {

            }
        });
        builder.setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}