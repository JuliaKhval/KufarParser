package org.example.kufarparser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {
    private String priceBYN;
    private String priceUSD;
    private String pricePerMeter;
    private String address;
    private String description;
    private String datePosted;
    private String rooms;
    private String area;
    private String floor;
    private List<String> photos;
    private String url;
}