package appengine_commons.example.controller;

import appengine_commons.counter.Counter;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class AddController extends Controller {
    @Override
    protected Navigation run() throws Exception {

        for (int i = 0; i < 10; i++) {
            Counter.increment("hoge");
        }

        for (int i = 0; i < 100; i++) {
            Counter.increment("fuga");
        }

        for (int i = 0; i < 300; i++) {
            Counter.increment("goga");
        }

        response.getWriter().write("ok");
        return null;
    }
}
