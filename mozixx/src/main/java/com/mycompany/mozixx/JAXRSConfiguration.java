package com.mycompany.mozixx;

import filters.CORSFilter;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;

@ApplicationPath("/resources")
public class JAXRSConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(CORSFilter.class);
        return resources;
    }
}