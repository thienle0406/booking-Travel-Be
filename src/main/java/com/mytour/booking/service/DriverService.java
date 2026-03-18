package com.mytour.booking.service;

import com.mytour.booking.entity.Driver; // Giả định Driver Entity tồn tại
import com.mytour.booking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Khớp với apiService.drivers.getAll
     * Lấy danh sách tài xế theo companyId.
     */
    public List<Driver> findAllDrivers(String companyId) {
        return driverRepository.findByCompanyId(companyId);
    }

    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver updateDriver(String id, Driver driverDetails) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (!optionalDriver.isPresent()) {
            throw new RuntimeException("Driver not found with id: " + id);
        }

        Driver driver = optionalDriver.get();
        driver.setName(driverDetails.getName());
        driver.setPhone(driverDetails.getPhone());
        driver.setStatus(driverDetails.getStatus());
        driver.setCompanyId(driverDetails.getCompanyId());

        return driverRepository.save(driver);
    }

    public void deleteDriver(String id) {
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

}