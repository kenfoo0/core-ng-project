package core.framework.impl.http;

import core.framework.http.HTTPMethod;
import core.framework.log.Markers;
import core.framework.util.Threads;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

/**
 * @author neo
 */
public class RetryHandler implements HttpRequestRetryHandler {
    private final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
    private final Set<String> idempotentMethods = Set.of(HTTPMethod.GET.name(), HTTPMethod.PUT.name(), HTTPMethod.DELETE.name(),
            HTTPMethod.HEAD.name(), HTTPMethod.OPTIONS.name(), HTTPMethod.TRACE.name());
    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount > maxRetries) return false;

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpUriRequest request = (HttpUriRequest) clientContext.getRequest();

        if (retry(request, clientContext)) {
            logger.warn(Markers.errorCode("HTTP_COMMUNICATION_FAILED"), "http connection failed, retry soon");
            Duration waitTime = waitTime(executionCount);
            waitBeforeRetry(waitTime);
            return true;
        }

        return false;
    }

    boolean retry(HttpUriRequest request, HttpClientContext clientContext) {
        if (request.isAborted()) {
            return false;
        }
        return !clientContext.isRequestSent() || idempotentMethods.contains(request.getMethod());
    }

    private void waitBeforeRetry(Duration waitTime) {
        Threads.sleepRoughly(waitTime);
    }

    Duration waitTime(int executionCount) {
        return Duration.ofMillis(500 * (long) Math.pow(2, executionCount - 1));
    }
}
