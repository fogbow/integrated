package cloud.fogbow.rcs.core.service.cache;

public class CacheServiceHolder {
    private static CacheService<String> cacheService;
    private static CacheServiceHolder instance;

    private CacheServiceHolder() {
        this.cacheService = new MemoryBasedCache<String>();
    }

    public static CacheServiceHolder getInstance() {
        if (instance == null) {
            instance = new CacheServiceHolder();
        }
        return instance;
    }

    public CacheService<String> getCacheService() {
        return this.cacheService;
    }
}
