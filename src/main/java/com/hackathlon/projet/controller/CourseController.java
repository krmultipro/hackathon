package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Course;
import com.hackathlon.projet.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Cours", description = "API de gestion des cours")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Lister tous les cours", description = "Retourne la liste complète des cours")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @Operation(summary = "Obtenir un cours par ID", description = "Retourne un cours à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(
            @Parameter(description = "ID du cours", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @Operation(summary = "Créer un cours", description = "Crée un nouveau cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cours créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        Course saved = courseService.create(course);
        return ResponseEntity
                .created(URI.create("/api/courses/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour un cours", description = "Met à jour un cours existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours mis à jour"),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Course> update(
            @Parameter(description = "ID du cours", example = "1") @PathVariable Long id,
            @Valid @RequestBody Course details) {
        return ResponseEntity.ok(courseService.update(id, details));
    }

    @Operation(summary = "Supprimer un cours", description = "Supprime un cours par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cours supprimé"),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du cours", example = "1") @PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
