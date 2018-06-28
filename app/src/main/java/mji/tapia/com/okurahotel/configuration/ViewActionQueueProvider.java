package mji.tapia.com.okurahotel.configuration;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;


public final class ViewActionQueueProvider {

    private final Scheduler mainScheduler;

    public ViewActionQueueProvider(final Scheduler mainScheduler) {
        this.mainScheduler = mainScheduler;
    }

    private final Map<String, ViewActionQueue> viewActionQueueMap = new HashMap<>();

    public ViewActionQueue queueFor(final String queueId) {
        final ViewActionQueue viewActionQueue = viewActionQueueMap.get(queueId);
        if (viewActionQueue != null) {
            return viewActionQueue;
        }

        final ViewActionQueue newQueue = new ViewActionQueue(mainScheduler);
        viewActionQueueMap.put(queueId, newQueue);
        return newQueue;
    }

    public void dispose(final String queueId) {
        viewActionQueueMap.remove(queueId);
    }
}
