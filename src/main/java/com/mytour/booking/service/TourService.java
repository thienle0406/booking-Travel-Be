package com.mytour.booking.service;

import com.mytour.booking.entity.TourDeparture;
import com.mytour.booking.entity.TourTemplate;
import com.mytour.booking.entity.Category;
import com.mytour.booking.model.request.TourSearchRequest;
import com.mytour.booking.model.response.TourSearchResponse;
import com.mytour.booking.repository.TourDepartureRepository;
import com.mytour.booking.repository.TourTemplateRepository;
import com.mytour.booking.repository.CategoryRepository;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TourService {

    @Autowired private TourTemplateRepository templateRepository;
    @Autowired private TourDepartureRepository departureRepository;
    @Autowired private CategoryRepository categoryRepository;

    // === Hàm CRUD Tour Template ===
    public List<TourTemplate> findAllTemplates(String companyId) {
        return templateRepository.findByCompanyId(companyId);
    }
    public TourTemplate findTemplateById(String id) {
        return templateRepository.findById(id).orElseThrow(() -> new RuntimeException("Template not found"));
    }
    public TourTemplate saveTemplate(TourTemplate template) {
        // Đảm bảo ID được gán trước khi lưu (vì sử dụng chiến lược Assigned cho khóa chính)
        if (template.getId() == null || template.getId().isEmpty()) {
            template.setId(UUID.randomUUID().toString());
        }

        // TODO: Có thể bổ sung thêm các validation cho companyId, categoryId, v.v.
        return templateRepository.save(template);
    }
    public void deleteTemplate(String id) {
        // [GHI CHÚ] Cần logic kiểm tra nếu có departure đang chạy thì không được xóa
        templateRepository.deleteById(id);
    }

    // === Hàm CRUD Tour Departure ===
    public List<TourDeparture> findAllDepartures(String companyId) {
        return departureRepository.findByCompanyId(companyId);
    }
    public TourDeparture findDepartureById(String id) {
        return departureRepository.findById(id).orElseThrow(() -> new RuntimeException("Departure not found"));
    }
    public TourDeparture saveDeparture(TourDeparture departure) {
        return departureRepository.save(departure);
    }

    // === Hàm JOIN cho Trang List/Trang Chủ (POST /tours/search) ===
    public List<TourSearchResponse> searchAndJoinTours(TourSearchRequest request) {
        List<TourDeparture> departures = departureRepository.findByCompanyId(request.getCompanyId());
        List<TourTemplate> templates = templateRepository.findByCompanyId(request.getCompanyId());
        List<Category> categories = categoryRepository.findByCompanyId(request.getCompanyId());

        var templateMap = templates.stream().collect(Collectors.toMap(TourTemplate::getId, t -> t));
        var categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));

        return departures.stream()
                .filter(dep -> templateMap.containsKey(dep.getTourTemplateId()))
                .map(dep -> {
                    TourTemplate tpl = templateMap.get(dep.getTourTemplateId());

                    String categoryName = categoryMap.getOrDefault(tpl.getCategoryId(), "Unknown");

                    // --- Lọc theo Request FE ---
                    // Ưu tiên lọc theo categoryId (chuẩn khóa ngoại)
                    if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()
                            && !tpl.getCategoryId().equals(request.getCategoryId())) {
                        return null;
                    }
                    if (request.getDestination() != null && !request.getDestination().isEmpty() &&
                            !tpl.getDestination().toLowerCase().contains(request.getDestination().toLowerCase())) {
                        return null;
                    }

                    long days = ChronoUnit.DAYS.between(dep.getStartDate(), dep.getEndDate()) + 1;
                    String duration = days + " ngày " + (days - 1) + " đêm";

                    return new TourSearchResponse(
                            dep.getId(),
                            tpl.getName(),
                            tpl.getDestination(),
                            duration,
                            dep.getPrice(),
                            dep.getOriginalPrice(),
                            dep.getDiscountPercent(),
                            4.8, // Giả lập Rating
                            tpl.getDescriptionHtml(),
                            tpl.getImageUrl(),
                            categoryName
                    );
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public TourDeparture updateDeparture(String id, TourDeparture departureDetails) {
        // 1. Tìm Departure cũ
        TourDeparture existingDeparture = departureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departure not found with id: " + id));

        existingDeparture.setStartDate(departureDetails.getStartDate());
        existingDeparture.setEndDate(departureDetails.getEndDate());
        existingDeparture.setTotalSlots(departureDetails.getTotalSlots());
        existingDeparture.setStatus(departureDetails.getStatus());
        return departureRepository.save(existingDeparture);
    }

    public void deleteDeparture(String id) {
        departureRepository.deleteById(id);

    }
    public TourTemplate updateTemplate(String id, TourTemplate templateDetails) {
        TourTemplate existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour Template not found with id: " + id));

        // Cập nhật các trường:
        existingTemplate.setName(templateDetails.getName());
        existingTemplate.setDestination(templateDetails.getDestination());
        existingTemplate.setDescriptionHtml(templateDetails.getDescriptionHtml());
        existingTemplate.setImageUrl(templateDetails.getImageUrl());
        existingTemplate.setDefaultPrice(templateDetails.getDefaultPrice());
        existingTemplate.setDiscountPercent(templateDetails.getDiscountPercent());
        existingTemplate.setCategoryId(templateDetails.getCategoryId());

        return templateRepository.save(existingTemplate);
    }
    // === BỔ SUNG: Hàm lấy Manifest (Cho TourDepartureController) ===
    public Object getDriverManifest(String departureId) {
        // [GHI CHÚ] Logic nghiệp vụ: Lấy chi tiết chuyến đi, tài xế được gán, và danh sách khách (bookings)
        // Đây sẽ là một DTO phức tạp (ví dụ: DriverManifestResponse)
        System.out.println("LOGIC: Lấy Manifest cho Departure ID: " + departureId);
        // Tạm thời trả về object giả
        return new Object();
    }

}