package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
