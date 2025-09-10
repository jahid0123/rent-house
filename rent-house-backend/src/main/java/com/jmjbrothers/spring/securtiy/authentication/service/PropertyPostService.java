package com.jmjbrothers.spring.securtiy.authentication.service;

import com.jmjbrothers.spring.securtiy.authentication.dto.*;
import com.jmjbrothers.spring.securtiy.authentication.model.Property;
import com.jmjbrothers.spring.securtiy.authentication.model.PropertyPost;
import com.jmjbrothers.spring.securtiy.authentication.model.User;
import com.jmjbrothers.spring.securtiy.authentication.repository.PropertyPostRepository;
import com.jmjbrothers.spring.securtiy.authentication.repository.PropertyRepository;
import com.jmjbrothers.spring.securtiy.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyPostService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyPostRepository propertyPostRepository;




    @Transactional
    public PropertyPost postProperty(PropertyPostDto dto, MultipartFile[] images) throws IOException {
        User user = userRepository.findById(dto.getUserID())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalanceCredits() < 10) {
            throw new RuntimeException("Insufficient credits");
        }

        Property property = new Property();
        property.setUser(user);
        property.setCategory(dto.getCategory());
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setAddress(dto.getAddress());
        property.setRentAmount(dto.getRentAmount());
        property.setDivision(dto.getDivision());
        property.setDistrict(dto.getDistrict());
        property.setThana(dto.getThana());
        property.setSection(dto.getSection());
        property.setRoadNumber(dto.getRoadNumber());
        property.setHouseNumber(dto.getHouseNumber());

        List<String> imagePaths = new ArrayList<>();

        if (images != null && images.length > 0) {
            Path uploadDir = Paths.get("uploads");

            // Ensure upload directory exists only once
            Files.createDirectories(uploadDir);

            for (MultipartFile file : images) {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(filename);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePaths.add(filename);
            }
        }

        property.setImageListPaths(imagePaths);

        // Deduct credit
        user.setBalanceCredits(user.getBalanceCredits() - 10);
        user = userRepository.save(user);

        property = propertyRepository.save(property);

        System.out.println("Property Save Successfully.");

        PropertyPost post = new PropertyPost();
        post.setUser(user);
        post.setProperty(property);
        post.setContactPerson(dto.getContactPerson());
        post.setContactNumber(dto.getContactNumber());
        post.setAvailableFrom(dto.getAvailableFrom());

        PropertyPost savedPostProperty = propertyPostRepository.save(post);
        System.out.println("Property Post Save Successfully.");



        return savedPostProperty;
    }



    /*@Transactional
    public PropertyPost postProperty(PropertyPostDto propertyPostDto) {
        User user = userRepository.findById(propertyPostDto.getUserID()).orElseThrow(
                ()-> new RuntimeException("User not found"));
        System.out.println(user.toString());

        if (user.getBalanceCredits()<10){
            System.out.println("No enough credit");
            throw new RuntimeException("Insufficient credits");
        }

        //Create new property
        Property property = new Property();
        property.setUser(user);
        property.setCategory(propertyPostDto.getCategory());
        property.setTitle(propertyPostDto.getTitle());
        property.setDescription(propertyPostDto.getDescription());
        property.setAddress(propertyPostDto.getAddress());
        property.setRentAmount(propertyPostDto.getRentAmount());
        property.setDivision(propertyPostDto.getDivision());
        property.setDistrict(propertyPostDto.getDistrict());
        property.setThana(propertyPostDto.getThana());
        property.setSection(propertyPostDto.getSection());
        property.setRoadNumber(propertyPostDto.getRoadNumber());
        property.setHouseNumber(propertyPostDto.getHouseNumber());

        propertyRepository.save(property);

        //deduct credit
        user.setBalanceCredits(user.getBalanceCredits()-10);
        userRepository.save(user);

        //Create new property post
        PropertyPost propertyPost = new PropertyPost();
        propertyPost.setUser(user);
        propertyPost.setProperty(property);
        propertyPost.setContactPerson(propertyPostDto.getContactPerson());
        propertyPost.setContactNumber(propertyPostDto.getContactNumber());
        propertyPost.setArea(propertyPostDto.getArea());
        propertyPost.setAvailableFrom(propertyPostDto.getAvailableFrom());

        return propertyPostRepository.save(propertyPost);
    }*/

    /*        //the easy way to mapped list PropertyPost List to GetPostedProperty List
            List<GetPostedProperty> getPostedPropertyList = new ArrayList<>();
            for (PropertyPost property : allPostedProperty) {
                GetPostedProperty dto = getPostedPropertyMapped(property);  // convert it
                getPostedPropertyList.add(dto);                         // add to new list
            }

            return getPostedPropertyList;*/
    @Transactional
    public List<GetPostedProperty> getAllPostedProperty() {

        List<PropertyPost> allPostedProperty = propertyPostRepository.findAll();
        return allPostedProperty.stream().map(this::getPostedPropertyMapped).collect(Collectors.toList());
    }


    //Mapped method PropertyPost class to GetPostedProperty
    private GetPostedProperty getPostedPropertyMapped(PropertyPost post) {
        Property property = post.getProperty();

        GetPostedProperty dto = new GetPostedProperty();
        dto.setId(post.getId());
        dto.setCategory(property.getCategory());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setIsAvailable(property.getIsAvailable());
        dto.setRentAmount(property.getRentAmount());
        dto.setDatePosted(post.getDatePosted());
        dto.setAvailableFrom(post.getAvailableFrom());
        dto.setDivision(property.getDivision());
        dto.setDistrict(property.getDistrict());
        dto.setThana(property.getThana());
        dto.setSection(property.getSection());
        dto.setImageUrls(post.getProperty().getImageListPaths());

        return dto;
    }


    @Transactional
    public List<MyPostPropertyResponseDto> allPropertyPostedByMe(Long id) {
        List<PropertyPost> allPropertyUnlock = propertyPostRepository.findAllByUserId(id);
        return allPropertyUnlock.stream().map(this::myPostPropertyResponseDto).collect(Collectors.toList());
    }

    private MyPostPropertyResponseDto myPostPropertyResponseDto (PropertyPost propertyPost){
        MyPostPropertyResponseDto myPost = new MyPostPropertyResponseDto();
        myPost.setId(propertyPost.getId());
        myPost.setContactNumber(propertyPost.getContactNumber());
        myPost.setContactPerson(propertyPost.getContactPerson());
        myPost.setAvailableFrom(propertyPost.getAvailableFrom());
        myPost.setCategory(propertyPost.getProperty().getCategory());
        myPost.setTitle(propertyPost.getProperty().getTitle());
        myPost.setDescription(propertyPost.getProperty().getDescription());
        myPost.setIsAvailable(propertyPost.getProperty().getIsAvailable());
        myPost.setRentAmount(propertyPost.getProperty().getRentAmount());
        myPost.setDatePosted(propertyPost.getProperty().getDatePosted());
        myPost.setDivision(propertyPost.getProperty().getDivision());
        myPost.setDistrict(propertyPost.getProperty().getDistrict());
        myPost.setThana(propertyPost.getProperty().getThana());
        myPost.setSection(propertyPost.getProperty().getSection());
        myPost.setRoadNumber(propertyPost.getProperty().getRoadNumber());
        myPost.setHouseNumber(propertyPost.getProperty().getHouseNumber());
        myPost.setAddress(propertyPost.getProperty().getAddress());
        myPost.setImageUrls(propertyPost.getProperty().getImageListPaths());

        return myPost;
    }

    @Transactional
    public String deleteMyPostedProperties(Long id) {
        PropertyPost post = propertyPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // First delete the post that holds the foreign key
        propertyPostRepository.deleteById(id);

        // Then delete the property if needed (and if not used elsewhere)
        propertyRepository.deleteById(post.getProperty().getId());


        return "Post deleted successfully!";
    }

    public MyPostPropertyResponseDto updateMyPostedProperties(UpdateMyPostedPropertyDto propertyDto) {
        PropertyPost propertyPost = propertyPostRepository.findById(propertyDto.getPostId()).orElseThrow(
                ()->new RuntimeException("Property not found with id "+propertyDto.getPostId()));

        Property findProperty = propertyRepository.findById(propertyPost.getProperty().getId()).orElseThrow(
                () -> new NullPointerException("Property not found by Id: " + propertyPost.getProperty().getId())
        );

        Property saved = saveProperty(findProperty, propertyDto);


        propertyPost.setContactNumber(propertyDto.getContactNumber());
        propertyPost.setContactPerson(propertyDto.getContactPerson());
        propertyPost.setAvailableFrom(propertyDto.getAvailableFrom());


        return myPostPropertyResponseDto(propertyPostRepository.save(propertyPost));
    }

    private Property saveProperty(Property findProperty, UpdateMyPostedPropertyDto propertyDto) {

        findProperty.setCategory(propertyDto.getCategory());
        findProperty.setTitle(propertyDto.getTitle());
        findProperty.setDescription(propertyDto.getDescription());
        findProperty.setRentAmount(propertyDto.getRentAmount());
        findProperty.setDivision(propertyDto.getDivision());
        findProperty.setDistrict(propertyDto.getDistrict());
        findProperty.setThana(propertyDto.getThana());
        findProperty.setSection(propertyDto.getSection());
        findProperty.setRoadNumber(propertyDto.getRoadNumber());
        findProperty.setHouseNumber(propertyDto.getHouseNumber());
        findProperty.setAddress(propertyDto.getAddress());

        return  propertyRepository.save(findProperty);

    }


    @Transactional
    public List<PostedPropertyResponseDto> allPostedProperty() {
        List<PropertyPost> propertyPosts = propertyPostRepository.findAll();
        return propertyPosts.stream().map(this::postedPropertyResponseDto).collect(Collectors.toList());
    }

    private PostedPropertyResponseDto  postedPropertyResponseDto (PropertyPost propertyPost){
        PostedPropertyResponseDto myPost = new PostedPropertyResponseDto();
        myPost.setPostId(propertyPost.getId());
        myPost.setUserId(propertyPost.getUser().getId());
        myPost.setContactNumber(propertyPost.getContactNumber());
        myPost.setContactPerson(propertyPost.getContactPerson());
        myPost.setAvailableFrom(propertyPost.getAvailableFrom());
        myPost.setCategory(propertyPost.getProperty().getCategory());
        myPost.setTitle(propertyPost.getProperty().getTitle());
        myPost.setDescription(propertyPost.getProperty().getDescription());
        myPost.setIsAvailable(propertyPost.getProperty().getIsAvailable());
        myPost.setRentAmount(propertyPost.getProperty().getRentAmount());
        myPost.setDatePosted(propertyPost.getProperty().getDatePosted());
        myPost.setDivision(propertyPost.getProperty().getDivision());
        myPost.setDistrict(propertyPost.getProperty().getDistrict());
        myPost.setThana(propertyPost.getProperty().getThana());
        myPost.setSection(propertyPost.getProperty().getSection());
        myPost.setRoadNumber(propertyPost.getProperty().getRoadNumber());
        myPost.setHouseNumber(propertyPost.getProperty().getHouseNumber());
        myPost.setAddress(propertyPost.getProperty().getAddress());

        return myPost;
    }

    @Transactional
    public PropertyPost updatePostedProperty(PropertyPostUpdateDto post) {

        PropertyPost propertyPost = propertyPostRepository.findById(post.getPostId()).orElseThrow(
                ()-> new NullPointerException("PropertyPost not found by Id: " + post.getPostId())
        );

        Property property = propertyPost.getProperty();
        property.setIsAvailable(post.getIsAvailable());
        propertyPost.setProperty(property);
        return propertyPostRepository.save(propertyPost);

    }
}
