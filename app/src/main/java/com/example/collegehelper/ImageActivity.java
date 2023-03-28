package com.example.collegehelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {
ImageView img,rotate,rotated;
Intent it;
    boolean fl=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        img=findViewById(R.id.img);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        rotate=findViewById(R.id.rotate);
       rotated=findViewById(R.id.rotated);
        it=getIntent();
        String url= it.getStringExtra("imgurl");
        Glide.with(this).load(url).into(img);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // img.setRotation(90.0f);
               // roateImage(img);
                if(fl) {
                    img.animate().rotation(90).start();
                    fl=false;
                }
                else{
                    img.animate().rotation(-90).start();
                    fl=true;
                }
            }
        });
        rotated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // img.setRotation(90.0f);
                if(fl) {
                    img.animate().rotation(-90).start();
                    fl=false;
                }
                else{
                    img.animate().rotation(90).start();
                    fl=true;
                }
            }
        });
    }
    private void roateImage(ImageView imageView) {
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        matrix.postRotate((float) 90, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }
    private void roateImageacw(ImageView imageView) {
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        matrix.preRotate((float) 90, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }
}