package org.activityinfo.server.util;

import com.google.appengine.api.memcache.ErrorHandler;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.Stats;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MemcacheStubModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    public MemcacheService provideMemcacheService() {
        return new MemcacheService() {
            @Override
            public void setNamespace(String s) {
            }

            @Override
            public Object get(Object o) {
                return null;
            }

            @Override
            public IdentifiableValue getIdentifiable(Object o) {
                return null;
            }

            @Override
            public <T> Map<T, IdentifiableValue> getIdentifiables(Collection<T> ts) {
                return Collections.emptyMap();
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public <T> Map<T, Object> getAll(Collection<T> ts) {
                return Collections.emptyMap();
            }

            @Override
            public boolean put(Object o, Object o2, Expiration expiration, SetPolicy setPolicy) {
                return false;
            }

            @Override
            public void put(Object o, Object o2, Expiration expiration) {
            }

            @Override
            public void put(Object key, Object value) {
                assertSerializable(key);
                assertSerializable(value);

            }

            private void assertSerializable(Object object) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(object);
                } catch (IOException e) {
                    throw new AssertionError("Cannot serialize " + object, e);
                }
            }

            @Override
            public <T> Set<T> putAll(Map<T, ?> tMap, Expiration expiration, SetPolicy setPolicy) {
                return Collections.emptySet();
            }

            @Override
            public void putAll(Map<?, ?> map, Expiration expiration) {
            }

            @Override
            public void putAll(Map<?, ?> map) {
            }

            @Override
            public boolean putIfUntouched(Object o,
                                          IdentifiableValue identifiableValue,
                                          Object o2,
                                          Expiration expiration) {
                return false;
            }

            @Override
            public boolean putIfUntouched(Object o, IdentifiableValue identifiableValue, Object o2) {
                return false;
            }

            @Override
            public <T> Set<T> putIfUntouched(Map<T, CasValues> tCasValuesMap) {
                return Collections.emptySet();
            }

            @Override
            public <T> Set<T> putIfUntouched(Map<T, CasValues> tCasValuesMap, Expiration expiration) {
                return Collections.emptySet();
            }

            @Override
            public boolean delete(Object o) {
                return false;
            }

            @Override
            public boolean delete(Object o, long l) {
                return false;
            }

            @Override
            public <T> Set<T> deleteAll(Collection<T> ts) {
                return Collections.emptySet();
            }

            @Override
            public <T> Set<T> deleteAll(Collection<T> ts, long l) {
                return Collections.emptySet();

            }

            @Override
            public Long increment(Object o, long l) {
                return null;
            }

            @Override
            public Long increment(Object o, long l, Long aLong) {
                return null;
            }

            @Override
            public <T> Map<T, Long> incrementAll(Collection<T> ts, long l) {
                return Collections.emptyMap();
            }

            @Override
            public <T> Map<T, Long> incrementAll(Collection<T> ts, long l, Long aLong) {
                return Collections.emptyMap();
            }

            @Override
            public <T> Map<T, Long> incrementAll(Map<T, Long> tLongMap) {
                return Collections.emptyMap();
            }

            @Override
            public <T> Map<T, Long> incrementAll(Map<T, Long> tLongMap, Long aLong) {
                return Collections.emptyMap();
            }

            @Override
            public void clearAll() {

            }

            @Override
            public Stats getStatistics() {
                return null;
            }

            @Override
            public String getNamespace() {
                return null;
            }

            @Override
            public ErrorHandler getErrorHandler() {
                return null;
            }

            @Override
            public void setErrorHandler(ErrorHandler errorHandler) {
            }
        };
    }
}
