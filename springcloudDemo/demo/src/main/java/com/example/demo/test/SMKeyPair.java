package com.example.demo.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMKeyPair {
    //私钥
    private String priKey;
    //公钥
    private String pubKey;
}
