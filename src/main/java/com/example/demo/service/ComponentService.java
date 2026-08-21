package com.example.demo.service;

import com.example.demo.entity.Component;
import com.example.demo.dto.request.groupCategory.GroupCategoryRequest;
import com.example.demo.dto.request.groupCategory.GroupCategorySearchRequest;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public interface ComponentService {

    List<Component> findAll();

    Component add(GroupCategoryRequest request);

    Component update(GroupCategoryRequest request);

    Boolean delete(Long id);

    Map<String, Object> updateStatus (Long id, Integer newStatus);

    Page<Component> findAllBySpecification(GroupCategorySearchRequest request);

    Page<Component> findAllByNativeQuery(GroupCategorySearchRequest request);

    Page<Component> findAllByProcedure(GroupCategorySearchRequest request);

    List<Component> findAllToExport(GroupCategorySearchRequest request);

    ByteArrayInputStream exportAll(GroupCategorySearchRequest request);

}
