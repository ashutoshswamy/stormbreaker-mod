package com.stormbreaker.client;

public final class ClientStormData {
    private static int thunderRemaining;
    private static int thunderCooldown;
    private static int bifrostCooldown;

    private ClientStormData() {
    }

    public static int thunderRemaining() {
        return thunderRemaining;
    }

    public static int thunderCooldown() {
        return thunderCooldown;
    }

    public static int bifrostCooldown() {
        return bifrostCooldown;
    }

    public static void update(int thunderRemaining, int thunderCooldown, int bifrostCooldown) {
        ClientStormData.thunderRemaining = thunderRemaining;
        ClientStormData.thunderCooldown = thunderCooldown;
        ClientStormData.bifrostCooldown = bifrostCooldown;
    }
}
