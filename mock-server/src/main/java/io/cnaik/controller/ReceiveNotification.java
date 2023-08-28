package io.cnaik.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ReceiveNotification {

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<String> receiveNotification(
            @RequestParam String key,
            @RequestBody String notificationRequest) {

        try {
            var requestJson = objectMapper.readValue(notificationRequest, JsonNode.class);
            var threadJson = requestJson.get("thread");
            log.info("Receiving notification for thread key {}:\n{}", threadJson != null ? threadJson.get("threadKey") : null, requestJson);
        } catch (IOException exception) {
            log.warn("Error parsing received value as JSON: {}", notificationRequest, exception);
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            int sleepTime = Integer.parseInt(key);
            log.info("sleepTime: " + sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }
}
