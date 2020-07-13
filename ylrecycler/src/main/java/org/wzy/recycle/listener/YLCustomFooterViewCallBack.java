package org.wzy.recycle.listener;

import android.view.View;

/**
 * Author: Create by bool 2020/4/8 15:43
 * Email: dram@gmail.com
 */

public interface YLCustomFooterViewCallBack {

    void onLoadingMore(View yourFooterView);

    void onLoadMoreComplete(View yourFooterView);

    void onSetNoMore(View yourFooterView, boolean noMore);

}
