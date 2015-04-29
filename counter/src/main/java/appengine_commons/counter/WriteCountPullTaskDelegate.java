package appengine_commons.counter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class WriteCountPullTaskDelegate {
    public interface CountWriter {
        public void addCount(String key, int count);
    }

    public static void doWriteCount(CountWriter countWriter) throws Exception {
        final Queue queue = QueueFactory.getQueue(Counter.COUNTER_PULL_QUEUE_NAME);
        final int enumerateCount = (queue.fetchStatistics().getNumTasks() / 1000) + 1;
        for (int i = 0; i < enumerateCount; i++) {
            final List<TaskHandle> tasks = queue.leaseTasks(3600, TimeUnit.SECONDS, 1000); // It can fetch up to 1000 tasks.
            final Map<String, AtomicLong> countMap = makeCountMapFromTaskList(tasks);
            for (String key : countMap.keySet()) {
                countWriter.addCount(key, countMap.get(key).intValue());
            }
            queue.deleteTask(tasks);
        }
    }

    private static Map<String, AtomicLong> makeCountMapFromTaskList(List<TaskHandle> taskHandles) throws UnsupportedEncodingException {
        final ConcurrentHashMap<String, AtomicLong> countMap = new ConcurrentHashMap<String, AtomicLong>();

        for (TaskHandle taskHandle : taskHandles) {
            final String key = taskHandle.getTag();
            countMap.putIfAbsent(key, new AtomicLong(0));
            countMap.get(key).incrementAndGet();
        }

        return countMap;
    }
}
