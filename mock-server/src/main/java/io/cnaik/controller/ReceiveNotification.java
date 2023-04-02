package io.cnaik.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ReceiveNotification {

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<String> receiveNotification(
            @RequestParam String key,
            @RequestParam(required = false) String threadKey,
            @RequestBody String notificationRequest) {

        log.info("Receiving notification for thread key {}:\n{}", threadKey, notificationRequest);

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
