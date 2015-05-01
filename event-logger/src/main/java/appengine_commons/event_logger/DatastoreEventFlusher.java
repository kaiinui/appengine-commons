package appengine_commons.event_logger;


import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * An event flusher implementation that flushes logs to datastore.
 *
 * As default, it flushes events as "ACEvent" kind, "tag" String property, "params" Map property.
 * You can change the kind with constructor parameter.
 *
 * <pre><code>
 *     new DatastoreEventFlusher("SomeKind");
 * </code></pre>
 */
public class DatastoreEventFlusher implements WriteEventPullTaskDelegate.EventFlusher {
    public static final String KIND_DEFAULT = "ACEvent";

    public static final String DELIMITER = "<$@$>";

    private final AsyncDatastoreService datastoreService = DatastoreServiceFactory.getAsyncDatastoreService();
    private final String kind;

    public DatastoreEventFlusher() {
        this.kind = KIND_DEFAULT;
    }

    public DatastoreEventFlusher(String kind) {
        this.kind = kind;
    }

    @Override
    public void flushEvents(List<Events.Event> events) {
        final List<Entity> entities = makeEntitiesFromEvents(kind, events);
        datastoreService.put(entities);
    }

    private static List<Entity> makeEntitiesFromEvents(String kind, List<Events.Event> events) {
        final ArrayList<Entity> entities = new ArrayList<Entity>(events.size());
        for (Events.Event event : events) {
            final Entity entity = new Entity(makeKeyFromEvent(kind, event));

            entities.add(entity);
        }
        return entities;
    }

    private static Key makeKeyFromEvent(String kind, Events.Event event) {
        final String name = new StringBuilder()
                .append(new Date().getTime()) // TODO: 本当は publish 時に付与すべき
                .append(DELIMITER)
                .append(event.getTag())
                .append(DELIMITER)
                .append(new Gson().toJson(event.getParams()))
                .toString();

        return KeyFactory.createKey(kind, name);
    }
}
