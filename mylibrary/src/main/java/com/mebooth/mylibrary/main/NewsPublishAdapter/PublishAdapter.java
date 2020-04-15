package com.mebooth.mylibrary.main.NewsPublishAdapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.activity.NewsPublishActivity;
import com.mebooth.mylibrary.main.home.bean.NewPublish;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.Collections;

import static com.mebooth.mylibrary.main.home.activity.NewsPublishActivity.newPublishesList;

public class PublishAdapter implements ItemViewDelegate<NewPublish> {

    private Context context;
    private MultiItemTypeAdapter multiItemTypeAdapter;
    private boolean shuRu;
    private NoPublish noPublish;

    public PublishAdapter(Context context, MultiItemTypeAdapter multiItemTypeAdapter,NoPublish noPublish) {
        this.context = context;
        this.multiItemTypeAdapter = multiItemTypeAdapter;
        this.noPublish = noPublish;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.newspublish_item;
    }

    @Override
    public boolean isForViewType(NewPublish item, int position) {

        if (position != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void convert(final ViewHolder holder, final NewPublish newPublish, final int position) {

        shuRu = true;

        holder.setIsRecyclable(false);

        if (newPublish.getType().equals("text")) {

            holder.setVisible(R.id.fiv, View.GONE);
            holder.setVisible(R.id.publish_content, View.VISIBLE);

            holder.setText(R.id.publish_content, newPublishesList.get(position).getContent());

        } else if (newPublish.getType().equals("image")) {
            holder.setVisible(R.id.fiv, View.VISIBLE);
            holder.setVisible(R.id.publish_content, View.GONE);
            GlideImageManager.glideLoader(context, newPublishesList.get(position).getContent(), (ImageView) holder.getView(R.id.fiv), GlideImageManager.TAG_RECTANGLE);

        }

        holder.setOnClickListener(R.id.publish_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPublishesList.remove(position);
                multiItemTypeAdapter.notifyDataSetChanged();


            }
        });

        shuRu = false;

        EditText edit = holder.getView(R.id.publish_content);

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    newPublishesList.get(position).setContent(s.toString());

                    if(s.toString().length()>=1000){

                        ToastUtils.getInstance().showToast("每个输入框最多只能输入1000个字");

                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                return true;
//            }
//        });

        holder.setOnClickListener(R.id.publish_up, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int fromPosition = holder.getAdapterPosition();
                int toPosition = fromPosition - 1;
                //交换集合中两个数据的位置
                if (toPosition == 0) {

                } else {
                    Collections.swap(newPublishesList, fromPosition, toPosition);
                    //刷新界面,局部刷新,索引会混乱
//                    multiItemTypeAdapter.notifyDataSetChanged();
                    multiItemTypeAdapter.notifyItemMoved(fromPosition, toPosition);
                }

            }
        });

        holder.setOnClickListener(R.id.publish_down, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromPosition = holder.getAdapterPosition();
                int toPosition = fromPosition + 1;
                if (toPosition >= newPublishesList.size()) {

                } else {
                    Collections.swap(newPublishesList, fromPosition, toPosition);
                    //刷新界面,局部刷新,索引会混乱
//                    multiItemTypeAdapter.notifyDataSetChanged();
                    multiItemTypeAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            }
        });

    }
}
