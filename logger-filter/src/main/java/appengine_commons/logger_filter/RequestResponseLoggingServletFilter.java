package appengine_commons.logger_filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class RequestResponseLoggingServletFilter implements Filter {
    static final Logger logger = Logger.getLogger(RequestResponseLoggingServletFilter.class.getSimpleName());
    private static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final long requestId = new Random().nextInt();
        final HttpServletRequest requestToLog = new RequestWrapper(requestId, (HttpServletRequest)request);
        final ResponseWrapper responseToLog = new ResponseWrapper(requestId, (HttpServletResponse)response);

        try {
            chain.doFilter(request, response);
        } finally {
            logRequest(requestToLog);
            logResponse(responseToLog);
        }
    }

    private void logRequest(final HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        msg.append(REQUEST_PREFIX);
        if(request instanceof RequestWrapper){
            msg.append("request id=").append(((RequestWrapper)request).getId()).append("; ");
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            msg.append("session id=").append(session.getId()).append("; ");
        }
        if(request.getContentType() != null) {
            msg.append("content type=").append(request.getContentType()).append("; ");
        }
        msg.append("uri=").append(request.getRequestURI());
        if(request.getQueryString() != null) {
            msg.append('?').append(request.getQueryString());
        }

        if(request instanceof RequestWrapper && !isMultipart(request)){
            RequestWrapper requestWrapper = (RequestWrapper) request;
            try {
                String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() :
                        "UTF-8";
                msg.append("; payload=").append(new String(requestWrapper.toByteArray(), charEncoding));
            } catch (UnsupportedEncodingException e) {
                logger.log(Level.WARNING, "Failed to parse request payload", e);
            }

        }
        logger.info(msg.toString());
    }

    private boolean isMultipart(final HttpServletRequest request) {
        return request.getContentType()!=null && request.getContentType().startsWith("multipart/form-data");
    }

    private void logResponse(final ResponseWrapper response) {
        StringBuilder msg = new StringBuilder();
        msg.append(RESPONSE_PREFIX);
        msg.append("request id=").append((response.getId()));
        try {
            msg.append("; payload=").append(new String(response.toByteArray(), response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, "Failed to parse response payload", e);
        }
        logger.info(msg.toString());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
