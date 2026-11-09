package com.github.znitche.SlingshotUpdater.classes;

public interface AppStateListener {
    default void onForeground() {};
    default void onBackground() {};
}
