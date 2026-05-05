package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Course;
import com.hackathlon.projet.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cours introuvable"));
    }

    public Course create(Course course) {
        // validate
        validateCourse(course);
        return courseRepository.save(course);
    }

    public Course update(Long id, Course details) {
        Course existing = findById(id);

        // validate
        validateCourse(details);

        existing.setTopic(details.getTopic());
        existing.setTitle(details.getTitle());
        existing.setContent(details.getContent());
        existing.setCreatedAt(details.getCreatedAt());

        return courseRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        courseRepository.deleteById(id);
    }

    private void validateCourse(Course course) {
        // validate
        if (course == null || isBlank(course.getTitle())) {
            throw new BadRequestException("Le titre du cours est invalide");
        }

        // check topic
        if (course.getTopic() == null) {
            throw new BadRequestException("Le topic est obligatoire");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
