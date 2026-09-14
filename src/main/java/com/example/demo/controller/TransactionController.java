package com.example.demo.controller;

import com.example.demo.dto.request.transaction.TransactionSearchRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction-log")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    private  final TransactionLogService transactionLogService;

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_MAKER','ROLE_CHECKER','ROLE_ADMIN','ROLE_VIEWER')")
    public ResponseEntity<?> searchTransaction(@RequestBody TransactionSearchRequest request) {
        Page<Transaction> page = transactionLogService.findTransaction(request);
        return ResponseEntity.ok(page);
    }

}
