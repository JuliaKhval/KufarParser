package org.example.kufarparser.controller;

import org.example.kufarparser.filter.ApartmentFilter;
import org.example.kufarparser.parser.KufarParser;
import org.example.kufarparser.model.Apartment;
import org.example.kufarparser.request.FilterRequest;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/find")
public class FindController {
    @PostMapping
    public List<Apartment> find(@RequestBody FilterRequest request) {
        List<Apartment> all = KufarParser.parseApartments();

        return ApartmentFilter.applyFilters(
                all,
                request.getStreet(),
                request.getRooms(),
                request.getMinPrice(),
                request.getMaxPrice()
        );
    }
}