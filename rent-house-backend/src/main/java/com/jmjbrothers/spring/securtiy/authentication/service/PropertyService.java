package com.jmjbrothers.spring.securtiy.authentication.service;

import com.jmjbrothers.spring.securtiy.authentication.dto.PropertyDto;
import com.jmjbrothers.spring.securtiy.authentication.model.Property;
import com.jmjbrothers.spring.securtiy.authentication.model.User;
import com.jmjbrothers.spring.securtiy.authentication.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserInfoDetailsService userDetailsService;

    public PropertyService(PropertyRepository propertyRepository, UserInfoDetailsService userDetailsService){
        this.propertyRepository = propertyRepository;
        this.userDetailsService = userDetailsService;
    }


    @Transactional
    public Property createProperty(PropertyDto propertyDto) {

        Long userId = propertyDto.getUserId();
        User user = userDetailsService.userFindById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Property property = new Property();
        property.setUser(user);
        property.setCategory(propertyDto.getCategory());
        property.setAddress(propertyDto.getAddress());
        property.setDescription(propertyDto.getDescription());
        property.setTitle(propertyDto.getTitle());
        property.setRentAmount(propertyDto.getRentAmount());
        return propertyRepository.save(property);

    }
}
