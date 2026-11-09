package com.github.znitche.SlingshotUpdater.managers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainLoopPoolingTask {
    private ScheduledExecutorService executor;
    private final Runnable task;
    private final Integer updaterTickInterval;
    private final Boolean isPeriodic;

    public MainLoopPoolingTask(Runnable task, Boolean isPeriodic, Integer updaterTickInterval) {
        this.task = task;
        this.updaterTickInterval = updaterTickInterval;
        this.isPeriodic = isPeriodic;
    }

    public void start() {
        this.stop();

        executor = Executors.newSingleThreadScheduledExecutor();

        if (this.isPeriodic) {
            executor.scheduleWithFixedDelay(this.task, 0, this.updaterTickInterval, TimeUnit.SECONDS);
        } else {
            executor.schedule(this.task, 0, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
