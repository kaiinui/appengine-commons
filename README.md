# appengine-commons

- Counter: [ ![Download](https://api.bintray.com/packages/kaiinui/maven/appengine-commons.counter/images/download.svg) ](https://bintray.com/kaiinui/maven/appengine-commons.counter/_latestVersion)
- DatastoreCallbacks: [ ![Download](https://api.bintray.com/packages/kaiinui/maven/appengine-commons.datastore-callbacks/images/download.svg) ](https://bintray.com/kaiinui/maven/appengine-commons.datastore-callbacks/_latestVersion)


🍣 Common AppEngine Code Snippets

- [Counter](https://github.com/kaiinui/appengine-commons#counter)
- [DatastoreCallbacks](https://github.com/kaiinui/appengine-commons#datastorecallbacks)
  - [KeyBaseCacheDatastoreCallbacksDelegate](https://github.com/kaiinui/appengine-commons#keybasecachedatastorecallbacksdelegate)
- [RequestResponseLoggingServletFilter](https://github.com/kaiinui/appengine-commons#requestresponseloggingservletfilter)

## Installation

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/kaiinui/maven"
    }
}

compile 'com.kaiinui.appengine-commons:counter:0.1.0'
compile 'com.kaiinui.appengine-commons:datastore-callbacks:0.1.0'
```

## Counter

* An incremental counter implementation, which is Datastore write cost friendly.
* It buffers counts on a pull queue, then a cron job perform Datastore writes.
* It provides `Counter.increment()` static method and `WriteCountPullTaskDelegate.doWriteCount()` static method.

```java
// It does not blocks the thread, does not perform datastore operation, either any heavy operations.
Counter.increment("/photos/67");
```

```java
// Cron Job
public void doGet(HttpServletRequest request, HttpServletResponse response) {
    WriteCountPullTaskDelegate.doWriteCount(new WriteCounter {
        @Override
        public void addCount(String key, int count) {
            final Page page = Datastore.get(Page.class, Datastore.createKey(Page.class, key));
            page.viewCount += count;
            Datastore.put(page);
        }
    });
}
```

## DatastoreCallbacks

### KeyBaseCacheDatastoreCallbacksDelegate

* Automatically caches entities to MemcacheService on `Get`, `BatchGet`, `Put`, `BatchPut`.
* If cache hits, it automatically returns the entity from cache.

To set the KeyBaseCache on, just put the following class on your package.

```java
public class KeyBaseCacheDatastoreCallbacks {
    @PostPut
    public void onPostPut(PutContext putContext) {
        KeyBaseCacheDatastoreCallbacksDelegate.putToCacheOnPostPut(putContext);
    }
    
    @PostLoad
    public void onPostLoad(PostLoadContext postLoadContext) {
        KeyBaseCacheDatastoreCallbacksDelegate.putToCacheOnPostLoad(postLoadContext);
    }
    
    @PostDelete
    public void onPostDelete(DeleteContext deleteContext) {
        KeyBaseCacheDatastoreCallbacksDelegate.removeFromCacheOnPostDelete(deleteContext);
    }
    
    @PreGet
    public void onPreGet(PreGetContext preGetContext) {
        KeyBaseCacheDatastoreCallbacksDelegate.getFromCacheIfHitsOnPreGet(preGetContext);
    }
}
```

## RequestResponseLoggingServletFilter

* It logs request, response payload. As defualt, AppEngine does not leave any logs about payload. It feels pretty incovenient for development, especially for former Rails developer :)
* To set the filter on, just put this filter on your `web.xml`.

```xml
<filter>
    <filter-name>LogFilter</filter-name>
    <filter-class>appengine_commons.example.LoggingFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>LogFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
