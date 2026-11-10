package storage;

import model.CachedString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageHandler {
    private final Map<String, CachedString> stringsMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> listsMap = new ConcurrentHashMap<>();

    public Map<String, CachedString> getStringsMap() {
        return stringsMap;
    }

    public Map<String, List<String>> getListsMap() {
        return listsMap;
    }
}
