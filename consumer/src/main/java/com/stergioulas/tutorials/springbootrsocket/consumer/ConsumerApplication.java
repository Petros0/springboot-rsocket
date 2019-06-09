package com.stergioulas.tutorials.springbootrsocket.consumer;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.*;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    @SneakyThrows
    RSocket rSocket() {
        return RSocketFactory.connect()
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .transport(TcpClientTransport.create(new InetSocketAddress("127.0.0.1", 7000)))
                .start()
                .block();
    }

    @Bean
    RSocketRequester requester(RSocketStrategies strategies) {
        return RSocketRequester.wrap(
                rSocket(), MimeTypeUtils.APPLICATION_JSON, strategies
        );
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


@RequiredArgsConstructor
@RestController
class GreetingsRestController {

    private final RSocketRequester requester;

    @GetMapping("/greet/{name}")
    public Publisher<GreetingsResponse> greet(@PathVariable String name) {
        return requester
                .route("greet")
                .data(new GreetingsRequest(name))
                .retrieveMono(GreetingsResponse.class);
    }


    @GetMapping(value = "/greet-stream/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<GreetingsResponse> greetStream(@PathVariable String name) {
        return requester
                .route("greet-stream")
                .data(new GreetingsRequest(name))
                .retrieveFlux(GreetingsResponse.class);
    }
}