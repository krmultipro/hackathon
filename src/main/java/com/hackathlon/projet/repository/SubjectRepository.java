package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsByName(String name);
}
