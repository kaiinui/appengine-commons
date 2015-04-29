package appengine_commons.datastore_callbacks;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Created by kaiinui on 2015/04/30.
 */
public class KeyBaseCacheDatastoreCallbacksDelegate {
    /**
     * This method should be called at {@link com.google.appengine.api.datastore.PostPut} callback method.
     *
     * @param putContext
     */
    public static void putToCacheOnPostPut(PutContext putContext) {
        final Entity entity = putContext.getCurrentElement();
        putEntityAsync(entity);
    }

    /**
     * This method should be called at {@link com.google.appengine.api.datastore.PostLoad} callback method.
     *
     * @param postLoadContext
     */
    public static void putToCacheOnPostLoad(PostLoadContext postLoadContext) {
        final Entity entity = postLoadContext.getCurrentElement();
        putEntityAsync(entity);
    }

    /**
     * This method should be called at {@link com.google.appengine.api.datastore.PostDelete} callback method.
     *
     * @param deleteContext
     */
    public static void removeFromCacheOnPostDelete(DeleteContext deleteContext) {
        MemcacheServiceFactory.getAsyncMemcacheService().delete(deleteContext.getCurrentElement());
    }

    /**
     * This method should be called at {@link com.google.appengine.api.datastore.PreGet} callback method.
     *
     * @param preGetContext
     */
    public static void getFromCacheIfHitsOnPreGet(PreGetContext preGetContext) {
        final Entity entity = (Entity) MemcacheServiceFactory.getMemcacheService().get(preGetContext.getCurrentElement());
        if (entity != null) {
            preGetContext.setResultForCurrentElement(entity);
        }
    }

    private static void putEntityAsync(Entity entity) {
        MemcacheServiceFactory.getAsyncMemcacheService().put(entity.getKey(), entity);
    }
}
