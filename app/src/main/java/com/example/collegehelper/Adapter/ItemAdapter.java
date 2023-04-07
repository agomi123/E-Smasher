package com.example.collegehelper.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegehelper.ImageActivity;
import com.example.collegehelper.Model.ItemModel;
import com.example.collegehelper.Model.User;
import com.example.collegehelper.R;
import com.example.collegehelper.UploadPdf;
import com.example.collegehelper.ViewProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    public List<ItemModel> fileNameList;
    LottieAnimationView lottieAnimationViewm;
    private Context context;
    public ItemAdapter(List<ItemModel> fileNameList,Context context,LottieAnimationView lottieAnimationView) {
        this.fileNameList = fileNameList;
        this.context=context;
        lottieAnimationViewm=lottieAnimationView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(fileNameList.size()==0){
            lottieAnimationViewm.setVisibility(View.GONE);
        }
        ItemModel itemModel = fileNameList.get(position);
        publisherInfo(holder.username,holder.name, itemModel.getPublisher());
        holder.date.setText(itemModel.getDate());
        holder.tags.setText(itemModel.getTags());
        if (itemModel.getIspdf()) {
            holder.typeimage.setImageResource(R.drawable.image5);
        } else {
            holder.typeimage.setImageResource(R.drawable.image4);
        }
        holder.filename.setText(itemModel.getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ViewProfile.class);
                it.putExtra("userid", itemModel.getPublisher());
                context.startActivity(it);
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ViewProfile.class);
                it.putExtra("userid", itemModel.getPublisher());
                context.startActivity(it);
            }
        });
            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]{
                            "Download",
                            "View",
                            "Cancel"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Choose One");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // we will be downloading the pdf
                            if(itemModel.getIspdf()) {
                                if (which == 0) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemModel.getUrl()));
                                    context.startActivity(intent);
                                }
                                if (which == 1) {
                                    Intent intent = new Intent(v.getContext(), UploadPdf.class);
                                    intent.putExtra("url", itemModel.getUrl());
                                    context.startActivity(intent);
                                }
                            }
                            else{
                                if (which == 0) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemModel.getUrl()));
                                    context.startActivity(intent);
                                }
                                if (which == 1) {
                                    Intent intent = new Intent(v.getContext(), ImageActivity.class);
                                    intent.putExtra("imgurl", itemModel.getUrl());
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });
                    builder.show();
                }
            });
        lottieAnimationViewm.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username,name, tags,filename, date;
        ImageView typeimage,open;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.uname);
            filename = itemView.findViewById(R.id.filename);
            date = itemView.findViewById(R.id.dateup);
            tags=itemView.findViewById(R.id.taghs);
           name = itemView.findViewById(R.id.nme);
           open = itemView.findViewById(R.id.open);
            typeimage = itemView.findViewById(R.id.typeimg);

        }

    }

    private void publisherInfo(final TextView username,final TextView name, final String userid) {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getName());
                name.setText("("+user.getYear()+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
