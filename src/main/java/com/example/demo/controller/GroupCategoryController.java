package com.example.demo.controller;

import com.example.demo.dto.request.groupCategory.StatusRequest;
import com.example.demo.entity.GroupCategory;
import com.example.demo.dto.request.groupCategory.GroupCategoryRequest;
import com.example.demo.dto.request.groupCategory.GroupCategorySearchRequest;
import com.example.demo.service.GroupCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-category")
@CrossOrigin(origins = "http://localhost:4200")
public class GroupCategoryController {

    private final GroupCategoryService groupCategoryService;

    @GetMapping("")
    public ResponseEntity<?> getAllGroupCategories() {
        return ResponseEntity.ok(groupCategoryService.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGroupCategory(@Valid @RequestBody GroupCategoryRequest request) {
        GroupCategory groupCategory = groupCategoryService.add(request);
        return ResponseEntity.ok(groupCategory);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateGroupCategory(@RequestBody GroupCategoryRequest request) {
        GroupCategory groupCategory = groupCategoryService.update(request);
        return ResponseEntity.ok(groupCategory);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteGroupCategory(@RequestBody GroupCategoryRequest request) {
        GroupCategory groupCategory = groupCategoryService.delete(request);
        return ResponseEntity.ok(groupCategory);
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatusGroupCategory(@RequestBody List<GroupCategoryRequest> request) {
        try {
            List<GroupCategory> updatedList = groupCategoryService.updateList(request);
            return ResponseEntity.ok(updatedList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/update-status-list")
    public ResponseEntity<?> updateStatusListGroupCategory(@RequestBody StatusRequest request) {
        try {
            Map<String, Object> result =groupCategoryService.updateStatusBatch(
                    request.getIds(),
                    request.getStatus()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/panding")
    public ResponseEntity<?> panding(@RequestBody StatusRequest request) {
        try {
            Map<String, Object> result =groupCategoryService.panding(
                    request.getIds(),
                    request.getStatus()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approve(@RequestBody StatusRequest request) {
        try {
            Map<String, Object> result =groupCategoryService.approve(
                    request.getIds(),
                    request.getStatus()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> reject(@RequestBody StatusRequest request) {
        try {
            Map<String, Object> result =groupCategoryService.reject(
                    request.getIds(),
                    request.getStatus()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestBody StatusRequest request) {
        try {
            Map<String, Object> result =groupCategoryService.cancel(
                    request.getIds(),
                    request.getStatus()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchGroupCategories(@RequestBody GroupCategorySearchRequest request) {
        Page<GroupCategory> page = groupCategoryService.findBySpecification(request);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search-native-query")
    public ResponseEntity<?> searchGroupCategoryNativeQuery(@RequestBody GroupCategorySearchRequest request) {
        Page<GroupCategory> page = groupCategoryService.findByNativeQuery(request);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search-procedure")
    public ResponseEntity<?> searchGroupCategoryProcedure(@RequestBody GroupCategorySearchRequest request) {
        Page<GroupCategory> page = groupCategoryService.findByProcedure(request);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/export-excel")
    public ResponseEntity<?> exportExcel(@RequestBody GroupCategorySearchRequest request) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "GroupCategories_" + timestamp + ".xlsx";

            ByteArrayInputStream in = groupCategoryService.exportAll(request);
            byte[] bytes = in.readAllBytes(); // Đọc hết thành byte array

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            Cho phep FE doc header
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
