package by.bsuir.daniil.hockey_schedule.cache;


import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
@Component
public class CacheManager<K, V> extends LinkedHashMap<K, V> {

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        int maxSize = 2;
        return size() > maxSize;
    }
}

