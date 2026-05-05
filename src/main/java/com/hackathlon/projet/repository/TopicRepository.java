package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findBySubjectId(Long subjectId);

    boolean existsByNameAndSubjectId(String name, Long subjectId);
}
