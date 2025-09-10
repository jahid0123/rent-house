package com.jmjbrothers.spring.securtiy.authentication.service;


import com.jmjbrothers.spring.securtiy.authentication.dto.MyPostPropertyResponseDto;
import com.jmjbrothers.spring.securtiy.authentication.dto.MyUnlockPropertyDto;
import com.jmjbrothers.spring.securtiy.authentication.dto.PropertyUnlockDto;
import com.jmjbrothers.spring.securtiy.authentication.model.PropertyPost;
import com.jmjbrothers.spring.securtiy.authentication.model.PropertyUnlock;
import com.jmjbrothers.spring.securtiy.authentication.model.User;
import com.jmjbrothers.spring.securtiy.authentication.repository.PropertyPostRepository;
import com.jmjbrothers.spring.securtiy.authentication.repository.PropertyUnlockRepository;
import com.jmjbrothers.spring.securtiy.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyUnlockService {

    private final PropertyUnlockRepository propertyUnlockRepository;
    private final UserRepository userRepository;
    private final PropertyPostRepository propertyPostRepository;


    @Transactional
    public PropertyUnlock propertyUnlock(PropertyUnlockDto propertyUnlockDto) {
        Long userId = propertyUnlockDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));
        System.out.println(user.toString());

        Long propertyPostId = propertyUnlockDto.getPropertyPostId();
        PropertyPost propertyPost = propertyPostRepository.findById(propertyPostId).orElseThrow(
                () -> new RuntimeException("PropertyPost not found"));
        System.out.println(propertyPost.toString());

        if (user.getBalanceCredits()<5){
            System.out.println("No enough credit");
            throw new RuntimeException("Insufficient credits");
        }
        user.setBalanceCredits(user.getBalanceCredits()-5);
        userRepository.save(user);

        PropertyUnlock propertyUnlock = new PropertyUnlock();
        propertyUnlock.setUser(user);
        propertyUnlock.setPropertyPost(propertyPost);

        return propertyUnlockRepository.save(propertyUnlock);
    }

    @Transactional
    public List<MyUnlockPropertyDto> allPropertyUnlockById(Long id) {
        List<PropertyUnlock> allPropertyUnlock = propertyUnlockRepository.findAllByUserId(id);
        return allPropertyUnlock.stream().map(this::unlockPropertyDto).collect(Collectors.toList());
    }

    private MyUnlockPropertyDto unlockPropertyDto(PropertyUnlock propertyUnlock) {

        MyUnlockPropertyDto propertyUnlockDto = new MyUnlockPropertyDto();
        propertyUnlockDto.setUnlockId(propertyUnlock.getId());
        propertyUnlockDto.setPostId(propertyUnlock.getPropertyPost().getId());
        propertyUnlockDto.setCreditsUsed(propertyUnlock.getCreditsUsed());
        propertyUnlockDto.setDateUnlocked(propertyUnlock.getDateUnlocked());
        propertyUnlockDto.setContactPerson(propertyUnlock.getPropertyPost().getContactPerson());
        propertyUnlockDto.setContactNumber(propertyUnlock.getPropertyPost().getContactNumber());
        propertyUnlockDto.setAvailableFrom(propertyUnlock.getPropertyPost().getAvailableFrom());
        propertyUnlockDto.setCategory(propertyUnlock.getPropertyPost().getProperty().getCategory());
        propertyUnlockDto.setTitle(propertyUnlock.getPropertyPost().getProperty().getTitle());
        propertyUnlockDto.setDescription(propertyUnlock.getPropertyPost().getProperty().getDescription());
        propertyUnlockDto.setIsAvailable(propertyUnlock.getPropertyPost().getProperty().getIsAvailable());
        propertyUnlockDto.setRentAmount(propertyUnlock.getPropertyPost().getProperty().getRentAmount());
        propertyUnlockDto.setAdPostedDate(propertyUnlock.getPropertyPost().getDatePosted());
        propertyUnlockDto.setDivision(propertyUnlock.getPropertyPost().getProperty().getDivision());
        propertyUnlockDto.setDistrict(propertyUnlock.getPropertyPost().getProperty().getDistrict());
        propertyUnlockDto.setThana(propertyUnlock.getPropertyPost().getProperty().getThana());
        propertyUnlockDto.setSection(propertyUnlock.getPropertyPost().getProperty().getSection());
        propertyUnlockDto.setAddress(propertyUnlock.getPropertyPost().getProperty().getAddress());
        propertyUnlockDto.setRoadNumber(propertyUnlock.getPropertyPost().getProperty().getRoadNumber());
        propertyUnlockDto.setHouseNumber(propertyUnlock.getPropertyPost().getProperty().getHouseNumber());

        return propertyUnlockDto;

    }
}

