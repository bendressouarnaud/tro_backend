package com.ankk.tro.httpbean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WavePaymentRequest {
    private Integer amount;
    private String currency;
    @JsonProperty("error_url")
    private String errorUrl;
    @JsonProperty("success_url")
    private String successUrl;
    //
    private long idpub;
    private long iduser;
    private int reserve;
}
