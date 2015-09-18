package com.example.yasina.picturesecond;

import com.example.yasina.picturesecond.utils.Constants;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private GridAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        mGridView = (GridView) findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String item = (String) adapterView.getItemAtPosition(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, position);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,

                new Pair<View, String>(view.findViewById(R.id.imageview_item),
                        DetailActivity.VIEW_NAME_HEADER_IMAGE));

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }


    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Constants.getImagesUrls().size();
        }

        @Override
        public String getItem(int position) {
            return Constants.getImagesUrls().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            String item = getItem(position);
            ImageView image = (ImageView) view.findViewById(R.id.imageview_item);
            Picasso.with(image.getContext()).load(item).into(image);

            return view;
        }
    }
}
