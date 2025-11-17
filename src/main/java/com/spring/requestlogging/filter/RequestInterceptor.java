package com.spring.requestlogging.filter;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import com.spring.requestlogging.repository.RequestDataRepository;
import com.spring.requestlogging.service.RequestDataService;


@Component
@AllArgsConstructor
public class RequestInterceptor implements ChannelInterceptor { // FullTrackingInterceptor


    private final RequestDataService requestDataService;
    private final RequestDataRepository requestDataRepository;

    // requesturl ve request istegi gonderirken gonderilen requestbody eklenecek
    @Override
    public Message <?> preSend(Message<?> message, MessageChannel messageChannel) {

        StompHeaderAccessor wrapped = StompHeaderAccessor.wrap(message); // gelen bilgileri islkemek icin wrap etmemiz lazim

        if (wrapped.getCommand() == null){ // stomp komudu degilse sistem mesajiysa gec diye. sadece request connect send gibi isteklerin calismasi icin.
            return message;
        }

        SimpMessageHeaderAccessor simp = SimpMessageHeaderAccessor.wrap(message); // islemek icin wrap etmemiz lazim
        Map<String, Object> sessionAttrs = simp.getSessionAttributes(); // kolayca veriyi get etmek icin map yapiyoruz



        String ip = (String) sessionAttrs.get("REMOTE_ADDR");
        if (ip == null | ip.isEmpty()){

            return message;

        }


        if (requestDataRepository.existsByIpAndBlacklistTrue(ip) == true){


            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ip Adresin ' " + requestDataRepository.findReasonByIp(ip) + " ' Sebebiyle Blackliste alınmıştır.");


        }
        String requestbody = message.getPayload().toString();
        String requestheaders = message.getHeaders().toString();
        String requesturl = message.getHeaders().get("requestUrl", String.class);
        String requestuseragent = message.getHeaders().get("user-agent", String.class);

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