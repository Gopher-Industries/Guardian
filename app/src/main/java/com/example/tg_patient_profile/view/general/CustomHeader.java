package com.example.tg_patient_profile.view.general;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.tg_patient_profile.R;
import com.google.android.material.navigation.NavigationView;

public class CustomHeader extends FrameLayout {

    public ImageView menuButton;
    private ImageView headerLogo;
    private ImageView headerProfileIcon;
    private ImageView headerTopImage;
    private TextView headerTextView;
    private View headerCard;

    public CustomHeader(Context context) {
        super(context);
        init(context, null);
    }

    public CustomHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_header, this, true);

        headerLogo = findViewById(R.id.headerGuardiansLogo);
        menuButton = findViewById(R.id.headerMenuIcon);
        headerProfileIcon = findViewById(R.id.headerProfileIcon);
        headerTextView = findViewById(R.id.headerTextView);
        headerCard = findViewById(R.id.headerCard);
        headerTopImage = findViewById(R.id.headerTopImage);

    }

    public void setHeaderTopImage(int resource) {
        headerTopImage.setImageResource(resource);
    }
    public void setHeaderTopImageVisibility(int visible) {
        headerTopImage.setVisibility(visible);
    }
    public void setHeaderText(String text) {
        headerTextView.setText(text);
    }

    public void setHeaderLogoVisibility(int visible) { headerLogo.setVisibility(visible);}

    public void setHeaderHeight(int height) {
        headerCard.getLayoutParams().height = height;
        headerCard.requestLayout();
    }

    public void setProfileIconVisibility(int visible) { headerProfileIcon.setVisibility(visible);}


}

