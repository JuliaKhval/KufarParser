package org.example.kufarparser.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "apartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {

    @Id
    private String id;

    private String priceBYN;
    private String priceUSD;
    private String pricePerMeter;
    @Column(length = 512)
    private String address;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String datePosted;
    private String rooms;
    private String area;
    private String floor;
    @Column(columnDefinition = "TEXT")
    private String photos;
    @Column(length = 512)
    private String url;
}