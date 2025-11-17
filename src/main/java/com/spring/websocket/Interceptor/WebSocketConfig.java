package com.spring.websocket.Interceptor;
// 2. WebSocketConfig.java
// WebSocket ayarları: /ws ile bağlan, mesajlar /app ile işlenir, yayın /ws ile


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.spring.websocket.Interceptor.CustomHandshakeInterceptor;
import com.spring.websocket.Interceptor.RequestInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomHandshakeInterceptor customHandshakeInterceptor;
    private final RequestInterceptor requestInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/ws");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(customHandshakeInterceptor);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(requestInterceptor);
    }

    // ONEMLI NOT
    // websocket /ws kanalından alıyor handshake yapıyor handshakede ben ıstersem verılerı kaydedebılıyorum. ben eğer handshakede http baglantısına ızın verırsem
    // Websocket RequestInterceptoru calıstıyor ve RequestInterceptor databaseye kaydedıp en sonki endpointe ulaşan izni veriyorgidiyor bilgiler
    // Blacklıst ip taraması HandshakeInterceptor de yapılmalı
}