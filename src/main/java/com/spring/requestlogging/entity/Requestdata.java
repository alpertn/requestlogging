package com.spring.requestlogging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // constructor kullanmana gerek kalmaz sadece private string user yazman yeterlidir.
@Builder // builder User user = User.builder().name("Ali").age(25) .build();
@Entity // jakarta ile veritabanindan table olusturmak icin bu gereklii
@Table(name = "Requestdata")
public class Requestdata{


    @Id // Bu olmadan jakarta calismaz AUTO_INCREMENT yazmana gerk kalmaz
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  AUTO_INCREMENT PRIMARY KEY  demek bu.  -- id BIGINT AUTO_INCREMENT PRIMARY KEY
    private Long id;

    @Column(name = "ip", nullable = false, length = 100)
    private String ip;


    @Column(name = "lastseen", nullable = false)
    private LocalDateTime lastseen;

    @Builder.Default
    @Column(name = "endpointusagecount", nullable = true)
    private Long endpointusagecount = 0L;

    @Column(name = "lastrequesturl", nullable = true, length = 150)
    private String requesturl;

    @Builder.Default
    @Column(name = "lastrequestbody", nullable = true)
    private String requestbody = null;

    @Builder.Default
    @Column(name = "blacklist", nullable = true)
    private Boolean blacklist = false;

    @Column(name = "reason", nullable = true, length = 1000)
    private String reason;

    @Column(name = "useragent", nullable = true, length = 1000)
    private String useragent;

    @Builder.Default
    @Column(name = "headers", nullable = true, length = 1000)
    private String headers = null;

}