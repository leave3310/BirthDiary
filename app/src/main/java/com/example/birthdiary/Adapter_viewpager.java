package com.example.birthdiary;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class Adapter_viewpager extends PagerAdapter {
    private List<View> mListViews;
    public Adapter_viewpager(List<View> mListViews){
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view = mListViews.get(position);
        container.addView(view);
        return view;
    }


    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }
}
