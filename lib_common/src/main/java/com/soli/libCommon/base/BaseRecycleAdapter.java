package com.soli.libCommon.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 时间：2018/11/13
 * 作者：CDY
 */
public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final int ITEM_TYPE_NORMAL = 0;
    protected final int ITEM_TYPE_HEADER = 1;
    protected final int ITEM_TYPE_FOOTER = 2;

    protected List<T> mList;

    protected Context ctx;

    protected View HeaderView, FooterView;

    protected LayoutInflater inflater;

    //更新数据是否要用有动画的那种效果
    protected boolean useHaveAnimationRefresh = true;

    private OnItemClickListener onItemClickListener;

    public BaseRecycleAdapter(Context context) {
        this.ctx = context;
        inflater = LayoutInflater.from(context);
    }

    public BaseRecycleAdapter(Context context, List<T> list) {
        this.ctx = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return onCreateViewHolder_impl(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {

        int type = getItemViewType(position);
        int realPosition = getRealItemPosition(position);

        if (ITEM_TYPE_NORMAL == type) {
            mholder.itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(realPosition);
                }
            });
        }

        onBindViewHolder_impl(mholder, type, position, realPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else
            onBindViewHolderPayLoads(holder, getItemViewType(position), position, getRealItemPosition(position), payloads);
    }

    /**
     * @param viewHolder
     * @param itemType
     * @param original_position
     * @param real_position
     * @param payloads
     */
    public void onBindViewHolderPayLoads(RecyclerView.ViewHolder viewHolder, int itemType, int original_position, int real_position, @NonNull List<Object> payloads) {
        //do something
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    protected void setText(TextView textView, CharSequence text) {
        if (!TextUtils.isEmpty(text))
            textView.setText(text);
        else
            textView.setText("");
    }

    /**
     * 添加头部
     */
    public void addHeaderView(View view) {
        this.HeaderView = view;
    }

    /**
     * 添加底部
     */
    public void addFooterView(View view) {
        this.FooterView = view;
    }


    /**
     * 获取真正的position,因为加了头部与底部，position会有所位移
     */
    public int getRealItemPosition(int position) {
        if (null != HeaderView) {
            return position - 1;
        }
        return position;
    }

    /**
     * @return
     */
    public int getRealItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + getHeaderCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (HeaderView != null && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (FooterView != null && getItemCount() - 1 == position) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }


    /**
     * @param position
     * @return
     */
    public T getItemData(int position) {
        return mList != null && mList.size() > 0 && position < getRealItemCount() ? mList.get(position) : null;
    }

    /**
     * @param list
     */
    public void setList(List<T> list) {
        checkList();
        mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<T> getList() {
        checkList();
        return this.mList;
    }

    /**
     *
     */
    private void checkList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    public void add(T t) {
        checkList();
        mList.add(t);
        if (useHaveAnimationRefresh)
            notifyItemInserted(mList.size() - 1 + getHeaderCount());
        else
            notifyDataSetChanged();
    }

    public int getHeaderCount() {
        return HeaderView != null ? 1 : 0;
    }

    public int getFooterCount() {
        return FooterView != null ? 1 : 0;
    }

    /**
     * insert  a item associated with the specified position of adapter
     *
     * @param position
     * @param item
     */
    public void add(int position, T item) {
        checkList();
        mList.add(position, item);
        if (useHaveAnimationRefresh) {
            notifyItemInserted(position + getHeaderCount());
            notifyItemRangeChanged(position + getHeaderCount(), getItemCount() - position - getHeaderCount());
        } else
            notifyDataSetChanged();
    }


    public void addAll(List<T> newData) {
        if (newData == null)
            return;
        checkList();
        mList.addAll(newData);
        if (useHaveAnimationRefresh) {
            notifyItemRangeInserted(mList.size() - newData.size() + getHeaderCount(), newData.size());
        } else
            notifyDataSetChanged();
    }

    public void addAll_Range(List<T> newData) {
        if (newData == null)
            return;
        checkList();
        mList.addAll(newData);
        notifyItemRangeChanged(mList.size() - newData.size() + getHeaderCount(), newData.size());
    }

    public void addAll(int position, List<T> newData) {
        checkList();
        mList.addAll(position, newData);
        if (useHaveAnimationRefresh) {
            notifyItemRangeInserted(position + getHeaderCount(), newData.size());
            notifyItemRangeChanged(position + getHeaderCount(), getItemCount() - position - getHeaderCount());
        } else
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
        checkList();
        mList.add(position, data);
        if (useHaveAnimationRefresh) {
            notifyItemInserted(position + getHeaderCount());
            notifyItemRangeChanged(position + getHeaderCount(), getItemCount() - position - getHeaderCount());
        } else
            notifyDataSetChanged();
    }

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
                if (position < mList.size()) {
                    mList.remove(position);
                    if (useHaveAnimationRefresh) {
                        notifyItemRemoved(position + getHeaderCount());
                        notifyItemRangeChanged(position + getHeaderCount(), getItemCount() - position - getHeaderCount());
                    } else
                        notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }


    protected abstract RecyclerView.ViewHolder onCreateViewHolder_impl(ViewGroup parent, int viewType);

    /**
     * 加了header后，position会有所不同,以下是说明
     *
     * @param itemType          判断是headview，footerView的标识
     * @param original_position 原始的position
     * @param real_position     真正的position，
     */
    protected abstract void onBindViewHolder_impl(RecyclerView.ViewHolder mholder, int itemType, int original_position, int real_position);
}