package com.soli.libCommon.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoLi on 2015/7/31.
 */
public class BaseRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList;

    protected Context ctx;

    public BaseRecycleAdapter(Context context) {
        this.ctx = context;
    }

    public BaseRecycleAdapter(Context context, List<T> list) {
        this.ctx = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * @param position
     * @return
     */
    public T getItemData(int position) {
        return mList != null && mList.size() > 0 && position < getItemCount() ? mList.get(position) : null;
    }

    /**
     * @param list
     */
    public void setList(List<T> list) {
        if (list != null && list.size() > 0) {
            mList = new ArrayList<T>();
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    public List<T> getList() {
        return this.mList;
    }

    public void add(T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(int position, List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(position, list);
        notifyDataSetChanged();
    }

    public void insertAtTop(T data) {
        insert(0, data);
    }

    /**
     * @param position
     * @param data
     */
    public void insert(int position, T data) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }

        mList.add(position, data);
        notifyItemInserted(position);
    }

//    public void remove(T position) {
//        if (mList != null) {
//            mList.remove(position);
//            notifyDataSetChanged();
//        }
//    }

    /**
     * @param position
     * @param data
     */
    public void set(int position, T data) {
        if (mList != null && position < getItemCount()) {
            mList.set(position, data);
        }
    }

    /**
     * @param position
     */
    public void remove(int position) {
        try {
            if (mList != null) {
                if (position >= 0 || position < mList.size()) {
                    mList.remove(position);
                    notifyItemRemoved(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.removeAll(mList);
            notifyDataSetChanged();
        }
    }
}
