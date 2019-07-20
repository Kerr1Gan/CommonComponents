package com.common.componentes.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LazyInitFragment extends Fragment {

    private static final String TAG = "LazyInitFragment";

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(TAG, "setUserVisibleHint " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        mHandler.post(() -> {
            onUserVisibleHintChanged(getUserVisibleHint());
        });
    }

    public void onUserVisibleHintChanged(boolean isVisibleToUser) {
        Log.i(TAG, "onUserVisibleHintChanged " + isVisibleToUser);
    }
}
