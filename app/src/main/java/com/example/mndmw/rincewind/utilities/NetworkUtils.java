package com.example.mndmw.rincewind.utilities;

import android.net.Uri;

import com.example.mndmw.rincewind.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mndmw on 17-11-2017.
 */

public class NetworkUtils {
    private final static String LAVAEOLUS_BASE_URL =
            "https://lavaeolus.herokuapp.com/api/";

    /**
     * Builds the URL used to query Lavaeolus.
     *
     * @return The URL to use to query Lavaeolus.
     */
    public static URL buildUrl(String... endpoints) {
        Uri.Builder builder = Uri.parse(LAVAEOLUS_BASE_URL).buildUpon();
        for(String endpoint : endpoints) {
            builder.appendPath(endpoint.toLowerCase());
        }
        Uri builtUri = builder.build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("x-auth-token", "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjM4NzE3NzEsInN1YiI6IntcInBhc3N3b3JkXCI6bnVsbCxcInVzZXJuYW1lXCI6XCJhZG1pblwiLFwiYXV0aG9yaXRpZXNcIjpbe1wiYXV0aG9yaXR5XCI6XCJBRE1JTlwifV0sXCJhY2NvdW50Tm9uRXhwaXJlZFwiOnRydWUsXCJhY2NvdW50Tm9uTG9ja2VkXCI6dHJ1ZSxcImNyZWRlbnRpYWxzTm9uRXhwaXJlZFwiOnRydWUsXCJlbmFibGVkXCI6dHJ1ZSxcInVzZXJcIjp7XCJpZFwiOjEwLFwidXNlcm5hbWVcIjpcImFkbWluXCIsXCJyb2xlXCI6XCJBRE1JTlwifSxcInJvbGVcIjpcIkFETUlOXCIsXCJpZFwiOjEwfSJ9.sZWoaVlyVwisMZuJ03iROJQFskOcj8olT1raokcwOHaI3miW4TtKNfsIPqGxqijkPGXVHBoKPlh8Z1oLtD2kHg");
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
