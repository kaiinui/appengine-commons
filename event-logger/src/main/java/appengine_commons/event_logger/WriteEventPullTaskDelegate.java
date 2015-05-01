package appengine_commons.event_logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class provides a static method that handles event flushing cron job.
 *
 * With following code, you can flushes the events buffered in pull-queue.
 *
 * <pre><code>
 *     // cron-job-targeted servlet
 *     protected void doGet(HttpServletRequest request, HttpServletResponse response) {
 *         WriteEventPullTaskDelegate.flushEvents(new DatastoreEventFlusher());
 *     }
 * </code></pre>
 *
 * To publish events, refer {@link appengine_commons.event_logger.Events}.
 */
public class WriteEventPullTaskDelegate {
    static final int MAX_FETCH_SIZE = 1000;

    public static void flushEvents(EventFlusher... flushers) throws UnsupportedEncodingException {
        final Queue queue = QueueFactory.getQueue(Events.PULL_QUEUE_NAME);
        final int enumerateCount = (queue.fetchStatistics().getNumTasks() / MAX_FETCH_SIZE) + 1;
        for (int i = 0; i < enumerateCount; i++) {
            final List<TaskHandle> tasks = queue.leaseTasks(3600, TimeUnit.SECONDS, MAX_FETCH_SIZE);
            final List<Events.Event> events = makeEventsFromTasks(tasks);

            for (EventFlusher flusher : flushers) {
                flusher.flushEvents(events);
            }

            queue.deleteTaskAsync(tasks);
        }
    }

    private static List<Events.Event> makeEventsFromTasks(List<TaskHandle> taskHandles) throws UnsupportedEncodingException {
        final ArrayList<Events.Event> events = new ArrayList<Events.Event>(taskHandles.size());
        for (TaskHandle taskHandle : taskHandles) {
            final Type mapT = new TypeToken<Map<String, Object>>() {}.getType();
            final String tag = taskHandle.getTag();
            final Map<String, Object> params = new Gson().fromJson(new String(taskHandle.getPayload()), mapT);
            events.add(new Events.Event(tag, params));
        }
        return events;
    }

    public static interface EventFlusher {
        public void flushEvents(List<Events.Event> events);
    }
}
