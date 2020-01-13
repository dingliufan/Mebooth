package com.mebooth.mylibrary.main.RecommendMultiItemView;

import android.content.Context;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.utils.GlideImageManager;

import java.util.ArrayList;


public class RecommendItemVIew implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend;

    public RecommendItemVIew(Context context, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend) {
        this.context = context;
        this.recommend = recommend;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommend_item;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {

        if (position != 0) {
            if (recommend.get(position).getFeed().getType() == 1) {
                return false;

            } else {
                return true;
            }

        } else {
            return false;
        }
    }

    @Override
    public void convert(ViewHolder holder, GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
        holder.setText(R.id.recommenditem_nickname, recommend.get(position).getUser().getNickname());

        holder.setText(R.id.recommenditem_content, recommend.get(position).getFeed().getContent());
        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_img), GlideImageManager.TAG_FILLET);
        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
        holder.setText(R.id.recommenditem_time, (month) + "-" + date + " " + hour + ":" + minute);

        holder.setText(R.id.recommenditem_browsecount, String.valueOf(recommend.get(position).getFeed().getWatches()));
        holder.setText(R.id.recommenditem_commentcount, String.valueOf(recommend.get(position).getFeed().getReplies()));

    }
}
