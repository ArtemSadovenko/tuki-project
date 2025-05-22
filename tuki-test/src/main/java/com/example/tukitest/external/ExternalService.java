package com.example.tukitest.external;

import com.example.tukitest.external.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;


@Service
public class ExternalService {

    @Value("${external.api.key}")
    private String apiKey;
    @Value("${external.api.secret}")
    private String apiSecret;
    private String endpointUrl = "https://api.imagga.com/v2/tags";
    private ObjectMapper objectMapper = new ObjectMapper();

    private String upload(MultipartFile file) throws IOException {
        String endpoint = "/uploads";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "Image Upload";

        URL urlObject = new URL("https://api.imagga.com/v2" + endpoint);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", basicAuth(apiKey, apiSecret));
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" +
                file.getOriginalFilename() + "\"" + crlf);
        request.writeBytes(crlf);

        byte[] fileBytes = file.getBytes();
        request.write(fileBytes);

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        InputStream responseStream = new BufferedInputStream(connection.getInputStream());
        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();

        String response = stringBuilder.toString();
        System.out.println(response);

        responseStream.close();
        connection.disconnect();

        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getJSONObject("result").getString("upload_id");
    }

    private ResponseDTO getImageTags(String imageId) throws IOException {
        String endpoint_url = "https://api.imagga.com/v2/tags";

        String url = endpoint_url + "?image_upload_id=" + imageId;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", basicAuth(apiKey, apiSecret));

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        System.out.println(jsonResponse);

        return objectMapper.readValue(jsonResponse, ResponseDTO.class);

    }


    public ResponseDTO getTags(MultipartFile file) {
        try {
            return getImageTags(upload(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String basicAuth(String apiKey, String apiSecret) {
        String auth = apiKey + ":" + apiSecret;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
