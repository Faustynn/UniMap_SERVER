package org.main.unimapapi.utils.DDOSProtection;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

// Make sure this component to become a priority due to the importance of ddos
// Checking before even the http-routing starts
@Component
@Order(1)
public class RequestFreqFilter implements Filter {

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

    // Create a data struct to store IP data
    private final ConcurrentHashMap<String, AtomicLong> reqCount
                                        = new ConcurrentHashMap<>();

    // A map for blocked ip addresses
    private final ConcurrentHashMap<String, Long> blockedIPs = new ConcurrentHashMap<>();

    // Each second there must be equal or fewer requests from this ip address
    private static final byte RATE_LIMIT = 6;

    // Blocking time, be aware it is in milliseconds, so * 1000 is mandatory for
    // this app
    private static final int  BLOCK_TIME = 30 * 1000;

    // Scheduler for clearing the IP history to make sure the server won't be
    // way too much memory-overloaded
    private final ScheduledExecutorService clearingScheduler =
                                           Executors.newScheduledThreadPool(1);


    // Filtering should return the 429 http error in case the user is way too
    // aggressive in the request sending
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

    @Override
    public void destroy() {
        // The scheduler should be destroyed in case of instance death
        clearingScheduler.shutdown();
    }
}