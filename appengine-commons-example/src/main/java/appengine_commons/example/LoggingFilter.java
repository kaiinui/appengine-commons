package appengine_commons.example;

import appengine_commons.logger_filter.RequestResponseLoggingServletFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class LoggingFilter extends RequestResponseLoggingServletFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Logger.getLogger(LoggingFilter.class.getSimpleName()).warning("hoge!!!");

        super.doFilter(request, response, chain);
    }
}
