package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.QuicklyActivity;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();
    private FlushJson bannerJson;

    private OnItemClickListener mOnItemClickListener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public RecommendAdapter(Context context, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend, FlushJson bannerJson) {
        this.context = context;
        this.recommend = recommend;
        this.bannerJson = bannerJson;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recommend_item,parent,false);
        return new ViewHolder(view);
    }

    private int praises;

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {


    }

    @Override
    public int getItemCount() {
        return recommend.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
//            name = itemView.findViewById(R.id.recommend_header_lly);
//            name = itemView.findViewById(R.id.recommendnews_item);
//            name = itemView.findViewById(R.id.recommendnow_item);
//            name = itemView.findViewById(R.id.recommenditem_headerimg);
//            name = itemView.findViewById(R.id.buy_car);
//            name = itemView.findViewById(R.id.publicusecar);
//            name = itemView.findViewById(R.id.logisticsusecar);
//            name = itemView.findViewById(R.id.recommenditem_headericon);
//            name = itemView.findViewById(R.id.recommenditem_nickname);

        }
    }


}
