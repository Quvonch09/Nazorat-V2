package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByPhoneAndRole(String phone, Role role);
    Optional<User> findByPhone(String phone);

    boolean existsByPhoneAndRole(String phone, Role role);
    boolean existsByPhone(String phone);

//    Page<User> findAllAndEnabledTrue(Pageable pageable);
}
