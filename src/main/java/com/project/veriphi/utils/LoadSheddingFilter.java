package com.project.veriphi.utils;

import com.sun.management.OperatingSystemMXBean;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class LoadSheddingFilter implements Filter {

    private final OperatingSystemMXBean osBean =
            ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private final double cpuThreshold = 0.85;   // 80%
    private final double memThreshold = 0.85;   // 80%

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        double cpuLoad = osBean.getCpuLoad();
        long totalMem = osBean.getTotalMemorySize();
        long freeMem = osBean.getFreeMemorySize();
        double memUsage = (double) (totalMem - freeMem) / totalMem;

        if (cpuLoad > cpuThreshold || memUsage > memThreshold) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Server under heavy load. Try again later."
            );
            return;
        }

        chain.doFilter(request, response);
    }
}
