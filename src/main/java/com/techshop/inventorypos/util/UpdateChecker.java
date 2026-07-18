package com.techshop.inventorypos.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.Desktop;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight update-check mechanism for a desktop app that has no built-in
 * updater (unlike a web app, a packaged .exe can't "hot swap" itself).
 *
 * How it works:
 *  1. You host a tiny JSON file somewhere public and free, e.g. a GitHub repo
 *     (raw.githubusercontent.com/<you>/<repo>/main/version.json):
 *       { "version": "1.1.0", "downloadUrl": "https://github.com/<you>/<repo>/releases/latest" }
 *  2. On startup, this class fetches that file and compares "version" against
 *     AppVersion.CURRENT.
 *  3. If the remote version is newer, it shows an alert with a link the user
 *     clicks to download the new installer manually (simplest, safest approach -
 *     silently replacing a running .exe on Windows is fragile and usually
 *     needs a separate installer/updater process, which is overkill here).
 */
public class UpdateChecker {

    // TODO: replace with your own hosted version.json URL once you publish releases
    private static final String VERSION_CHECK_URL =
            "https://raw.githubusercontent.com/CJY333555/Inventory-and-Sales-Management-Application/refs/heads/main/version.json";

    public static void checkForUpdatesAsync() {
        Thread thread = new Thread(UpdateChecker::checkNow);
        thread.setDaemon(true);
        thread.start();
    }

    private static void checkNow() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERSION_CHECK_URL))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return;

            String remoteVersion = extractJsonValue(response.body(), "version");
            String downloadUrl = extractJsonValue(response.body(), "downloadUrl");
            if (remoteVersion == null) return;

            if (isNewer(remoteVersion, AppVersion.CURRENT)) {
                Platform.runLater(() -> showUpdateAlert(remoteVersion, downloadUrl));
            }
        } catch (Exception e) {
            // Silently ignore - no internet, DNS fail, host not set up yet, etc.
            // An update check should never crash or interrupt the app.
        }
    }

    private static void showUpdateAlert(String newVersion, String downloadUrl) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Available");
        alert.setHeaderText("A new version (" + newVersion + ") is available!");
        alert.setContentText("You're currently on v" + AppVersion.CURRENT +
                ". Click OK to open the download page.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK && downloadUrl != null) {
            try {
                Desktop.getDesktop().browse(new URI(downloadUrl));
            } catch (Exception ignored) {}
        }
    }

    /** Very small "1.2.10" > "1.2.9" style comparator - no external library needed. */
    private static boolean isNewer(String remote, String current) {
        String[] r = remote.split("\\.");
        String[] c = current.split("\\.");
        int len = Math.max(r.length, c.length);
        for (int i = 0; i < len; i++) {
            int rv = i < r.length ? parseIntSafe(r[i]) : 0;
            int cv = i < c.length ? parseIntSafe(c[i]) : 0;
            if (rv != cv) return rv > cv;
        }
        return false;
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    /** Minimal JSON field extractor - avoids pulling in a JSON library for one tiny file. */
    private static String extractJsonValue(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
