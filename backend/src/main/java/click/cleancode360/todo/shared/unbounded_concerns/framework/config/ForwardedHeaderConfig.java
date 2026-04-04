package click.cleancode360.todo.shared.unbounded_concerns.framework.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class ForwardedHeaderConfig {

    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter(
            @Value("${app.public-url:}") String publicUrl) {

        ForwardedHeaderFilter filter = publicUrl.isBlank()
                ? new ForwardedHeaderFilter()
                : new PublicUrlForwardedHeaderFilter(publicUrl);

        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    private static class PublicUrlForwardedHeaderFilter extends ForwardedHeaderFilter {

        private final String scheme;
        private final String host;

        PublicUrlForwardedHeaderFilter(String publicUrl) {
            URI uri = URI.create(publicUrl);
            this.scheme = uri.getScheme();
            this.host = uri.getHost();
        }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain)
                throws ServletException, IOException {
            super.doFilterInternal(new OverriddenRequest(request, scheme, host), response, filterChain);
        }
    }

    private static class OverriddenRequest extends HttpServletRequestWrapper {

        private final String scheme;
        private final String host;

        OverriddenRequest(HttpServletRequest request, String scheme, String host) {
            super(request);
            this.scheme = scheme;
            this.host = host;
        }

        @Override
        public String getHeader(String name) {
            if ("X-Forwarded-Proto".equalsIgnoreCase(name)) return scheme;
            if ("X-Forwarded-Host".equalsIgnoreCase(name)) return host;
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("X-Forwarded-Proto".equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(scheme));
            }
            if ("X-Forwarded-Host".equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(host));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            Enumeration<String> original = super.getHeaderNames();
            while (original.hasMoreElements()) {
                names.add(original.nextElement());
            }
            names.add("X-Forwarded-Proto");
            names.add("X-Forwarded-Host");
            return Collections.enumeration(names);
        }
    }
}
