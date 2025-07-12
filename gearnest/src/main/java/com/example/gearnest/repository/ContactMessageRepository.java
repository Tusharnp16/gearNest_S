package com.example.gearnest.repository;

import com.example.gearnest.model.ContactMessage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findByResponded(boolean b);
}