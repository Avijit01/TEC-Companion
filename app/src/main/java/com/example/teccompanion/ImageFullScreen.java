package com.example.teccompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageFullScreen extends AppCompatActivity {

    ImageView image;
    String receiveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);

        image = (ImageView) findViewById(R.id.imageFullScreenId);
        receiveImage = getIntent().getExtras().get("visit_image").toString();

        Picasso.get().load(receiveImage).into(image);
    }
}