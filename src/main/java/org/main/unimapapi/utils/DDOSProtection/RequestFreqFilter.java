package org.main.unimapapi.utils.DDOSProtection;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Servlet filter that provides basic DDoS protection by limiting the frequency
 * of requests per IP address.
 *
 * <p>If a client exceeds {@value RATE_LIMIT} requests per second, the IP is blocked
 * for {@value BLOCK_TIME} milliseconds and receives HTTP 429 (Too Many Requests).</p>
 *
 * <p>This filter is prioritized to be applied early in the request chain via {@code @Order(1)}.</p>
 */
@Component
@Order(1)
public class RequestFreqFilter implements Filter {

    /**
     * Initializes the clearing scheduler to remove expired blocked IPs every minute.
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // Every n amount of seconds, the data should be wiped
        // To allow a user to retry the attempt of connecting

        clearingScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();

            // Clearing the IP array to make sure it will not cause
            // the memory leak
            blockedIPs.entrySet().removeIf
                    (entry ->
                            now - entry.getValue() > BLOCK_TIME);
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Map for counting requests from IP addresses.
     */
    private final ConcurrentHashMap<String, AtomicLong> reqCount
                                        = new ConcurrentHashMap<>();

    /**
     * Map for blocked IP addresses with timestamp of block.
     */
    private final ConcurrentHashMap<String, Long> blockedIPs = new ConcurrentHashMap<>();

    /**
     * Max allowed requests per second from one IP.
     */
    private static final byte RATE_LIMIT = 6;

    /**
     * Time in milliseconds an IP will be blocked after exceeding the limit.
     */
    private static final int  BLOCK_TIME = 30 * 1000;

    /**
     * Scheduled task executor that clears expired blocked IP entries every minute.
     */
    private final ScheduledExecutorService clearingScheduler =
                                           Executors.newScheduledThreadPool(1);


    /**
     * Main filtering logic that checks IP request frequency.
     * <ul>
     *   <li>Returns 429 if IP is temporarily blocked.</li>
     *   <li>Increments request count for IP.</li>
     *   <li>Blocks and returns 429 if limit exceeded.</li>
     *   <li>Passes to next filter if within limit.</li>
     * </ul>
     *
     * @param req   incoming servlet request
     * @param res   servlet response
     * @param chain filter chain
     * @throws IOException      if response writing fails
     * @throws ServletException if the next filter fails
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        // There the sender ip address gets acknowledged
        String ipAddress = req.getRemoteAddr();
        HttpServletResponse httpResponse = (HttpServletResponse) res;

        if (blockedIPs.containsKey(ipAddress)) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write(
                    "Suspicious activity is being detected from your device. " +
                            "Try again later.");
            return;
        }

        // Counter to get the count of the individual IP occurrence on the server
        // Will work in case the IP is not there yet (absent)
        AtomicLong count = reqCount.computeIfAbsent(ipAddress,
                                                 _k -> new AtomicLong());

        // If the quantity of the requests is exceeding the Hz rate, make sure
        // To leave a user without the data to stop him from overloading the
        // Databases and other vital components of the server
        if (count.incrementAndGet() > RATE_LIMIT) {

            // The IP should be put to the container of potentially unwanted for
            // The specified before period, it will be deleted in init function
            blockedIPs.put(ipAddress, System.currentTimeMillis());

            // Now the task is to get a writer instance to write back a user
            // He is suspicious to our server because the excessive activity

            // The error 429 stands for blocking the user based on abnormalities
            httpResponse.setStatus(429);
            httpResponse.getWriter().write(
                    "Suspicious activity is being detected from your device. " +
                            "Try again later.");

            return;
        }

        chain.doFilter(req, res);
    }

    /**
     * Shuts down the IP cleanup scheduler.
     */
    @Override
    public void destroy() {
        // The scheduler should be destroyed in case of instance death
        clearingScheduler.shutdown();
    }
}