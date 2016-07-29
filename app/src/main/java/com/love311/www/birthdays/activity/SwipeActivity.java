package com.love311.www.birthdays.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.daimajia.swipe.SwipeLayout;
import com.love311.www.birthdays.R;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by Administrator on 2016/6/5.
 */
public class SwipeActivity extends AutoLayoutActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_layout);
        SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.bottom_wrapper);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
    }

}
