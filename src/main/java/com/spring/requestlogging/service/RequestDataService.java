package com.spring.requestlogging.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.spring.requestlogging.entity.Requestdata;
import com.spring.requestlogging.repository.RequestDataRepository;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // constructor yazmamiza gerek kalmiyor bunu yazarsak
public class RequestDataService {

    private final RequestDataRepository requestdatarepository; // Constructor yazmaya gerek yok

    public void createdata(String ip, String headers, String requesturl, String requestbody,String useragent){

        Requestdata data = requestdatarepository.findByIp(ip).orElseGet(() ->
                Requestdata.builder()
                        .ip(ip)
                        .headers(headers)
                        .requesturl(requesturl)
                        .requestbody(requestbody)
                        .useragent(useragent)
                        .lastseen(LocalDateTime.now())
                        .build()
        );
        requestdatarepository.save(data);

    }

}
