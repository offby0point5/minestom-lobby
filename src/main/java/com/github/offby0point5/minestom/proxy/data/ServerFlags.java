package com.github.offby0point5.minestom.proxy.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServerFlags {
    public final Set<String> flags;

    public ServerFlags(String... flags) {
        this.flags = new HashSet<>(Arrays.asList(flags));
    }

    public ServerFlags(Set<String> flags) {
        this.flags = new HashSet<>(flags);
    }
}
