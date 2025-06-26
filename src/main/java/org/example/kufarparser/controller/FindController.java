package org.example.kufarparser.controller;

import lombok.RequiredArgsConstructor;
import org.example.kufarparser.filter.ApartmentFilter;
import org.example.kufarparser.parser.KufarParser;
import org.example.kufarparser.model.Apartment;
import org.example.kufarparser.repository.ApartmentRepository;
import org.example.kufarparser.repository.DistrictRepository;
import org.example.kufarparser.request.FilterRequest;

import org.example.kufarparser.service.EmailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/find")
@RequiredArgsConstructor
public class FindController {

    private final ApartmentRepository apartmentRepo;
    private final DistrictRepository districtRepo;
    private final EmailService emailService;


    @PostMapping
    public List<Apartment> find(@RequestBody FilterRequest request) {


        KufarParser.parseAndSave(request.getCity(), request.getType(), apartmentRepo);
        List<Apartment> all = apartmentRepo.findAll();
        List<Apartment> filtered = ApartmentFilter.applyFilters(all, request, districtRepo);


        if (!filtered.isEmpty()) {
            try {
                emailService.sendJsonEmail(filtered);
            } catch (Exception e) {
                System.err.println(" Не удалось отправить письмо: " + e.getMessage());
            }
        }

        return filtered;

    }
}