package com.voxelwind.server;

import com.voxelwind.server.jni.CryptoUtil;
import com.voxelwind.server.network.Native;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class VoxelwindConfiguration {
    /**
     * Whether or not to use Xbox authentication. This setting will be overridden on a system that does not have the
     * unlimited strength JCE policy installed and 64-bit Linux is not in use. Enabling this setting will also encrypt
     * all player connections.
     */
    private boolean performXboxAuthentication;
    /**
     * The host name or IP address Voxelwind will bind to. By default, Voxelwind will bind to 0.0.0.0.
     */
    private String bindHost;
    /**
     * The port number Voxelwind will bind to. By default, Voxelwind will bind to port 19132.
     */
    private int port;
    /**
     * Configuration for the RCON service.
     */
    private RconConfiguration rcon;

    public static class RconConfiguration {
        /**
         * Whether or not RCON is enabled.
         */
        private boolean enabled;
        /**
         * The host name or IP address Voxelwind will bind to. By default, Voxelwind will bind to 0.0.0.0.
         */
        private String bindHost;
        /**
         * The port number Voxelwind will bind to. By default, Voxelwind will bind to port 19132.
         */
        private int port;
        /**
         * The password for the RCON server.
         */
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public String getBindHost() {
            return bindHost;
        }

        public int getPort() {
            return port;
        }

        public String getPassword() {
            return password;
        }

        public void clearPassword() {
            password = null;
        }
    }

    public boolean isPerformXboxAuthentication() {
        return performXboxAuthentication;
    }

    public String getBindHost() {
        return bindHost;
    }

    public int getPort() {
        return port;
    }

    public RconConfiguration getRcon() {
        return rcon;
    }

    public static VoxelwindConfiguration load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return VoxelwindServer.MAPPER.readValue(reader, VoxelwindConfiguration.class);
        }
    }

    public static void save(Path path, VoxelwindConfiguration configuration) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            VoxelwindServer.MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, configuration);
        }
    }

    public static VoxelwindConfiguration defaultConfiguration() {
        VoxelwindConfiguration configuration = new VoxelwindConfiguration();
        configuration.bindHost = "0.0.0.0";
        configuration.performXboxAuthentication = CryptoUtil.isJCEUnlimitedStrength() || Native.cipher.isLoaded();
        configuration.port = 19132;
        configuration.rcon = new RconConfiguration();
        configuration.rcon.enabled = false;
        configuration.rcon.bindHost = "127.0.0.1";
        configuration.rcon.port = 27015;
        configuration.rcon.password = generateRandomPassword();
        return configuration;
    }

    private static String generateRandomPassword() {
        BigInteger integer = new BigInteger(130, new Random());
        return integer.toString(36);
    }
}
