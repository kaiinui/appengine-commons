package appengine_commons.event_logger;


import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.util.ArrayList;
import java.util.List;

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
    public static final String ATTR_TAG = "tag";
    public static final String ATTR_PARAMS = "params";
    public static final String KIND_DEFAULT = "ACEvent";

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
            final Entity entity = new Entity(kind);
            entity.setProperty(ATTR_TAG, event.getTag());
            entity.setUnindexedProperty(ATTR_PARAMS, event.getParams());

            entities.add(entity);
        }
        return entities;
    }
}
