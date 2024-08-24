package pl.trayz.proxy.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Trayz
 **/
public class Cache {

    public static final com.google.common.cache.Cache<String, Byte> lastConnections = CacheBuilder
            .newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public static final com.google.common.cache.Cache<String, Byte> timeBlocked = CacheBuilder
            .newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    public static final List<String> blocked = Lists.newArrayList();

}
