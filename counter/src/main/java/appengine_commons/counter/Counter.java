package appengine_commons.counter;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class Counter {
    public static final String COUNTER_PULL_QUEUE_NAME = "ac-counter";

    public static void increment(String key) {
        final TaskOptions options = TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
                .tag(key);
        QueueFactory.getQueue(COUNTER_PULL_QUEUE_NAME)
                .addAsync(options);
    }
}
