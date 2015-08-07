package org.kuali.kfs.module.purap.rest;

import com.sun.jersey.api.container.filter.LoggingFilter;
import org.kuali.kfs.sys.rest.BackdoorResource;
import org.kuali.kfs.sys.rest.PreferencesResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("purap")
public class PurapApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> clazzes = new HashSet<>();

    public PurapApplication() {
        singletons.add(new MyOrdersResource());
        clazzes.add(LoggingFilter.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
