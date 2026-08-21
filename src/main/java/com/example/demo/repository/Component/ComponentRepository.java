package com.example.demo.repository.Component;

import com.example.demo.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentRepository extends JpaRepository<Component, String> {
    Component findComponentByComponentCode(String componentCode);
}
