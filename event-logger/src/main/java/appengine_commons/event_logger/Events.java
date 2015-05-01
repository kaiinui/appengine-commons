package appengine_commons.event_logger;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * An structured-data logger that stores logs separated from access logs.
 *
 * You can log an event with following code snippet.
 * It does not blocks the thread, does not occurs datastore write operation, neither do any heavy operations.
 *
 * <pre><code>
 *     Events.withTag("view")
 *         .param("path", "photos/23784")
 *         .publish();
 * </code></pre>
 *
 * The events will be buffered at the "ac-events" pull-queue.
 * The flush cron task will flushes them to some databases such as BigQuery, Datastore.
 * Target databases are fully opt-in.
 */
public class Events {
    public static final String PULL_QUEUE_NAME = "ac-events";

    public static EventBuilder withTag(String tag) {
        return new EventBuilder(tag);
    }

    private Events() {}

    public static class EventBuilder {
        private String tag;
        private HashMap<String, Object> params = new HashMap<String, Object>();

        protected EventBuilder(String tag) {
            this.tag = tag;
        }

        public EventBuilder param(String key, String value) {
            this.params.put(key, value);

            return this;
        }

        public EventBuilder param(String key, int value) {
            this.params.put(key, value);

            return this;
        }

        public EventBuilder param(String key, float value) {
            this.params.put(key, value);

            return this;
        }

        public EventBuilder param(String key, double value) {
            this.params.put(key, value);

            return this;
        }

        public EventBuilder param(String key, boolean value) {
            this.params.put(key, value);

            return this;
        }

        public void publish() {
            this.build().publish();
        }

        private Event build() {
            return new Event(this.tag, this.params);
        }
    }

    public static class Event {
        private final String tag;
        private final Map<String, Object> params;

        protected Event(String tag, Map<String, Object> params) {
            this.tag = tag;
            this.params = params;
        }

        protected void publish() {
            final TaskOptions options = makeTaskOption();
            QueueFactory.getQueue(PULL_QUEUE_NAME).addAsync(options);
        }

        private TaskOptions makeTaskOption() {
            return TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
                    .tag(tag)
                    .payload(new Gson().toJson(params));
        }

        public String getTag() {
            return tag;
        }

        public Map<String, Object> getParams() {
            return params;
        }
    }
}
