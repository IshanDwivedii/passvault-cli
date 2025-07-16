package com.ishan.passvault.repository;

import com.ishan.passvault.model.PasswordEntry;
import com.ishan.passvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByUserOrderByServiceNameAsc(String username);
    Optional<PasswordEntry> findByUserAndServiceName(User user, String serviceName);

    void deleteByUserAndServiceName(User user, String serviceName);

}
