package com.project.veriphi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM user WHERE email=:email AND password=:password LIMIT 1", nativeQuery = true)
    User findByEmailAndPassword(@Param("email") String email,
                                @Param("password") String password);
}
