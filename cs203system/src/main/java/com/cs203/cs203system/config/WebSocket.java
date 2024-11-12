    package com.cs203.cs203system.config;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.Ordered;
    import org.springframework.core.annotation.Order;
    import org.springframework.messaging.converter.DefaultContentTypeResolver;
    import org.springframework.messaging.converter.MappingJackson2MessageConverter;
    import org.springframework.messaging.converter.MessageConverter;
    import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    import java.util.List;

    import static org.springframework.http.MediaType.APPLICATION_JSON;
    /**
     * WebSocket configuration class for enabling and configuring WebSocket message handling.
     *
     * This class sets up the message broker, STOMP endpoints, argument resolvers, and message converters
     * for WebSocket communication.
     */
    @Configuration
    @EnableWebSocketMessageBroker
    @Order(Ordered.HIGHEST_PRECEDENCE + 99)
    @RequiredArgsConstructor
    public class WebSocket implements WebSocketMessageBrokerConfigurer {

        /**
         * Configures the message broker options.
         *
         * This sets up a simple message broker with a user-specific destination prefix
         * and an application destination prefix for routing messages.
         *
         * @param registry the {@link MessageBrokerRegistry} to configure
         */
        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/user");
            registry.setApplicationDestinationPrefixes("/app");
            registry.setUserDestinationPrefix("/user");
        }

        /**
         * Registers STOMP endpoints for WebSocket connections.
         *
         * This method sets up an endpoint at "/ws" with SockJS support, allowing connections
         * from the specified origin (e.g., "http://localhost:3000" for a Next.js app).
         *
         * @param registry the {@link StompEndpointRegistry} to configure
         */
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry
                    .addEndpoint("/ws")
                    .setAllowedOrigins("http://localhost:3000")
                    .withSockJS();
        }

        /**
         * Adds argument resolvers for WebSocket message handling.
         *
         * This adds an argument resolver to resolve the current authenticated principal in message handling methods.
         *
         * @param argumentResolvers the list of {@link HandlerMethodArgumentResolver} to configure
         */
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
        }

        /**
         * Configures message converters for WebSocket communication.
         *
         * This sets up a JSON message converter with a default content type of "application/json",
         * using an {@link ObjectMapper} for JSON serialization and deserialization.
         *
         * @param messageConverters the list of {@link MessageConverter} to configure
         * @return false to add default message converters after the custom converters
         */
        @Override
        public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
            DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
            resolver.setDefaultMimeType(APPLICATION_JSON);
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(new ObjectMapper());
            converter.setContentTypeResolver(resolver);
            messageConverters.add(converter);
            return false;
        }
    }
