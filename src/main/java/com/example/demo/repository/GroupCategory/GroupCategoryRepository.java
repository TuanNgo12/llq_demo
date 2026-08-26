package com.example.demo.repository.GroupCategory;

import com.example.demo.entity.GroupCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long>, JpaSpecificationExecutor<GroupCategory> {

    @Query("SELECT g FROM GroupCategory g ORDER BY g.updateDate DESC")
    Page<GroupCategory> getAll(Pageable pageable);

    @Query("""
    SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
    FROM GroupCategory g
    WHERE g.paramValue = :paramValue
       AND g.paramType = :paramType
""")
    boolean existsDuplicate(
            @Param("paramValue") String paramValue,
            @Param("paramType") String paramType
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE GroupCategory p
           SET p.isActive = 1
           WHERE p.isActive = 0            
             AND p.effectiveDate <= :now
           """)
    int activateParams(@Param("now") Date now);

    @Query("""
    SELECT COUNT(g) > 0
    FROM GroupCategory g
    WHERE (:id IS NULL OR g.id <> :id)
      AND g.effectiveDate <= :effectiveDate
      AND (
          g.endEffectiveDate IS NULL
          OR g.endEffectiveDate >= :effectiveDate
      )
    """)
    boolean existsEffectiveDateInRange(@Param("id") Long id, @Param("effectiveDate") Date effectiveDate);

    @Query("""
    SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
    FROM GroupCategory g
    WHERE g.paramValue = :paramValue
      AND g.paramType = :paramType
      AND (:id IS NULL OR g.id <> :id)
      AND g.effectiveDate <= :effectiveDate
      AND (
          g.endEffectiveDate IS NULL
          OR g.endEffectiveDate >= :effectiveDate
      )
""")
    boolean existsDuplicate(
            @Param("id") Long id,
            @Param("paramValue") String paramValue,
            @Param("paramType") String paramType,
            @Param("effectiveDate") Date effectiveDate
    );
}
