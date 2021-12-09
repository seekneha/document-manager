package com.ns.docmanager.repository;

import com.ns.docmanager.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    public String findFileNameById(Integer id);
}
