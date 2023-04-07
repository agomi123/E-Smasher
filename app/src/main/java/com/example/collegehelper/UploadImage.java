package com.example.collegehelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.collegehelper.Adapter.ItemAdapter;
import com.example.collegehelper.Adapter.UploadListAdapter;
import com.example.collegehelper.Model.ItemModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadImage extends AppCompatActivity {
    public static final int RESULT_IMAGE = 10;
    private Button btnUpload;
    LottieAnimationView lottieAnimationView;
    private StorageTask uploadtask;
    EditText addtags;
    FirebaseUser firebaseUser;
    private AdView mAdView;
    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<ItemModel> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        btnUpload = findViewById(R.id.btnUpload);
        lottieAnimationView=findViewById(R.id.p2);
        addtags=findViewById(R.id.tags);
        postList=new ArrayList<>();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        LinearLayoutManager layout= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        itemAdapter=new ItemAdapter(postList,this,lottieAnimationView);
        recyclerView.setAdapter(itemAdapter);
        storageReference = FirebaseStorage.getInstance().getReference();
        readPosts();
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
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tags= addtags.getText().toString();
                if(tags.isEmpty()){
                    Toast.makeText(UploadImage.this,"Add Tag Related to The Images",Toast.LENGTH_SHORT).show();
                    return;
                }
                requestPermission();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_IMAGE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date= dtf.format(now);
        assert firebaseUser != null;
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri>urls=new ArrayList<>();
        if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK){
            assert data != null;
            if (data.getClipData() != null || data.getData()!=null){
                int totalItems = data.getClipData().getItemCount();
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading");
                progressDialog.show();
                for (int i = 0;i < totalItems; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    urls.add(fileUri);
                    String fileName = getFileName(fileUri);
                   upoadImage(fileUri,fileName,date);
                }
                progressDialog.cancel();
                 Toast.makeText(UploadImage.this,"Images Uploaded",Toast.LENGTH_SHORT).show();
                readPosts();
            }
        }
    }
    private void upoadImage(Uri imageurri,String filename,String date) {
        String uid=firebaseUser.getUid();
        if (imageurri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("uploads");
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageurri));
            uploadtask = filereference.putFile(imageurri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloaduri = (Uri) task.getResult();
                        String muri = downloaduri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", postid);
                        hashMap.put("ispdf", false);
                        hashMap.put("tags", addtags.getText().toString()+",img");
                        hashMap.put("date", date);
                        hashMap.put("name", filename);
                        hashMap.put("url", muri);
                        hashMap.put("publisher", uid);
                        assert postid != null;
                        reference.child(postid).setValue(hashMap);

                    } else {
                        Toast.makeText(UploadImage.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //pd.dismiss();
                }
            });
        } else {
            Toast.makeText(UploadImage.this, "no image selected", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageuri = data.getData();
//            if (uploadtask != null && uploadtask.isInProgress()) {
//                Toast.makeText(UploadImage.this, "upload in progress", Toast.LENGTH_SHORT).show();
//
//            } else {
//                upoadImage();
//            }
//        }
//
//    }

    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        } if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadImage.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void requestPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //Toast.makeText(RegisterActivity2.this, "Permission", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        PermissionListener dialogPermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                        .withContext(UploadImage.this)
                                        .withTitle("Read External Storage permission")
                                        .withMessage("Read External Storage  permission is needed")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.drawable.logo)
                                        .build();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private void readPosts(){
        addtags.setText("");
        //lottieAnimationView.setVis
        // ibility(View.VISIBLE);
        String uid=firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ItemModel post=dataSnapshot.getValue(ItemModel.class);
                    if(post!=null) {
                        if (post.getPublisher().equals(uid)) {
                            postList.add(post);

                        }
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      //  lottieAnimationView.setVisibility(View.INVISIBLE);
    }
}