package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findBySubjectId(Long subjectId);

    boolean existsByNameAndSubjectId(String name, Long subjectId);

    Optional<Topic> findByNameAndSubjectId(String name, Long subjectId);
}
