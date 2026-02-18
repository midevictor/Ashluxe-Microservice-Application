package com.app.ecom.service;

import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.model.Address;
import com.app.ecom.model.User;
import com.app.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
//    private List<User> userList = new ArrayList<>();

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);
    }



    public Optional<UserResponse> getUser(Long id) {
        return userRepository.findById(id)
                .map((this::mapToUserResponse));
    }

    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
      return userRepository.findById(id)
              .map(exisingUser -> {
                   updateUserFromRequest(exisingUser, updatedUserRequest);
                    userRepository.save(exisingUser);
                    return true;
              }).orElse(false);

    }

    private UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(Long.valueOf(String.valueOf(user.getId())));
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());

       if (user.getAddress() != null) {
           AddressDTO addressDTO = new AddressDTO();
              addressDTO.setStreet(user.getAddress().getStreet());
                addressDTO.setCity(user.getAddress().getCity());
                addressDTO.setState(user.getAddress().getState());
                addressDTO.setZipCode(user.getAddress().getZipCode());
                addressDTO.setCountry(user.getAddress().getCountry());
                userResponse.setAddress(addressDTO);
       }
        return userResponse;
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRole(userRequest.getRole());
        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setZipCode(userRequest.getAddress().getZipCode());
            address.setCountry(userRequest.getAddress().getCountry());
            user.setAddress(address);
        }
    }
}
