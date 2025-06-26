package org.example.kufarparser.filter;

import lombok.RequiredArgsConstructor;
import org.example.kufarparser.model.Apartment;
import org.example.kufarparser.model.Street;
import org.example.kufarparser.repository.ApartmentRepository;
import org.example.kufarparser.repository.DistrictRepository;
import org.example.kufarparser.request.FilterRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApartmentFilter {




    public static List<Apartment> applyFilters(List<Apartment> apartments, FilterRequest request,DistrictRepository districtRepo) {

        List<Apartment> result = new ArrayList<>(apartments);
        result = filterByCity(result,request.getCity());


        if (request.getStreet() != null && !request.getStreet().trim().isEmpty()) {
            result = filterByStreet(result, request.getStreet());
        }

        if(request.getDistrict() != null && !request.getDistrict().trim().isEmpty()) {
            result = filterByDistrict(result,request.getDistrict(),districtRepo);
        }

        result = filterByRooms(result, request.getRooms());



        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            result = filterByPriceRange(result,
                    request.getMinPrice() != null ? request.getMinPrice() : 0,
                    request.getMaxPrice() != null ? request.getMaxPrice() : Double.MAX_VALUE);
        }

        return result;
    }
    public static List<Apartment> filterByCity(List<Apartment> apartments, String city) {
        String normalized = city.toLowerCase().trim();
        return apartments.stream()
                .filter(apt -> apt.getAddress() != null && apt.getAddress().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }
    public static List<Apartment> filterByStreet(List<Apartment> apartments, String street) {
        String normalized = street.toLowerCase().trim();
        return apartments.stream()
                .filter(apt -> apt.getAddress() != null && apt.getAddress().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByRooms(List<Apartment> apartments, List<Integer> requiredRooms) {
        if (requiredRooms == null || requiredRooms.isEmpty()) {
            return apartments;
        }

        return apartments.stream()
                .filter(apt -> {
                    Integer rooms = extractRoomCount(apt.getRooms());
                    return rooms != null && requiredRooms.contains(rooms);
                })
                .collect(Collectors.toList());
    }



    public static List<Apartment> filterByPriceRange(List<Apartment> apartments, double minPrice, double maxPrice) {
        return apartments.stream().filter(apt -> {
                    String priceStr = apt.getPriceUSD();


                    if ("не указано".equalsIgnoreCase(priceStr)) {
                        return true;
                    }


                    try {
                        double price = Double.parseDouble(priceStr.replaceAll("[^\\d.]", ""));
                        return price >= minPrice && price <= maxPrice;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByDistrict(List<Apartment> apartments, String districtName, DistrictRepository districtRepo) {
        List<String> streets = districtRepo.findByName(districtName)
                .map(d -> d.getStreets().stream().map(Street::getName).toList())
                .orElse(Collections.emptyList());

        return apartments.stream()
                .filter(apt -> streets.stream().anyMatch(street ->
                        apt.getAddress().toLowerCase().contains(street.toLowerCase())))
                .collect(Collectors.toList());
    }

    private static int extractRoomCount(String roomsStr) {
        if (roomsStr == null || roomsStr.isEmpty()) return -1;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(roomsStr);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }


}