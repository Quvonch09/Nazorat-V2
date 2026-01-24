package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByPhoneAndRole(String phone, Role role);
    Optional<User> findByPhone(String phone);

    Optional<User> findByIdAndActiveTrue(Long id);
    boolean existsByPhoneAndRole(String phone, Role role);
    boolean existsByPhone(String phone);

    @Query(
            value = """
        SELECT *
        FROM users u
        WHERE u.active = true
          AND u.role IN ('ROLE_PARENT', 'ROLE_TEACHER','ROLE_ADMIN')
          AND (:name IS NULL 
               OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:phone IS NULL 
               OR u.phone LIKE CONCAT('%', :phone, '%'))
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM users u
        WHERE u.active = true
          AND u.role IN ('ROLE_PARENT', 'ROLE_TEACHER','ROLE_ADMIN')
          AND (:name IS NULL 
               OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:phone IS NULL 
               OR u.phone LIKE CONCAT('%', :phone, '%'))
        """,
            nativeQuery = true
    )
    Page<User> searchUser(
            @Param("name") String name,
            @Param("phone") String phone,
            Pageable pageable
    );
//    Page<User> findAllAndEnabledTrue(Pageable pageable);
}
