package deakin.gopher.guardian.view.general;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import deakin.gopher.guardian.R;

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

  public CustomHeader(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
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

  public void setHeaderHeight(int height) {
    headerCard.getLayoutParams().height = height;
    headerCard.requestLayout();
  }
}
