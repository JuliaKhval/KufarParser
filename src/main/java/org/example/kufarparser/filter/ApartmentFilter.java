package org.example.kufarparser.filter;

import org.example.kufarparser.model.Apartment;
import org.example.kufarparser.request.FilterRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApartmentFilter {

    public static List<Apartment> applyFilters(List<Apartment> apartments, FilterRequest request) {

        List<Apartment> result = new ArrayList<>(apartments);


        if (request.getStreet() != null && !request.getStreet().trim().isEmpty()) {
            result = filterByStreet(result, request.getStreet());
        }


        if (request.getRooms() != null && !request.getRooms().isEmpty()) {
            result = filterByRooms(result, request.getRooms());
        }


        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            result = filterByPriceRange(result, request.getMinPrice(), request.getMaxPrice());
        }



        return result;
    }

    public static List<Apartment> filterByStreet(List<Apartment> apartments, String street) {
        String normalized = street.toLowerCase().trim();
        return apartments.stream()
                .filter(apt -> apt.getAddress() != null && apt.getAddress().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByRooms(List<Apartment> apartments, List<Integer> requiredRooms) {
        return apartments.stream()
                .filter(apt -> requiredRooms.contains(extractRoomCount(apt.getRooms())))
                .toList();
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


    private static int extractRoomCount(String roomsStr) {
        if (roomsStr == null || roomsStr.isEmpty()) return -1;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(roomsStr);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }


}