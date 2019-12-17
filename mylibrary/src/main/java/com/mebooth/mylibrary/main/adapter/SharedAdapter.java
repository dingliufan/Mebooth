package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;

import java.util.ArrayList;

public class SharedAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> sharedList = new ArrayList<>();
    private ArrayList<Integer> imgSharedList = new ArrayList<>();

    public SharedAdapter(Context context, ArrayList<String> sharedList, ArrayList<Integer> imgSharedList) {
        this.context = context;
        this.sharedList = sharedList;
        this.imgSharedList = imgSharedList;
    }

    @Override
    public int getCount() {
        return sharedList.size();
    }

    @Override
    public Object getItem(int position) {
        return sharedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.shareditem_layout, null);

        ImageView sharedImg = (ImageView) convertView.findViewById(R.id.shared_img);
        TextView tvShared = (TextView) convertView.findViewById(R.id.shared_tv);

        sharedImg.setImageResource(imgSharedList.get(position));
        tvShared.setText(sharedList.get(position));

        return convertView;
    }
}
