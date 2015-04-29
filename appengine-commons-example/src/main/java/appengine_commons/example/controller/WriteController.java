package appengine_commons.example.controller;

import appengine_commons.counter.WriteCountPullTaskDelegate;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import java.util.logging.Logger;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class WriteController extends Controller {
    static final Logger logger = Logger.getLogger(WriteController.class.getSimpleName());

    @Override
    protected Navigation run() throws Exception {
        WriteCountPullTaskDelegate.doWriteCount(new WriteCountPullTaskDelegate.CountWriter() {
            @Override
            public void addCount(String key, int count) {
                logger.info("key: " + key + " count: " + count);
            }
        });

        response.getWriter().write("ok");
        return null;
    }
}
