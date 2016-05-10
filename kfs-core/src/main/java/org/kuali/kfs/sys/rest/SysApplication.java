package org.kuali.kfs.sys.rest;

import com.sun.jersey.api.container.filter.LoggingFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api/v1/sys")
public class SysApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> clazzes = new HashSet<>();

    public SysApplication() {
        singletons.add(new PreferencesResource());
        singletons.add(new BackdoorResource());
        singletons.add(new AuthenticationResource());
        singletons.add(new SystemResource());
        singletons.add(new AuthorizationResource());
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
