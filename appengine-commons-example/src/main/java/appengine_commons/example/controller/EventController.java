package appengine_commons.example.controller;

import appengine_commons.event_logger.Events;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

/**
 * Created by kaiinui on 2015/05/02.
 */
public class EventController extends Controller {
    @Override
    protected Navigation run() throws Exception {
        Events.withTag("view")
                .param("path", "event")
                .publish();

        response.getWriter().write("ok");
        return null;
    }
}
