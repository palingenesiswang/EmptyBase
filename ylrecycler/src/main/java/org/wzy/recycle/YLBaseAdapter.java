package org.wzy.recycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.wzy.recycle.listener.YLOnItemClickListener;
import org.wzy.recycle.listener.YLOnItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author : wzy
 * @date: 2016/8/29 09:46
 */
public abstract class YLBaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int TYPE_COMMON_VIEW = 0x00000001;
    public static final int TYPE_HEAD_VIEW = 0x00000002;
    public static final int TYPE_FOOTER_VIEW = 0x00000003;
    public static final int TYPE_EMPTY_VIEW = 0x00000004;


    private YLOnItemClickListener<T> mItemClickListener;
    private YLOnItemLongClickListener<T> mOnItemLongClickListener;

    protected Context mContext;
    protected List<T> mData;
    private boolean mOpenLoadMore;
    private boolean isAutoLoadMore = false;

    private View mLoadingView;
    private View mEmptyView;
    private LinearLayout mHeaderLayout;
    private RelativeLayout mFooterLayout;
    private int position;
    /**
     * View type
     */
    private Map<Integer, Integer> layoutIdMap, viewTypeMap;
    private int mCurrentViewTypeValue = 0x0107;


    protected abstract void convert(BaseViewHolder holder, T data);

    protected void convert(BaseViewHolder holder, T data, List payloads) {

    }

    protected abstract int getItemLayoutId(int currentPosition, T item);

    @SuppressLint("UseSparseArrays")
    public YLBaseAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data == null ? new ArrayList<T>() : data;
        layoutIdMap = new HashMap<>();
        viewTypeMap = new HashMap<>();
        mOpenLoadMore = false;
    }

    @SuppressLint("UseSparseArrays")
    public YLBaseAdapter(Context context, List<T> data, boolean isOpenLoadMore) {
        mContext = context;
        mData = data == null ? new ArrayList<T>() : data;
        layoutIdMap = new HashMap<>();
        viewTypeMap = new HashMap<>();

        mOpenLoadMore = isOpenLoadMore;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder;
        switch (viewType) {
            case TYPE_HEAD_VIEW:
                viewHolder = new BaseViewHolder(mHeaderLayout, mContext);
                break;
            case TYPE_FOOTER_VIEW:
                if (mFooterLayout == null)  //  为空
                {
                    mFooterLayout = new RelativeLayout(mContext);
                }
                viewHolder = new BaseViewHolder(mFooterLayout, mContext);
                break;
            case TYPE_EMPTY_VIEW:
                if (mEmptyView == null) //  默认为空显示
                {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
                    viewHolder = new BaseViewHolder(view, mContext);
                } else  //  定制为空显示
                {
                    viewHolder = new BaseViewHolder(mEmptyView, mContext);
                }
                break;
            case TYPE_COMMON_VIEW:
            default:
                viewHolder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(layoutIdMap.get(viewType)
                        , parent, false), mContext);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_COMMON_VIEW:
            default:
                bindCommonItem(holder, position, null);
                break;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        switch (holder.getItemViewType()) {
            case TYPE_COMMON_VIEW:
            default:
                bindCommonItem(holder, position, payloads);
                break;
        }
    }

    private void bindCommonItem(final BaseViewHolder holder, final int position, List payloads) {
        this.position = position - getHeaderViewCount();

        if (payloads == null || payloads.isEmpty()) //  整个控件刷新
        {
            convert(holder, getItem(this.position));
        } else  //  局部刷新
        {
            convert(holder, getItem(this.position), payloads);
        }

        if (mItemClickListener != null) //  非空 点击事件
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData.size() <= 0) return; //  empty view
                    mItemClickListener.onItemClick(holder, mData.get(position), position);
                }
            });
        }
        if (mOnItemLongClickListener != null) //  非空 长按点击事件
        {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mData.size() <= 0) return true; //  empty view
                    mOnItemLongClickListener.onItemLongClick(view, mData.get(position), position);
                    return true;
                }
            });
        }
    }

    /************************重写 setHasStableIds getItemId 防止数据错乱************************************/
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /************************重写 setHasStableIds getItemId 防止数据错乱************************************/

    protected int getPosition() {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mData.size() <= 0) return 1;
        return mData.size() + getFooterViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() <= 0) {
            return TYPE_EMPTY_VIEW;
        }

        if (position < getHeaderViewCount()) {
            return TYPE_HEAD_VIEW;
        }

        if (isFooterView(position)) {
            return TYPE_FOOTER_VIEW;
        }

        int currentPosition = position - getHeaderViewCount();
        int currentLayoutId = getItemLayoutId(currentPosition, mData.get(currentPosition));
        if (!viewTypeMap.containsKey(currentLayoutId)) {
            mCurrentViewTypeValue++;
            viewTypeMap.put(currentLayoutId, mCurrentViewTypeValue);
            layoutIdMap.put(viewTypeMap.get(currentLayoutId), currentLayoutId);
        }
        return viewTypeMap.get(currentLayoutId);
    }

    public T getItem(int position) {
        int headerViewCount = getHeaderViewCount();
        return mData.size() == 0 ? null : mData.get(position + headerViewCount);
    }

    /**
     * 是否是FooterView
     */
    private boolean isFooterView(int position) {
        return mOpenLoadMore && getItemCount() > 1 && position >= getItemCount() - 1;
    }


    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }


    public void addHeaderView(View header) {
        addHeaderView(header, -1);
    }

    public void addHeaderView(View header, int index) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
            mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        }
        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
        mHeaderLayout.addView(header, index);
        this.notifyDataSetChanged();
    }

    private void removeFooterView() {
        mFooterLayout.removeAllViews();
    }

    private void addFooterView(View footerView) {
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = new RelativeLayout(mContext);
        }
        removeFooterView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mFooterLayout.addView(footerView, params);
    }

    public int getHeaderViewCount() {
        return null == mHeaderLayout ? 0 : 1;
    }

    public int getFooterViewCount() {
        return null == mFooterLayout ? 0 : 1;
    }

    public void setLoadMoreData(List<T> data) {
        int size = mData.size();
        mData.addAll(data);
//        notifyItemInserted(size);   // Inconsistency detected. Invalid view holder adapter positionViewHolder
        notifyItemChanged(size >= 1 ? size - 1 : size);
    }

    public void setData(List<T> data) {
        mData = null;
        mData = new ArrayList<>();
        mData.addAll(0, data);
        notifyDataSetChanged();
    }

    public void setNewData(List<T> data) {
        if (data == null) return;
        mData = null;
        mData = new ArrayList<>();
        mData.addAll(data);
//        mData.clear();   //  会清空新输入的List？？？？？？
        notifyDataSetChanged();
    }

    public void setEmptyData() {
        mData = null;
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addPullRefreshData(List<T> datas) {
        mData.addAll(0, datas);
        notifyDataSetChanged();
    }

    /**
     * 初始化加载中布局
     */
    public void setLoadingView(View loadingView) {
        mLoadingView = loadingView;
        addFooterView(mLoadingView);
    }

    public void setLoadingView(int loadingId) {
        setLoadingView(inflate(loadingId));
    }

    /**
     * 初始加载失败布局
     */
    public void setLoadFailedView(View loadFailedView) {
        if (loadFailedView == null) {
            return;
        }
        View mLoadFailedView = loadFailedView;
        addFooterView(mLoadFailedView);
        mLoadFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFooterView(mLoadingView);
            }
        });
    }

    public void setLoadFailedView(int loadFailedId) {
        setLoadFailedView(inflate(loadFailedId));
    }

    /**
     * 初始化全部加载完成布局
     */
    public void setLoadEndView(View loadEndView) {
        addFooterView(loadEndView);
    }

    public void setLoadEndView(int loadEndId) {
        setLoadEndView(inflate(loadEndId));
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void setOnItemClickListener(YLOnItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(YLOnItemLongClickListener<T> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }


    private View inflate(int layoutId) {
        if (layoutId <= 0) {
            return null;
        }
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }
}
