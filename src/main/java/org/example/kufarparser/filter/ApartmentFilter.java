package org.example.kufarparser.filter;

import org.example.kufarparser.model.Apartment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApartmentFilter {

    public static List<Apartment> filterByStreet(List<Apartment> apartments, String street) {
        if (street == null || street.trim().isEmpty()) return apartments;

        String normalized = street.toLowerCase().trim();
        return apartments.stream()
                .filter(apt -> apt.getAddress() != null && apt.getAddress().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByRooms(List<Apartment> apartments, Integer requiredRooms) {
        if (requiredRooms == null) return apartments;

        return apartments.stream()
                .filter(apt -> extractRoomCount(apt.getRooms()) == requiredRooms)
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByMinPrice(List<Apartment> apartments, Double minPrice) {
        if (minPrice == null) return apartments;

        return apartments.stream()
                .filter(apt -> extractPrice(apt.getPriceUSD()) >= minPrice)
                .collect(Collectors.toList());
    }

    public static List<Apartment> filterByMaxPrice(List<Apartment> apartments, Double maxPrice) {
        if (maxPrice == null) return apartments;

        return apartments.stream()
                .filter(apt -> extractPrice(apt.getPriceUSD()) <= maxPrice)
                .collect(Collectors.toList());
    }


    public static List<Apartment> applyFilters(List<Apartment> apartments,
                                               String street,
                                               Integer rooms,
                                               Double minPrice,
                                               Double maxPrice) {

        List<Apartment> result = new ArrayList<>(apartments);

        if (street != null) {
            result = filterByStreet(result, street);
        }

        if (rooms != null) {
            result = filterByRooms(result, rooms);
        }

        if (minPrice != null) {
            result = filterByMinPrice(result, minPrice);
        }

        if (maxPrice != null) {
            result = filterByMaxPrice(result, maxPrice);
        }

        return result;
    }


    private static int extractRoomCount(String roomsStr) {
        if (roomsStr == null || roomsStr.isEmpty()) return -1;

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(roomsStr);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }

    private static double extractPrice(String priceStr) {
        if (priceStr == null || "договорная".equalsIgnoreCase(priceStr)) return 0;
        return Double.parseDouble(priceStr.replaceAll("[^\\d.]", ""));
    }
}