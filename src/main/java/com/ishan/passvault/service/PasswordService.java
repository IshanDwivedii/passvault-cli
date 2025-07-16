package com.ishan.passvault.service;

import com.ishan.passvault.repository.PasswordEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PasswordService {

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;



}
