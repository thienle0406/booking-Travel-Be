package com.mytour.booking.controller;

import com.mytour.booking.entity.TourTemplate;
import com.mytour.booking.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour-templates")
public class TourTemplateController {

    @Autowired
    private TourService tourService;

    // === 1. POST /api/v1/tour-templates/list (Khớp FE) ===
    // FE gửi { "companyId": "..." }
    @PostMapping("/list")
    public ResponseEntity<List<TourTemplate>> getAllTemplates(@RequestBody TourTemplate request) {
        // Cần logic Security để kiểm tra quyền ADMIN
        List<TourTemplate> templates = tourService.findAllTemplates(request.getCompanyId());
        return ResponseEntity.ok(templates);
    }

    // === 2. POST /api/v1/tour-templates/detail (Khớp FE) ===
    // FE gửi { "id": "tpl_1" }
    @PostMapping("/detail")
    public ResponseEntity<TourTemplate> getTemplateDetail(@RequestBody TourTemplate request) {
        // Cần logic để tìm Template bằng ID
        TourTemplate template = tourService.findTemplateById(request.getId());
        return ResponseEntity.ok(template);
    }

    // === 3. POST /api/v1/tour-templates (CREATE) ===
    @PostMapping
    public ResponseEntity<TourTemplate> createTemplate(@RequestBody TourTemplate template) {
        TourTemplate savedTemplate = tourService.saveTemplate(template);
        return ResponseEntity.ok(savedTemplate);
    }

    // === 4. PUT /api/v1/tour-templates/{id} (UPDATE) ===
    @PutMapping("/{id}")
    public ResponseEntity<TourTemplate> updateTemplate(@PathVariable String id, @RequestBody TourTemplate template) {
        TourTemplate updatedTemplate = tourService.updateTemplate(id, template);
        return ResponseEntity.ok(updatedTemplate);
    }

    // === 5. DELETE /api/v1/tour-templates/{id} (DELETE) ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        tourService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}