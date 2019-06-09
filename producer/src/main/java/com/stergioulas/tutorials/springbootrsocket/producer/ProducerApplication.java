package com.stergioulas.tutorials.springbootrsocket.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingsRequest {

    private String name;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingsResponse {

    private String greeting;
}

@Controller
class GreetingsController {


    @MessageMapping("greet")
    Mono<GreetingsResponse> greet(GreetingsRequest request) {
        return Mono.just(new GreetingsResponse("Hello " + request.getName() + " @ " + Instant.now()));
    }

    @MessageMapping("greet-stream")
    Flux<GreetingsResponse> greetStream(GreetingsRequest request) {
        return Flux.fromStream(Stream.generate(
                () -> new GreetingsResponse("Hello " + request.getName() + " @ " + Instant.now())
        )).delayElements(Duration.ofSeconds(1));
    }
}


