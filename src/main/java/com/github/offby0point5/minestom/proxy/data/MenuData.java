package com.github.offby0point5.minestom.proxy.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuData {
    public final Entry headerEntry;
    public final LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();  // server/group name -> displaying item

    public MenuData(Entry headerEntry, Map<String, Entry> entries) {
        this.headerEntry = headerEntry;
        this.entries.putAll(entries);
    }

    public static class Entry {
        public final String itemMaterial;  // as bukkit Material name
        public final int itemAmount;
        public final String displayName;  // as MiniMessage string
        public final List<String> lore;  // as MiniMessage strings
        public final int priority;  // priority for sorting the entries
        public final Status status;  // if this entry is online, offline or has another special status

        public Entry(String itemMaterial, int itemAmount, String displayName, List<String> lore, int priority, Status status) {
            this.itemMaterial = itemMaterial;
            this.itemAmount = itemAmount;
            this.displayName = displayName;
            this.lore = lore;
            this.priority = priority;
            this.status = status;
        }
    }

    public enum Status {
        ONLINE,
        OFFLINE,
        FULL,
        EMPTY
    }
}
