package com.example.collegehelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegehelper.Model.ItemModel;
import com.example.collegehelper.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {
CircleImageView circleImageView;
Intent it;
    int c=0;
TextView name,name2,email,organization,branch,yr,mbl, count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        circleImageView=findViewById(R.id.imgp);
        name=findViewById(R.id.namep);
        count=findViewById(R.id.usp);
        name2=findViewById(R.id.namepp);
        email=findViewById(R.id.emailp);
        mbl=findViewById(R.id.mbl);
        branch=findViewById(R.id.branch);
        yr=findViewById(R.id.yr);
        organization=findViewById(R.id.orgp);
        it=getIntent();
        String userid=it.getStringExtra("userid");
       DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getName());
                name2.setText(user.getName());
                email.setText(user.getEmail());
                branch.setText(user.getBranch());
                mbl.setText(user.getMobile());
                yr.setText("("+user.getYear()+")");
                organization.setText(user.getCollege());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void backplease(View view) {
        finish();
    }
    private void readPosts(){

        //lottieAnimationView.setVisibility(View.VISIBLE);
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ItemModel post=dataSnapshot.getValue(ItemModel.class);
                    if(post!=null) {
                        if (post.getPublisher().equals(uid)) {
                           c++;
                        }
                    }
                }
                count.setText("Contribution: "+c);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //  lottieAnimationView.setVisibility(View.INVISIBLE);
    }

    public void block(View view) {
        Toast.makeText(ViewProfile.this, "User Blocked", Toast.LENGTH_SHORT).show();
    }
    public void report(View view) {
        Toast.makeText(ViewProfile.this, "User Reported", Toast.LENGTH_SHORT).show();
    }
}