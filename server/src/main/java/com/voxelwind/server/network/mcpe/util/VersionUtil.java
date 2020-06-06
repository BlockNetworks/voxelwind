package com.voxelwind.server.network.mcpe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionUtil {
    private static final int BROADCAST_PROTOCOL_VERSION = 390;
    private static final int[] COMPATIBLE_PROTOCOL_VERSIONS = new int[]{390};

    public static int[] getCompatibleProtocolVersions() {
        return COMPATIBLE_PROTOCOL_VERSIONS.clone();
    }

    public static int getBroadcastProtocolVersion() {
        return BROADCAST_PROTOCOL_VERSION;
    }

    public static boolean isCompatible(int protocolVersion) {
        return Arrays.binarySearch(COMPATIBLE_PROTOCOL_VERSIONS, protocolVersion) >= 0;
    }

    public static String getHumanVersionName(int protocolVersion) {
        switch (protocolVersion) {
            case 390:
                return "1.14.60";
        }
        return null;
    }
}
