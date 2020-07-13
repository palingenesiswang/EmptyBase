package org.wzy.recycle.listener;

import org.wzy.recycle.BaseViewHolder;

/**
 * Author: wzy
 * Time: 2016/8/29 10:48
 */
public interface YLOnItemClickListener<T> {
    void onItemClick(BaseViewHolder viewHolder, T data, int position);
}
