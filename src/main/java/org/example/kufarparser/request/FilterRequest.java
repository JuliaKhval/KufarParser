package org.example.kufarparser.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

        private String type;
        private String district;
        private String street;
        private List<Integer> Rooms;
        private Double minPrice;
        private Double maxPrice;
}