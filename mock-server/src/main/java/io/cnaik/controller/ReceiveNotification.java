package io.cnaik.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ReceiveNotification {

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<String> receiveNotification(@RequestParam String key, @RequestBody String notificationRequest) {
        log.info("Receiving:\n{}", notificationRequest);

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
