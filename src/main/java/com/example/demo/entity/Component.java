package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "PMH_COMPONENTS")
public class Component {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COMPONENT_CODE")
    private String componentCode;

    @Column(name = "COMPONENT_NAME")
    private String componentName;

//    @Column(name = "MESSAGE_TYPE")
//    private String messageType;
//
//    @Column(name = "CONNECTION_METHOD")
//    private String connectionMethod;
//
//    @Column(name = "CHECK_TOKEN")
//    private String checkToken;
//
//    @Column(name = "STATUS")
//    private Integer status;
//
//    @Column(name = "IS_ACTIVE")
//    private Integer isActive;
//
//    @Column(name = "IS_DISPLAY")
//    private Integer isDisplay;
//
//    @Column(name = "NEW_DATA")
//    private String newData;
//
//    @Column(name = "EFFECTIVE_DATE")
//    private Date effectiveDate;
//
//    @Column(name = "END_EFFECTIVE_DATE")
//    private Date endEffectiveDate;
//
//    @OneToMany(mappedBy = "componentCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private List<GroupCategory> groupCategories;
//
//    @Column(name = "CREATED_BY")
//    private String createdBy;
//
//    @Column(name = "CREATED_DATE")
//    private Date createdDate;
//
//    @Column(name = "UPDATED_BY")
//    private String updateBy;
//
//    @Column(name = "UPDATED_DATE")
//    private Date updatedDate;

}
