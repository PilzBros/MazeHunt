package com.pilzbros.mazehunt.io;

import com.pilzbros.mazehunt.MazeHunt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Update {
    private final int projectID;
    private final String apiKey;
    private static final String API_NAME_VALUE = "name";
    private static final String API_LINK_VALUE = "downloadUrl";
    private static final String API_RELEASE_TYPE_VALUE = "releaseType";
    private static final String API_FILE_NAME_VALUE = "fileName";
    private static final String API_GAME_VERSION_VALUE = "gameVersion";
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";

    public Update(int projectID) {
        this(projectID, (String) null);
    }

    public Update(int projectID, String apiKey) {
        this.projectID = projectID;
        this.apiKey = apiKey;
        this.query();
    }

    public void query() {
        URL url = null;

        try {
            url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + this.projectID);
        } catch (MalformedURLException var13) {
            var13.printStackTrace();
            return;
        }

        try {
            URLConnection conn = url.openConnection();
            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }

            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);
            if (array.size() > 0) {
                JSONObject latest = (JSONObject) array.get(array.size() - 1);
                String versionName = (String) latest.get("name");
                String versionLink = (String) latest.get("downloadUrl");
                String versionType = (String) latest.get("releaseType");
                String versionFileName = (String) latest.get("fileName");
                String versionGameVersion = (String) latest.get("gameVersion");
                if (Double.parseDouble(versionName) > Double.parseDouble("1.5")) {
                    System.out.println("The latest version of " + versionFileName + " is " + versionName + ", a " + versionType.toUpperCase() + " for " + versionGameVersion + ", available at: " + versionLink);
                    System.out.println("");
                    MazeHunt.updateNeeded = true;
                } else {
                    System.out.println("[SandFall] You're running the latest version!");
                }
            } else {
                System.out.println("[SandFall] You are running the latest version");
            }

        } catch (IOException var12) {
            var12.printStackTrace();
        }
    }
}