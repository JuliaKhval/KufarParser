package org.example.kufarparser.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
        private String street;
        private Integer Rooms;

        private Double minPrice;
        private Double maxPrice;
}