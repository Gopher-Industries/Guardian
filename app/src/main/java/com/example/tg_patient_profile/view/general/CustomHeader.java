package com.example.tg_patient_profile.view.general;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tg_patient_profile.R;

public class CustomHeader extends FrameLayout {

    public ImageView menuButton;
    private ImageView headerLogo;
    private ImageView headerProfileIcon;
    private ImageView headerTopImage;
    private TextView headerTextView;
    private View headerCard;

    public CustomHeader(final Context context) {
        super(context);
        init(context, null);
    }

    public CustomHeader(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_header, this, true);

        headerLogo = findViewById(R.id.headerGuardiansLogo);
        menuButton = findViewById(R.id.headerMenuIcon);
        headerProfileIcon = findViewById(R.id.headerProfileIcon);
        headerTextView = findViewById(R.id.headerTextView);
        headerCard = findViewById(R.id.headerCard);
        headerTopImage = findViewById(R.id.headerTopImage);

    }

    public void setHeaderTopImage(final int resource) {
        headerTopImage.setImageResource(resource);
    }

    public void setHeaderTopImageVisibility(final int visible) {
        headerTopImage.setVisibility(visible);
    }

    public void setHeaderText(final String text) {
        headerTextView.setText(text);
    }

    public void setHeaderLogoVisibility(final int visible) {
        headerLogo.setVisibility(visible);
    }

    public void setHeaderHeight(final int height) {
        headerCard.getLayoutParams().height = height;
        headerCard.requestLayout();
    }

    public void setProfileIconVisibility(final int visible) {
        headerProfileIcon.setVisibility(visible);
    }


}

