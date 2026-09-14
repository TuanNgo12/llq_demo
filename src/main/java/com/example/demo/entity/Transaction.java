package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "TRANSACTION_LOG")
public class Transaction {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRANSACTION_CODE")
    private String transactionCode;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_AT")
    private Date createdAt;
}
