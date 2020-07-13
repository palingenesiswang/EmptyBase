package org.wzy.recycle.listener;

/**
 * Author: Create by bool 2020/4/8 15:39
 * Email: dram@gmail.com
 */
public interface YLRefreshHeaderListener {

    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_REFRESH = 1;
    int STATE_REFRESHING = 2;
    int STATE_DONE = 3;

    void onMove(float delta);

    boolean releaseAction();

    void refreshComplete();

}
