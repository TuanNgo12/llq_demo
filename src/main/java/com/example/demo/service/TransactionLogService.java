package com.example.demo.service;

import com.example.demo.dto.request.transaction.TransactionSearchRequest;
import com.example.demo.entity.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionLogService {

    Page<Transaction> findTransaction(TransactionSearchRequest request);

}
