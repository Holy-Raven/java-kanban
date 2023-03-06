
package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String API_TOKEN;
    private String url;

    public KVTaskClient(String url) {
        this.url = url;
        API_TOKEN = getToken();
    }

    private String getToken() {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(url + "register?apiToken");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonElement jsonElement = JsonParser.parseString(response.body());

            return jsonElement.getAsString();

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        return null;
    }

    public void put (String key, String json) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + API_TOKEN);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public String load (String key) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        return response.body();

    }

}