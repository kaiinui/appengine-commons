package appengine_commons.example.controller;

import appengine_commons.event_logger.DatastoreEventFlusher;
import appengine_commons.event_logger.WriteEventPullTaskDelegate;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

/**
 * Created by kaiinui on 2015/05/02.
 */
public class FlushEventController extends Controller {
    @Override
    protected Navigation run() throws Exception {
        WriteEventPullTaskDelegate.flushEvents(new DatastoreEventFlusher());

        response.getWriter().write("ok");
        return null;
    }
}
