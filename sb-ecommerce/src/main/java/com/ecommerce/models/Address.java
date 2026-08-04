package com.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(nullable = false, length = 100, name = "street")
    private String street;

    @Column(nullable = false, length = 100, name = "building_number")
    private String buildingNumber;

    @Column(nullable = false, length = 100, name = "city")
    private String city;

    @Column(nullable = false, length = 100, name = "state")
    private String state;

    @Column(nullable = false, length = 100, name = "country")
    private String country;

    @Column(nullable = false, length = 10, name = "zip_code")
    private String zipCode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private Set<User> users = new HashSet<>();
}
