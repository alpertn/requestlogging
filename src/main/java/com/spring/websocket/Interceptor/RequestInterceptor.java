package com.spring.websocket.Interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import com.spring.websocket.repository.RequestDataRepository;
import com.spring.websocket.service.RequestDataService;




// sessionAttrs duzenlenecek
// sessionAttrs duzenlenecek
// sessionAttrs duzenlenecek
// sessionAttrs duzenlenecek



@Component
@RequiredArgsConstructor // constructor yaZmamiza gerek yok all yazinca websockette hata veriyor cunku websocketin constructoru yazilmaz
public class RequestInterceptor implements ChannelInterceptor { // FullTrackingInterceptor


    private final RequestDataService requestDataService;
    private final RequestDataRepository requestDataRepository;

    // requesturl ve request istegi gonderirken gonderilen requestbody eklenecek
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {

        StompHeaderAccessor wrapped = StompHeaderAccessor.wrap(message);

        if (wrapped.getCommand() == null){ // sistem mesajiysa gecmesi icin
            return message;
        }

        SimpMessageHeaderAccessor simphandshakedengelen = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> sessionAttrs = simphandshakedengelen.getSessionAttributes();

        if (sessionAttrs == null){
            return message;
        }

        String ip = (String) sessionAttrs.get("IP");
        if (ip == null || ip.isEmpty()){

            return message;

        }




        String requestbody = message.getPayload().toString();
        String requestheaders = message.getHeaders().toString();

        String requesturl = (String) sessionAttrs.get("URL");


        String requestuseragent = (String) sessionAttrs.get("User-Agent");

        if (requestuseragent == null || requestuseragent.isEmpty()){

            requestuseragent = "Bilinmiyor";

        }

        requestDataService.createdata(ip,requestheaders,requesturl,requestbody,requestuseragent);

        return message;




    }

}




// STOMP HEADER KATMANI

//{
//        "command": "SEND",
//        "destination": "/app/chat",
//        "subscriptionId": "sub-0",
//        "nativeHeaders": {
//        "content-type": ["application/json"],
//        "token": ["abc123"]
//        },
//        "contentLength": 48
//        }
//

//SimpMessageHeaderAccessor
//{
//  "sessionId": "xyz-123",
//  "user": {
//    "name": "ahmet",
//    "roles": ["USER"]
//  },
//  "sessionAttributes": {
//    "userId": 17,
//    "username": "ahmet",
//    "roomId": 42,
//    "ip": "192.168.1.10"
//  },
//  "simpDestination": "/app/chat",
//  "simpMessageType": "MESSAGE"
//}

//message.getPayload()
// {
//  "message": "Selam arkadaşlar!",
//  "roomId": 42
//}

//@Override
//public Message<?> preSend(Message<?> message, MessageChannel channel) {
//    var h = StompHeaderAccessor.wrap(message);
//    if (h.getCommand() == null) return message;
//
//    String ip = (String) h.getSessionAttributes().get("REMOTE_ADDR");
//
//    if (BANNED.contains(ip)) {
//        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "IP yasaklandı: " + ip);
//    }
//
//    repo.findByIpAddress(ip).ifPresentOrElse(
//            s -> { s.setLastSeen(LocalDateTime.now()); s.setEndpointRealtime(s.getEndpointRealtime() + 1); repo.save(s); },
//            () -> repo.save(IpStats.builder()
//                    .ipAddress(ip)
//                    .firstSeen(LocalDateTime.now())
//                    .lastSeen(LocalDateTime.now())
//                    .endpointRealtime(1)
//                    .build())
//    );
//
//    return message;
//}