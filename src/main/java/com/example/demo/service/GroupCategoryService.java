package com.example.demo.service;

import com.example.demo.entity.GroupCategory;
import com.example.demo.dto.request.groupCategory.GroupCategoryRequest;
import com.example.demo.dto.request.groupCategory.GroupCategorySearchRequest;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public interface GroupCategoryService {

    Page<GroupCategory> findAll();

    GroupCategory findById(Long id);

    GroupCategory add(GroupCategoryRequest request);

    GroupCategory update(GroupCategoryRequest request);

    GroupCategory delete(GroupCategoryRequest request);

    List<GroupCategory> updateList(List<GroupCategoryRequest> listRequest);

    Page<GroupCategory> findBySpecification(GroupCategorySearchRequest request);

    Page<GroupCategory> findByNativeQuery(GroupCategorySearchRequest request);

    Page<GroupCategory> findByProcedure(GroupCategorySearchRequest request);

    List<GroupCategory> findAllToExport(GroupCategorySearchRequest request);

    ByteArrayInputStream exportAll(GroupCategorySearchRequest groupCategorySearchRequest);

    int activateParams();

    Map<String, Object> updateStatusBatch(List<Long> ids, Integer newStatus);

    Map<String, Object> panding (List<Long> ids, Integer newStatus);

    Map<String, Object> approve (List<Long> ids, Integer newStatus);

    Map<String, Object> cancel (List<Long> ids, Integer newStatus);

    Map<String, Object> reject (List<Long> ids, Integer newStatus);


}
