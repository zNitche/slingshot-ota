package com.github.znitche.SlingshotUpdater.managers;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.github.znitche.SlingshotUpdater.classes.AppStateListener;

public class AppStateManager implements DefaultLifecycleObserver {
    private final AppStateListener listener;
    private Boolean isInBackground = Boolean.FALSE;

    public Boolean getIsInBackground() {
        return this.isInBackground;
    }

    public AppStateManager() {
        this(null);
    }

    public AppStateManager(AppStateListener listener) {
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(this);

        this.listener = listener;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (listener != null) {
            listener.onForeground();
        }
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        if (listener != null) {
            listener.onBackground();
        }
    }
}