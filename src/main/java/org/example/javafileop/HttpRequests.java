//package org.example.javafileop;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileNotFoundException;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//
//
//class HttpRequests {
//    private final String api_key;
//    private final String api_id;
//    private final int timeout;
//
//
//    public HttpRequests(String api_key, String api_id, int timeout) {
//        if (api_key == null || api_key.isEmpty() || api_id == null || api_id.isEmpty()) {
//            throw new IllegalArgumentException("API key and ID cannot be null or empty");
//        }
//        if (timeout <= 0) {
//            throw new IllegalArgumentException("Timeout must be greater than 0");
//        }
//        this.api_key = api_key;
//        this.api_id = api_id;
//        this.timeout = timeout;
//    }
//
//    public void printData() {
//        System.out.printf("API Key: %s, API ID: %s, Timeout: %d%n", api_key, api_id, timeout);
//    }
//
//
//    public void AuthenticateHttps() {
//
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("https://example.com");
//
//        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
//            System.out.println("Status Code: " + response.getStatusLine().getStatusCode());
//            String responseBody = EntityUtils.toString(response.getEntity());
//            System.out.println("Response Body: " + responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        String fileName = "src/main/resources/secrets.json";
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            // Load credentials from a JSON file
//            File file = new File(fileName);
//            if (!file.exists()) {
//                throw new FileNotFoundException("File not found: " + fileName);
//            }
//
//            JsonNode jsonNode = mapper.readTree(file);
//            System.out.println(jsonNode.getNodeType());
//
//            String apiKey = jsonNode.get("apikey").asText();
//            String apiId = jsonNode.get("apiid").asText();
//            int timeout = jsonNode.has("timeout") ? jsonNode.get("timeout").asInt() : 30; // Default timeout
//
//            HttpRequests httpRequests = new HttpRequests(apiKey, apiId, timeout);
//
//        } catch (Exception e) {
//            System.err.println("Failed to process credentials: " + e.getMessage());
//        } finally {
//            System.out.println("Execution completed.");
//        }
//    }
//}