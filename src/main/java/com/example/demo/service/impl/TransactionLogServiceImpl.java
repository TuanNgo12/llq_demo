package com.example.demo.service.impl;

import com.example.demo.dto.request.transaction.TransactionSearchRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.transactionLog.TransactionLogRepository;
import com.example.demo.service.TransactionLogService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionLogServiceImpl implements TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<Transaction> findTransaction(TransactionSearchRequest request) {
        int pageNo = Math.max(request.getPageNo(), 0);
        int pageSize = Math.max(request.getPageSize(), 10);
        StringBuilder sql =new StringBuilder("SELECT * FROM TRANSACTION_LOG a WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM TRANSACTION_LOG a WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        builWhere(request, sql, params);
        builWhere(request, countSql, params);

        sql.append(" ORDER BY a.").append("CREATED_AT").append(" ").append("DESC");

        Query query = em.createNativeQuery(sql.toString(), Transaction.class);
        params.forEach(query::setParameter);

        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);
        List<Transaction> data = query.getResultList();

        Query countQuery = em.createNativeQuery(countSql.toString());
        params.forEach(countQuery::setParameter);

        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PageImpl<>(data, PageRequest.of(pageNo, pageSize), total);
    }

    @Transactional(readOnly = true)
    private void builWhere (TransactionSearchRequest request, StringBuilder sql, Map<String, Object> params) {
        addString(sql, params, "transaction_code", request.getTransactionCode());
        addString(sql, params, "account_no", request.getAccountNo());
        addString(sql, params, "status", request.getStatus());
        addNumber(sql, params, "amount", request.getAmountMin(), request.getAmountMax());
        addDate(sql, params, "created_at", request.getCreatedAtMin(), request.getCreatedAtMax());
    }

    @Transactional(readOnly = true)
    private void addString (StringBuilder sql, Map<String, Object> params, String columns, String value){
        if (value != null && !value.isBlank()) {
            sql.append(" AND LOWER(a. ")
                    .append(columns)
                    .append(") LIKE '%' || :")
                    .append(columns)
                    .append(" || '%' ");
            params.put(columns, value.toLowerCase());
        }
    }

    @Transactional(readOnly = true)
    private void addNumber(StringBuilder sql, Map<String, Object> params, String column, Number amountMin, Number amountMax) {
        if (amountMin != null && amountMax != null) {
            sql.append(" AND a.").append(column)
                    .append(" BETWEEN :").append(column).append("Min AND :").append(column).append("Max");

            params.put(column + "Min", amountMin);
            params.put(column + "Max", amountMax);
        }
    }

    @Transactional(readOnly = true)
    private void addDate(StringBuilder sql, Map<String, Object> params, String column, Date createdAtMin, Date createdAtMax) {
        if (createdAtMin != null && createdAtMax != null) {
            sql.append(" AND a.").append(column)
                    .append(" BETWEEN :").append(column).append("Min AND :")
                    .append(column).append("Max");

            params.put(column + "Min", createdAtMin);
            params.put(column + "Max", createdAtMax);
        }
    }
}
