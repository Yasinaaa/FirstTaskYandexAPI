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

/**
 * Created by yasina on 18.09.15.
 */

public class DetailActivity extends Activity {

    public static final String EXTRA_PARAM_ID = "detail:_id";
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    private ImageView mHeaderImageView;
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
                    loadFullSizeImage();
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
            return true;
        }

        return false;
    }

}
