package com.project.veriphi.organiser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganiserAuthRepository extends JpaRepository<OrganiserAuth, String> {

    OrganiserAuth findByEmailAndPassword(String email, String password);
}
