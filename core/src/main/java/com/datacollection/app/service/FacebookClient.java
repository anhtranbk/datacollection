package com.datacollection.app.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FacebookClient {

    public static final String BASE_URL = "https://graph.facebook.com/v2.12/";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Image {
        public ImageData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ImageData {
        @JsonProperty("is_silhouette")
        public boolean isSilhouette;

        public String url;
    }

    public static String fetchAvatarUrl(String perAppId) throws IOException {
        return fetchAvatarUrl(perAppId, 200, 200);
    }

    @SuppressWarnings("ConstantConditions")
    public static String fetchAvatarUrl(String perAppId, int width, int height) throws IOException {
        String url = BASE_URL + perAppId + "/picture?redirect=false&width="
                + width + "&height=" + height;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return "";
            Image image = MAPPER.readValue(response.body().string(), Image.class);
            return image.data.isSilhouette ? "" : image.data.url;
        }
    }

    public static Pair<String, byte[]> fetchAvatarSource(String imgUrl) throws IOException {
        Request request = new Request.Builder()
                .url(imgUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) return null;
            return new ImmutablePair<>(response.request().url().toString(), body.bytes());
        }
    }

    public static String fetchUserIdFromUsername(String profileUrl) throws IOException {
        RequestBody req = RequestBody.create(MediaType.parse("x-www-form-urlencoded"), "url=" + profileUrl);
        Request request = new Request.Builder()
                .url("https://findmyfbid.com")
                .method("POST", req)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36")
                .header("Accept", "*/*")
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) return null;

            try {
                String content = body.string();
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(content).getAsJsonObject();
                return json.get("id").getAsString();
            } catch (RuntimeException e) {
                return null;
            }
        }
    }

    public static String fetchUserIdFromUsername_v2(String profileUrl) {
        RequestBody req = RequestBody.create(MediaType.parse("form-data"), "check=Lookup&fburl=" + profileUrl);
        Request request = new Request.Builder()
                .url("https://lookup-id.com/")
                .method("POST", req)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36")
                .header("Accept", "*/*")
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) return null;

            String content = body.string();
            String pattern = "id=\"code\">[0-9]+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(content);
            if(m.find()){
                return m.group(0).replace("id=\"code\">","");
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(fetchUserIdFromUsername("huyentrang231"));
        System.out.println(fetchUserIdFromUsername("https://www.facebook.com/lam.nguyenthanh.1654"));
        System.out.println(fetchAvatarUrl("948797981925138"));
    }
}
