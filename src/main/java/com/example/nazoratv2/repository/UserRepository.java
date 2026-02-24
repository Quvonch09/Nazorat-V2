package com.example.nazoratv2.repository;

import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByPhoneAndRole(String phone, Role role);
    Optional<User> findByPhoneAndActiveTrue(String phone);

    Optional<User> findByIdAndActiveTrue(Long id);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndActiveTrue(String phone);
    Optional<User> findByTelegramUsername(String telegramUsername);

    @Query("""
    select u from User u
    where (:name is null or :name = '' or lower(u.fullName) like lower(concat('%', :name, '%')))
      and (:phone is null or :phone = '' or u.phone like concat('%', :phone, '%'))
      and (:role is null or u.role = :role) and u.role <> 'ROLE_SUPER_ADMIN'
""")
    Page<User> searchUser(@Param("name") String name,
                          @Param("phone") String phone,
                          @Param("role") Role role,
                          Pageable pageable);


    List<User> findAllByRole(Role role);

    boolean existsByTelegramIdAndIdNot(Long telegramId, Long id);
    boolean existsByTelegramUsername(String telegramUsername);

    long countUserByActiveTrueAndRole(Role role);

    Optional<User> findByPhone(String phone);
}
