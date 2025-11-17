package com.spring.websocket.Interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.spring.websocket.repository.RequestDataRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;


// HandshakeInterceptor den requestınterceptore atıp ordan mı verıtabanına kaydedıcem
// gelen bılgıler ılk websocket ondan sonra handshakeye gıdıyor. handshake tüm bılgılerı alıyor. ondan sonra requestınterceptore gonderıp verıtabanına islettırıcem.
// requestınterceptorde ip ve bilgileri almak gereksiz
@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {


    private final RequestDataRepository requestdatarepository;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> httpservletrequestmap) { // gelen bılgılerı attırıbutese atıyor

        if (request instanceof ServletServerHttpRequest) { // eger request ServletServerHttpRequest turunde ıse
            HttpServletRequest http = ((ServletServerHttpRequest) request).getServletRequest(); // WebSocket istegını http ıstegıne cevırmeden bılgı alamayız asla


            httpservletrequestmap.put("IP", http.getRemoteAddr());

            if(requestdatarepository.existsByIpAndBlacklistTrue(http.getRemoteAddr()) == true){


                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ip Adresin ' " + requestdatarepository.findReasonByIp(http.getRemoteAddr()) + " ' Nedeniyle Blackliste Alinmistir.");

            }

            String tarayiciBilgisi = http.getHeader("User-Agent");

            if (tarayiciBilgisi == null || tarayiciBilgisi.isEmpty()){

                httpservletrequestmap.put("User-Agent", "Bilinmiyor");

            }else{

                httpservletrequestmap.put("User-Agent", tarayiciBilgisi);

            }

            // Bağlanılan URL (örnek: /ws)
            httpservletrequestmap.put("WebsocketUrl", http.getRequestURL().toString()); // burda normal urller cikmiyor /api gibi cikmiyor. nedeni de daha apiye baglanmadi http asamasinda bunlar. burdan kabul gorurse gecicek.
            //  --- ilk http ile baglanma istegi kurulur ondan sonra websocket ile endpointlere aktarilir
        }
        return true; // Bağlantıya izin ver
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // bundan sonra requestinterceptor burdaki veriyi cekicek ve veritabanna kaydedicek burda olmayacak
        // bu gonderımıde soyle yapıyoruz

        //  SimpMessageHeaderAccessor h = SimpMessageHeaderAccessor.wrap(message);
        //    Map<String, Object> attrs = h.getSessionAttributes(); // ← BURADA
        // bunu yaptıktan sonra baksa bırsey yapmaya gerk kalmadan eksıksız tum handshakede kaydettıgımız verıyı alıyoruz.
    }
}


//Senin POST (WebSocket),Hangi Katmandan Geçiyor?
//Bağlantı (Handshake),HTTP → HandshakeInterceptor
//Gönderdiğin veri (mesaj),WebSocket (STOMP) → RequestInterceptor

//Aşama,Ne Oluyor?,Hangi Katman?
//1. ws://.../ws ile bağlan,HTTP isteği (upgrade),HTTP
//2. HandshakeInterceptor çalışır,"IP, User-Agent, URL alınır",HTTP
//3. Bağlantı kurulur,Artık WebSocket,WebSocket
//"4. SEND /app/chat → { ""msg"": ""selam"" }",WebSocket mesajı,STOMP / WebSocket
//5. RequestInterceptor çalışır,Mesaj içeriği (payload) okunur,WebSocket

// PROXY VARSA
//HttpServletRequest r = ((ServletServerHttpRequest) request).getServletRequest();
//String ip = Optional.ofNullable(r.getHeader("X-Forwarded-For"))
//                    .map(h -> h.split(",")[0].trim())
//                    .filter(h -> !h.isEmpty() && !"unknown".equalsIgnoreCase(h))
//                    .orElse(r.getRemoteAddr());
//attributes.put("IP", ip);