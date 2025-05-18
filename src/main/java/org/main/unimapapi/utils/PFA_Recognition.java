package org.main.unimapapi.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PFA_Recognition {
    String neuronServiceUrl = "http://localhost:8088/api/unimap/neuron/validate";

    public boolean send_to_neuron_multipart(byte[] avatar) {
        if (avatar == null || avatar.length == 0) {
            throw new IllegalArgumentException("Avatar cannot be null or empty");
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(avatar) {
                @Override
                public String getFilename() {
                    return "avatar.jpg";
                }
            };

            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    neuronServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && "\"passed\"".equalsIgnoreCase(response.getBody())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending avatar to neuron microservice: " + e.getMessage());
        }
    }

    public boolean send_to_neuron_image(byte[] avatar, String imageFormat) {
        if (avatar == null || avatar.length == 0) {
            throw new IllegalArgumentException("Avatar cannot be null or empty");
        }

        if (imageFormat == null || imageFormat.trim().isEmpty()) {
            imageFormat = "jpeg"; //deflt
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/" + imageFormat.toLowerCase()));
            headers.setContentLength(avatar.length);

            headers.set("X-Image-Type", imageFormat.toUpperCase());

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(avatar, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    neuronServiceUrl + "/process-image",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && "\"passed\"".equalsIgnoreCase(response.getBody())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending image to neuron microservice: " + e.getMessage());
        }
    }

}
