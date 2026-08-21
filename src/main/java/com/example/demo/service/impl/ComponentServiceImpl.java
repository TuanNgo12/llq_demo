package com.example.demo.service.impl;

import com.example.demo.entity.Component;
import com.example.demo.repository.Component.ComponentRepository;
import com.example.demo.dto.request.groupCategory.GroupCategoryRequest;
import com.example.demo.dto.request.groupCategory.GroupCategorySearchRequest;
import com.example.demo.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@Service
public class ComponentServiceImpl implements ComponentService {

    @Autowired
    private ComponentRepository componentRepository;

    @Override
    public List<Component> findAll() {
        return componentRepository.findAll();
    }

    @Override
    public Component add(GroupCategoryRequest request) {
        return null;
    }

    @Override
    public Component update(GroupCategoryRequest request) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public Map<String, Object> updateStatus(Long id, Integer newStatus) {
        return Map.of();
    }

    @Override
    public Page<Component> findAllBySpecification(GroupCategorySearchRequest request) {
        return null;
    }

    @Override
    public Page<Component> findAllByNativeQuery(GroupCategorySearchRequest request) {
        return null;
    }

    @Override
    public Page<Component> findAllByProcedure(GroupCategorySearchRequest request) {
        return null;
    }

    @Override
    public List<Component> findAllToExport(GroupCategorySearchRequest request) {
        return List.of();
    }

    @Override
    public ByteArrayInputStream exportAll(GroupCategorySearchRequest request) {
        return null;
    }
}
