package com.example.collegehelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegehelper.Adapter.ItemAdapter;
import com.example.collegehelper.Model.ItemModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  FloatingActionButton mAddAlarmFab, mAddPersonFab;
    ExtendedFloatingActionButton mAddFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;
    LottieAnimationView lottieAnimationView;
    FirebaseUser firebaseUser;
    ProgressDialog dialog;
    EditText search;
    Uri imageuri;
    DatabaseReference reference;
    String displayName = null;
    private AdView mAdView;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter,searchadpter;
    private List<ItemModel> postList,searchlist;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        mAddFab = findViewById(R.id.add_fab);
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        lottieAnimationView=findViewById(R.id.p2);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        search=findViewById(R.id.search);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);
        recyclerView=findViewById(R.id.recyclerview);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
             //   Toast.makeText(MainActivity.this,"Ad Loaded",Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
//                app id- ca-app-pub-7925334082418787~1493869248
//                ad id - ca-app-pub-7925334082418787/9521103892
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                mAdView.loadAd(adRequest);
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             if(count==0){
                 readPosts();
                 return;
             }
               searchlist.clear();
                searchUser(s.toString().toLowerCase());


            }

            @Override
            public void afterTextChanged(Editable s) {
             //  readPosts();
            }
        });
        postList=new ArrayList<>();
        searchlist=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layout= new LinearLayoutManager(this);
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        itemAdapter=new ItemAdapter(postList,this,lottieAnimationView);
        searchadpter=new ItemAdapter(searchlist,this,lottieAnimationView);
        recyclerView.setAdapter(itemAdapter);
        readPosts();
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                readPosts();
            }
        });
        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.shrink();
        mAddFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddAlarmFab.show();
                            mAddPersonFab.show();
                            addAlarmActionText.setVisibility(View.VISIBLE);
                            addPersonActionText.setVisibility(View.VISIBLE);
                            mAddFab.extend();
                            isAllFabsVisible = true;
                        } else {
                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);
                            mAddFab.shrink();
                            isAllFabsVisible = false;
                        }
                    }
                });
        mAddPersonFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       uploadresume();
                    }
                });
        mAddAlarmFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it=new Intent(MainActivity.this,UploadImage.class);
                        startActivity(it);
                    }
                });
    }
    private void searchUser(String s){
        searchlist.clear();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ItemModel post=dataSnapshot.getValue(ItemModel.class);
                    if(post!=null) {
                        String tags=post.getTags();
                        String[] arrOfStr = tags.split(",", 0);
                        for (String a : arrOfStr)
                        {
                            if(a.contains(s))
                                searchlist.add(post);
                        }

                    }
                }
                recyclerView.setAdapter(searchadpter);
                searchadpter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void uploadresume(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // We will be redirected to choose pdf
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date= dtf.format(now);
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();
            imageuri = data.getData();
            String uriString = imageuri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(imageuri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
            final String messagePushID = timestamp;
            final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        String uid=firebaseUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Post");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", postid);
                        hashMap.put("ispdf",true);
                        hashMap.put("tags",displayName+",pdf");
                        hashMap.put("date",date);
                        hashMap.put("name",displayName);
                        hashMap.put("url", myurl);
                        hashMap.put("publisher", uid);
                        reference.child(postid).setValue(hashMap);
                        Toast.makeText(MainActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search.getText().toString().equals("")) {
                    postList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ItemModel post = dataSnapshot.getValue(ItemModel.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                }
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void meetme(View view) {
        Uri uri = Uri.parse("https://agomi123.github.io/AGOMI-INDIA.github.io/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
