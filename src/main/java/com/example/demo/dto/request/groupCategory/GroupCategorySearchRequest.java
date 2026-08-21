package com.example.demo.dto.request.groupCategory;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class GroupCategorySearchRequest {

    private int pageNo;

    private int pageSize;

    private String sortField;

    private String sortDir;

    private Long id;

    private String paramName;

    private String paramValue;

    private String paramType;

    private String description;

    private String componentCode;

    private String newData;

    private Integer status;

    private Integer isActive;

    private Integer isDisplay;

    Date effectiveDateFrom;

    Date effectiveDateTo;

    Date endEffectiveDateFrom;

    Date endEffectiveDateTo;

}
