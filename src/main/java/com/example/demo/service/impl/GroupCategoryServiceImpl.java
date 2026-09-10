package com.example.demo.service.impl;

import com.example.demo.Enum.SortFieldEnum;
import com.example.demo.Ultils.ExcelBase;
import com.example.demo.entity.GroupCategory;
import com.example.demo.repository.GroupCategory.GroupCategoryRepository;
import com.example.demo.dto.request.groupCategory.GroupCategoryRequest;
import com.example.demo.dto.request.groupCategory.GroupCategorySearchRequest;
import com.example.demo.service.GroupCategoryService;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupCategoryServiceImpl implements GroupCategoryService{

    private final GroupCategoryRepository groupCategoryRepository;

    @PersistenceContext
    EntityManager em;

    private final ObjectMapper objectMapper;

    @Override
    public Page<GroupCategory> findAll() {
        return groupCategoryRepository.getAll(PageRequest.of(0, 10));
    }

    @Override
    public GroupCategory findById(Long id) {
        return groupCategoryRepository.findById(id).orElseThrow();
    }

    @Override
    public GroupCategory add(GroupCategoryRequest request) {
        boolean existsDuplicate = groupCategoryRepository.existsDuplicate(
                null,
                request.getParamValue(),
                request.getParamType(),
                request.getEffectiveDate(),
                request.getEndEffectiveDate()
        );
        if (existsDuplicate){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Dữ liệu đã tồn tại!"
            );
        }
        GroupCategory groupCategory = new GroupCategory();
        mapRequestToEntity(request, groupCategory);
        groupCategory.setStatus(1);
        groupCategory.setIsDisplay(1);
        groupCategory.setCreatedDate(new Date());
        groupCategory.setUpdateDate(new Date());
        groupCategoryRepository.save(groupCategory);
        return groupCategory;
    }

    @Override
    public GroupCategory update(GroupCategoryRequest request) {
        GroupCategory groupCategory = groupCategoryRepository.getReferenceById(request.getId());
        if (request.getStatus() == 1){
            mapRequestToEntity(request, groupCategory);
            groupCategory.setUpdateDate(new Date());
            groupCategoryRepository.save(groupCategory);
            return groupCategory;
        }
        try {
            groupCategory.setNewData(objectMapper.writeValueAsString(request));
            groupCategory.setStatus(request.getStatus());
            groupCategory.setUpdateDate(new Date());
            groupCategoryRepository.save(groupCategory);
            return groupCategory;
        }catch (Exception e){
            throw new IllegalArgumentException("Lỗi khi cập nhật GroupCategory: " + e.getMessage());
        }
    }

    @Override
    public GroupCategory delete(GroupCategoryRequest request) {
        GroupCategory groupCategory = groupCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy GroupCategory"));

//        groupCategory.setIsActive(0);// Cập nhật trạng thái thành "Không hoạt động"
//        groupCategory.setUpdateDate(new Date());
        groupCategoryRepository.delete(groupCategory);
        return groupCategory;
    }

    @Override
    @Transactional
    public List<GroupCategory> updateList(List<GroupCategoryRequest> listRequest) {
        List<Long> ids = listRequest.stream().map(GroupCategoryRequest::getId).toList();
        Map<Long, GroupCategory> entityById = groupCategoryRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(GroupCategory::getId, entity -> entity));
        List<GroupCategory> updatedList = new ArrayList<>();
        for (GroupCategoryRequest request : listRequest) {
            GroupCategory entity = entityById.get(request.getId());
            if (entity == null) {
                  throw new RuntimeException("Không tìm thấy bản ghi với id: " + request.getId());
            }
            if (entity.getNewData() != null && request.getStatus() == 4) {
                GroupCategoryRequest newData = objectMapper.readValue(entity.getNewData(), GroupCategoryRequest.class);
                mapRequestToEntity(newData, entity);
                entity.setStatus(newData.getStatus());
                entity.setIsDisplay(2);
                entity.setNewData(null);
            } else if (entity.getNewData() == null && request.getStatus() == 4) {
                mapRequestToEntity(request, entity);
                entity.setStatus(request.getStatus());
                entity.setIsDisplay(request.getIsDisplay());
            }
            entity.setStatus(request.getStatus() );
            entity.setUpdateDate(new Date());
            updatedList.add(entity);
        }
        return groupCategoryRepository.saveAll(updatedList);
    }

    @Override
    @Transactional
    public Map<String, Object> updateStatusBatch(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ID không được rỗng");
        }
        List<GroupCategory> categories = groupCategoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi nào");
        }
        Integer firstStatus = categories.get(0).getStatus();
        boolean allSameStatus = categories.stream()
                .allMatch(c -> c.getStatus().equals(firstStatus));

        if (!allSameStatus) {
            throw new IllegalStateException("Các bản ghi phải có cùng trạng thái");
        }
        for (GroupCategory category: categories){
            if (category.getNewData() != null && newStatus == 4){
              GroupCategoryRequest newData = objectMapper.readValue(category.getNewData(), GroupCategoryRequest.class);
              mapRequestToEntity(newData, category);
              category.setStatus(newStatus);
              category.setIsDisplay(2);
              category.setNewData(null);
            }
            else if (category.getNewData() == null && newStatus == 4) {
                category.setStatus(newStatus);
                category.setIsDisplay(2);
            }
            category.setStatus(newStatus);
            category.setUpdateDate(new Date());
        }
        List<GroupCategory> updated = groupCategoryRepository.saveAll(categories);
        return Map.of(
                "message", "Cập nhật thành công",
                "updatedCount", updated.size(),
                "oldStatus", firstStatus,
                "newStatus", newStatus
        );
    }

    @Override
    @Transactional
    public Map<String, Object> panding(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ID không được rỗng");
        }
        List<GroupCategory> categories = groupCategoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi nào");
        }
        Integer firstStatus = categories.get(0).getStatus();
        boolean allSameStatus = categories.stream()
                .allMatch(c -> c.getStatus().equals(firstStatus));

        if (!allSameStatus) {
            throw new IllegalStateException("Các bản ghi phải có cùng trạng thái");
        }
        if (firstStatus == 1 && newStatus != 3){
            throw new IllegalStateException("Trạng thái 1 mới gửi duyệt được!");
        }
        for (GroupCategory category: categories){
            category.setStatus(newStatus);
            category.setUpdateDate(new Date());
        }
        List<GroupCategory> updated = groupCategoryRepository.saveAll(categories);
        return Map.of(
                "message", "Gửi duyệt thành công",
                "updatedCount", updated.size(),
                "oldStatus", firstStatus,
                "newStatus", newStatus
        );
    }

    @Override
    @Transactional
    public Map<String, Object> approve(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ID không được rỗng");
        }
        List<GroupCategory> categories = groupCategoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi nào");
        }
        Integer firstStatus = categories.get(0).getStatus();
        boolean allSameStatus = categories.stream()
                .allMatch(c -> c.getStatus().equals(firstStatus));

        if (!allSameStatus) {
            throw new IllegalStateException("Các bản ghi phải có cùng trạng thái");
        }
        if (firstStatus == 3 && newStatus != 4){
            throw new IllegalStateException("Trạng thái 3 mới duyệt được!");
        }
        for (GroupCategory category: categories){
            if (category.getNewData() != null){
                GroupCategoryRequest newData = objectMapper.readValue(category.getNewData(), GroupCategoryRequest.class);
                mapRequestToEntity(newData, category);
                category.setStatus(newStatus);
                category.setIsDisplay(2);
                category.setNewData(null);
            }
            else {
                category.setStatus(newStatus);
                category.setIsDisplay(2);
            }

            category.setUpdateDate(new Date());
        }
        List<GroupCategory> updated = groupCategoryRepository.saveAll(categories);
        return Map.of(
                "message", "Duyệt thành công",
                "updatedCount", updated.size(),
                "oldStatus", firstStatus,
                "newStatus", newStatus
        );
    }

    @Override
    @Transactional
    public Map<String, Object> cancel(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ID không được rỗng");
        }
        List<GroupCategory> categories = groupCategoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi nào");
        }
        Integer firstStatus = categories.get(0).getStatus();
        boolean allSameStatus = categories.stream()
                .allMatch(c -> c.getStatus().equals(firstStatus));

        if (!allSameStatus) {
            throw new IllegalStateException("Các bản ghi phải có cùng trạng thái");
        }
        if (firstStatus == 4 && newStatus != 7){
            throw new IllegalStateException("Trạng thái 4 mới hủy duyệt được!");
        }
        for (GroupCategory category: categories){
            category.setStatus(newStatus);
            category.setUpdateDate(new Date());
        }
        List<GroupCategory> updated = groupCategoryRepository.saveAll(categories);
        return Map.of(
                "message", "Hủy duyệt thành công",
                "updatedCount", updated.size(),
                "oldStatus", firstStatus,
                "newStatus", newStatus
        );
    }

    @Override
    @Transactional
    public Map<String, Object> reject(List<Long> ids, Integer newStatus) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ID không được rỗng");
        }
        List<GroupCategory> categories = groupCategoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi nào");
        }
        Integer firstStatus = categories.get(0).getStatus();
        boolean allSameStatus = categories.stream()
                .allMatch(c -> c.getStatus().equals(firstStatus));

        if (!allSameStatus) {
            throw new IllegalStateException("Các bản ghi phải có cùng trạng thái");
        }
        if (firstStatus == 3 && newStatus != 5){
            throw new IllegalStateException("Trạng thái 3 mới từ chối duyệt được!");
        }
        for (GroupCategory category: categories){
            category.setStatus(newStatus);
            category.setUpdateDate(new Date());
        }
        List<GroupCategory> updated = groupCategoryRepository.saveAll(categories);
        return Map.of(
                "message", "Từ chối thành công",
                "updatedCount", updated.size(),
                "oldStatus", firstStatus,
                "newStatus", newStatus
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupCategory> findBySpecification(GroupCategorySearchRequest request) {
        int pageNo = Math.max(request.getPageNo(), 0);
        int pageSize = Math.max(request.getPageSize(), 5);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Specification<GroupCategory> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getParamName())) {
                predicates.add(criteriaBuilder.like(root.get("paramName"), "%" + request.getParamName() + "%"));
            }
            if (StringUtils.hasText(request.getParamValue())) {
                predicates.add(criteriaBuilder.like(root.get("paramValue"), "%" + request.getParamValue() + "%"));
            }
            if (StringUtils.hasText(request.getParamType())) {
                predicates.add(criteriaBuilder.like(root.get("paramType"), "%" + request.getParamType() + "%"));
            }
            if (request.getStatus() != null && !request.getStatus().equals(0)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (request.getIsActive() != null && !request.getIsActive().equals(0)) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
            }
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return groupCategoryRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupCategory> findByNativeQuery(GroupCategorySearchRequest request) {
        int pageNo = Math.max(request.getPageNo(), 0);
        int pageSize = Math.max(request.getPageSize(), 5);
        StringBuilder sql =new StringBuilder("SELECT * FROM PMH_GROUP_CATEGORY a WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM PMH_GROUP_CATEGORY a WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        builWhere(request, sql, params);
        builWhere(request, countSql, params);

        sql.append(" ORDER BY a.").append("UPDATED_DATE").append(" ").append("DESC");

        Query query = em.createNativeQuery(sql.toString(), GroupCategory.class);
        params.forEach(query::setParameter);

        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);
        List<GroupCategory> data = query.getResultList();

        Query countQuery = em.createNativeQuery(countSql.toString());
        params.forEach(countQuery::setParameter);

        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PageImpl<>(data, PageRequest.of(pageNo, pageSize), total);
    }

    @Transactional(readOnly = true)
    private void builWhere (GroupCategorySearchRequest request, StringBuilder sql, Map<String, Object> params) {
        addString(sql, params, "param_name", request.getParamName());
        addString(sql, params, "param_value", request.getParamValue());
        addString(sql, params, "param_type", request.getParamType());
        addNumber(sql, params, "status", request.getStatus());
        addNumber(sql, params, "is_active", request.getIsActive());
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
    private void addNumber(StringBuilder sql, Map<String, Object> params, String column, Number value) {
        if (value != null) {
            sql.append(" AND a.").append(column).append(" = :").append(column);
            params.put(column, value);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupCategory> findByProcedure(GroupCategorySearchRequest request) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("PMH_SEARCH_GROUP_CATEGORY_T1", GroupCategory.class);
        //IN Parameter
        query.registerStoredProcedureParameter("P_PARAM_NAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_PARAM_VALUE", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_PARAM_TYPE", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_STATUS", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_IS_ACTIVE", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_PAGE_SIZE", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_PAGE_NO", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_ORDER_BY", String.class, ParameterMode.IN);

        //OUT Parameter
        query.registerStoredProcedureParameter("P_RESULT", void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("P_TOTAL_ELEMENTS", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("P_TOTAL_PAGES", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("P_PAGE_SIZE_OUT", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("P_PAGE_NO_OUT", Integer.class, ParameterMode.OUT);

        // Set IN Parameter
        query.setParameter("P_PARAM_NAME", request.getParamName());
        query.setParameter("P_PARAM_VALUE", request.getParamValue());
        query.setParameter("P_PARAM_TYPE", request.getParamType());
        query.setParameter("P_STATUS", request.getStatus());
        query.setParameter("P_IS_ACTIVE", request.getIsActive());
        query.setParameter("P_PAGE_SIZE", request.getPageSize());
        query.setParameter("P_PAGE_NO", request.getPageNo());

        String dbField;
        try {
            dbField = SortFieldEnum.fromField(request.getSortField()); // trả về PARAM_NAME
        } catch (IllegalArgumentException e) {
            dbField = "UPDATED_DATE";
        }
        String orderBy = dbField + " " + (request.getSortDir() != null ? request.getSortDir() : "DESC");
        query.setParameter("P_ORDER_BY", orderBy);

        query.execute();


        List<GroupCategory> results = query.getResultList();

        Integer totalElements = (Integer) query.getOutputParameterValue("P_TOTAL_ELEMENTS");

        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        return new PageImpl<>(results, pageable, totalElements != null ? totalElements : 0);
    }

    @Override
    public List<GroupCategory> findAllToExport(GroupCategorySearchRequest request) {
        StringBuilder sql =new StringBuilder("SELECT * FROM PMH_GROUP_CATEGORY a WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        builWhere(request, sql, params);

        Query query = em.createNativeQuery(sql.toString(), GroupCategory.class);
        params.forEach(query::setParameter);
        List<GroupCategory> results = query.getResultList();
        return results;
    }

    @Override
    public ByteArrayInputStream exportAll(GroupCategorySearchRequest groupCategorySearchRequest) {
        List<GroupCategory> categoryList = this.findAllToExport(groupCategorySearchRequest);
        LinkedHashMap<String, String> columnMap = new LinkedHashMap<>();
        columnMap.put("Danh mục theo nhóm", "paramType");
        columnMap.put("Giá trị thành phần", "paramValue");
        columnMap.put("Tên thành phần", "paramName");
        columnMap.put("Mô tả", "description");
        columnMap.put("Cấu phần xử lý", "componentCode");
        columnMap.put("Ngày hiệu lực", "effectiveDate");
        columnMap.put("Ngày hết hiệu lực", "endEffectiveDate");
        columnMap.put("Trạng thái hoạt động", "isActive");
        columnMap.put("Trạng thái tham số", "status");

        Map<String, Map<Object, String>> valueMappings = new HashMap<>();

        valueMappings.put("status", Map.of(1, "Tạo mới", 3, "Chờ phê duyệt", 4, "Đã phê duyệt",
                5, "Từ chối", 7, "Huỷ phê duyệt"));
        valueMappings.put("isActive", Map.of(0, "Không hoạt động", 1, "Hoạt động"));

        // 4. Gọi Utils
        return ExcelBase.exportToExcel(categoryList, columnMap, valueMappings, "Group Categories");
    }

    @Override
    @Scheduled(
            cron = "${scheduler.group-category-activation.cron:0 * * * * *}",
            zone = "Asia/Ho_Chi_Minh"
    )
    @Transactional
    public int activateParams() {
        Date now = new Date();
        int updated = groupCategoryRepository.activateParams(now);
        if (updated > 0) {
            System.out.println("Đã tự động kích hoạt "+ updated+ " tham số (EFFECTIVE_DATE <= now)");
        }
        return updated;
    }

    private void mapRequestToEntity(GroupCategoryRequest request, GroupCategory groupCategory) {
        groupCategory.setParamName(request.getParamName());
        groupCategory.setParamValue(request.getParamValue());
        groupCategory.setParamType(request.getParamType());
        groupCategory.setDescription(request.getDescription());
        groupCategory.setComponentCode(request.getComponentCode());
        groupCategory.setIsActive(request.getIsActive());
        groupCategory.setEffectiveDate(request.getEffectiveDate());
        groupCategory.setEndEffectiveDate(request.getEndEffectiveDate());
    }
}
