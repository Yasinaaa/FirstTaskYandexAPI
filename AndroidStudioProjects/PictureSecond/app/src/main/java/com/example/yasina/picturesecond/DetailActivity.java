package com.example.yasina.picturesecond;

import com.example.yasina.picturesecond.utils.Constants;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailActivity extends Activity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";


    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;

    private String mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mItem = Constants.getImagesUrls().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        mHeaderImageView = (ImageView) findViewById(R.id.imageview_header);
        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
        loadItem();
    }

    private void loadItem() {

//        mHeaderTitle.setText(getString(R.string.image_header, mItem));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            loadThumbnail();
        } else {
            loadFullSizeImage();
        }
    }


    private void loadThumbnail() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem)
                .noFade()
                .into(mHeaderImageView);
    }


    private void loadFullSizeImage() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem)
                .noFade()
                .noPlaceholder()
                .into(mHeaderImageView);
    }

    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {

            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullSizeImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

}
