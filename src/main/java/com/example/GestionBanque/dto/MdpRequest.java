package com.example.GestionBanque.dto;

import lombok.Data;

@Data
public class MdpRequest {
    private String ancienMdp;
    private String nouveauMdp;
}
