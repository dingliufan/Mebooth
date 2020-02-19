package com.mebooth.mylibrary.main.entity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.main.home.bean.NewPublish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleItemTouchCallBack extends ItemTouchHelper.Callback {
    private MultiItemTypeAdapter adapter;
    private ArrayList<NewPublish> mData;

    public SimpleItemTouchCallBack(MultiItemTypeAdapter adapter, ArrayList<NewPublish> mData) {
        this.adapter = adapter;
        this.mData = mData;
    }

    /**
     * 设置ItemTouchHelper的类型
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 上下拖动，
     * return true;//可以滑动
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        Collections.swap(mData, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);//局部更新，如果item有点击事件的话，会出错position，就不要用了
//        adapter.notifyDataSetChanged();//没有动画效果，但适用于item有点击事件的话
        return true;//true:可以滑动
    }

    /**
     * 左右滑动删除
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mData.remove(position);
        adapter.notifyItemRemoved(position);//局部更新，如果item有点击事件的话，会出错position，就不要用了
//        adapter.notifyDataSetChanged();//没有动画效果，但适用于item有点击事件的话
    }
}
