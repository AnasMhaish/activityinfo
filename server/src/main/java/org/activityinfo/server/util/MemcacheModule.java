package org.activityinfo.server.util;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MemcacheModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    public MemcacheService getMemcacheService() {
        return MemcacheServiceFactory.getMemcacheService();
    }

}
