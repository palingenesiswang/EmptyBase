package org.wzy.recycle.listener;

import android.view.View;

public interface YLOnItemLongClickListener<T> {
    void onItemLongClick(View view, T item, int position);
}