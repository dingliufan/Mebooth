package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.bean.GetDecorationJson;
import com.mebooth.mylibrary.utils.GlideImageManager;

import java.util.ArrayList;

public class DecorationGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GetDecorationJson.DecorationData.DecorationView.DecorationGroup.Decorationlist> list;

    public DecorationGridAdapter(Context context, ArrayList<GetDecorationJson.DecorationData.DecorationView.DecorationGroup.Decorationlist> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.decorationgrid_item, null);

        ImageView decorationGridLevel = convertView.findViewById(R.id.decorationgrid_item_level);
        ImageView decorationGridLabel = convertView.findViewById(R.id.decorationgrid_item_label);
        TextView decorationGridTitle = convertView.findViewById(R.id.decorationgrid_item_title);
        TextView decorationGridContent = convertView.findViewById(R.id.decorationgrid_item_content);

        GlideImageManager.glideLoader(context, list.get(position).getImage(), decorationGridLevel, GlideImageManager.TAG_RECTANGLE);
        if (list.get(position).isLocked()) {
            decorationGridLabel.setImageResource(R.drawable.decorationgrid_godeblock);
        }
        if (list.get(position).isUpgrade()) {
            decorationGridLabel.setImageResource(R.drawable.decorationgrid_canupgrade);
        }

        decorationGridTitle.setText(list.get(position).getTitle());
        decorationGridContent.setText(list.get(position).getContent());

        return convertView;
    }
}
