package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTitleAndTopicId(String title, Long topicId);

    Optional<Course> findByTitleAndTopicId(String title, Long topicId);
}
