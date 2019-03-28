package com.voxelwind.server.network.mcpe.util;

import lombok.Value;

// TODO: move to api?
@Value
public class EntityLink {
    private final long from;
    private final long to;
    private final Type type;
    private final boolean immediate;

    public enum Type {
        REMOVE,
        RIDER,
        PASSENGER
    }
}