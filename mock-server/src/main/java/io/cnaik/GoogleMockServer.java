package io.cnaik;

import java.util.Scanner;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GoogleMockServer {

    private static final String EXIT_LISTENER_PARAM = "exitListener";

    public static void main(String[] args) {
        SpringApplication.run(GoogleMockServer.class, args);
    }

    // @see https://stackoverflow.com/a/54220104/3228529
    @Bean
    public ApplicationRunner systemExitListener(ConfigurableApplicationContext applicationContext) {
        return args -> {
            if (args.getOptionValues(EXIT_LISTENER_PARAM) != null) {
                log.info("Press Enter to exit application");
                new Scanner(System.in).nextLine();
                log.info("Exiting");
                applicationContext.stop();
                applicationContext.close();
            }
        };
    }
}
