package com.example.demo.dto.request.transaction;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionSearchRequest {

    private int pageNo;

    private int pageSize;

    private String transactionCode;

    private String accountNo;

    private Integer amountMin;

    private Integer amountMax;

    private String status;

    private Date createdAtMin;

    private Date createdAtMax;

}
