package com.app.manage_restaurant.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.app.manage_restaurant.dtos.request.OpeningHourRequest;
import com.app.manage_restaurant.dtos.request.RestaurantRequest;
import com.app.manage_restaurant.dtos.response.OpeningHourResponse;
import com.app.manage_restaurant.dtos.response.RestaurantHomeResponse;
import com.app.manage_restaurant.dtos.response.RestaurantResponse;
import com.app.manage_restaurant.dtos.response.RestaurantSpecialResponse;
import com.app.manage_restaurant.entities.OpeningHour;
import com.app.manage_restaurant.entities.Restaurant;

public class RestaurantMapper {

    // ==========================
    // TO ENTITY
    // ==========================
    public static Restaurant toEntity(RestaurantRequest request) {
        if (request == null) return null;

        Restaurant entity = new Restaurant();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setDescription(request.getDescription());
        entity.setCapacity(request.getCapacity());
        entity.setCurrency(request.getCurrency());
        entity.setLogo(request.getLogo());
        entity.setCuisine(request.getCuisine());
        entity.setCoverImage(request.getCoverImage());
        entity.setActive(request.getActive() != null ? request.getActive() : true);
        entity.setId(request.getId() != null ? request.getId() : null);

        // ✅ Conversion des horaires
        if (request.getOpeningHours() != null) {
        	Set<OpeningHour> hours = request.getOpeningHours().stream()
                    .map(RestaurantMapper::toOpeningHourEntity)
                    .collect(Collectors.toSet());
            entity.setOpeningHours(hours);
        }

        return entity;
    }

    private static OpeningHour toOpeningHourEntity(OpeningHourRequest request) {
        if (request == null) return null;
        OpeningHour oh = new OpeningHour();
        oh.setDays(request.getDays());
        oh.setOpen(request.getOpen());
        oh.setClose(request.getClose());
        oh.setClosed(request.getIsClosed() != null ? request.getIsClosed() : false);
        oh.setId(request.getId());
        return oh;
    }

    // ==========================
    // TO RESPONSE
    // ==========================
    public static RestaurantResponse toResponse(Restaurant entity) {
        if (entity == null) return null;

        RestaurantResponse response = new RestaurantResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setAddress(entity.getAddress());
        response.setCity(entity.getCity());
        response.setDescription(entity.getDescription());
        response.setCapacity(entity.getCapacity());
        response.setCurrency(entity.getCurrency());
        response.setLogo(entity.getLogo());
        response.setCoverImage(entity.getCoverImage());
        response.setActive(entity.isActive());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setCuisine(entity.getCuisine());

        // ✅ Conversion des horaires
        if (entity.getOpeningHours() != null) {
        	Set<OpeningHourResponse> hours = entity.getOpeningHours().stream()
                    .map(RestaurantMapper::toOpeningHourResponse)
                    .collect(Collectors.toSet());
            response.setOpeningHours(hours);
        }

        return response;
    }

    // ==========================
    // TO RESPONSE
    // ==========================
    public static RestaurantHomeResponse toHomeResponse(Restaurant entity) {
        if (entity == null) return null;

        RestaurantHomeResponse response = new RestaurantHomeResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setAddress(entity.getAddress());
        response.setCity(entity.getCity());
        response.setDescription(entity.getDescription());
        response.setCapacity(entity.getCapacity());
        response.setCurrency(entity.getCurrency());
        response.setLogo(entity.getLogo());
        response.setCoverImage(entity.getCoverImage());
        response.setActive(entity.isActive());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setCuisine(entity.getCuisine());

        // ✅ Conversion des horaires
        if (entity.getOpeningHours() != null) {
        	Set<OpeningHourResponse> hours = entity.getOpeningHours().stream()
                    .map(RestaurantMapper::toOpeningHourResponse)
                    .collect(Collectors.toSet());
            response.setOpeningHours(hours);
        }

        return response;
    }

    private static OpeningHourResponse toOpeningHourResponse(OpeningHour entity) {
        if (entity == null) return null;
        OpeningHourResponse response = new OpeningHourResponse();
        response.setId(entity.getId());
        response.setDays(entity.getDays());
        response.setOpen(entity.getOpen());
        response.setClose(entity.getClose());
        response.setIsClosed(entity.isClosed());
        return response;
    }

    public static RestaurantSpecialResponse toResponseSpecial(Restaurant entity) {
        if (entity == null) return null;
        RestaurantSpecialResponse response = new RestaurantSpecialResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

    // ==========================
    // UPDATE EXISTING ENTITY (SAFE)
    // ==========================
    public static void updateEntityFromRequest(RestaurantRequest request, Restaurant entity) {
        if (request == null || entity == null) return;

        if (isValidString(request.getName())) entity.setName(request.getName().trim());
        if (isValidString(request.getCuisine())) entity.setCuisine(request.getCuisine().trim());
        if (isValidEmail(request.getEmail())) entity.setEmail(request.getEmail().trim());
        if (isValidPhone(request.getPhone())) entity.setPhone(request.getPhone().trim());
        if (isValidString(request.getAddress())) entity.setAddress(request.getAddress().trim());
        if (isValidString(request.getCity())) entity.setCity(request.getCity().trim());
        if (isValidString(request.getDescription())) entity.setDescription(request.getDescription().trim());
        if (request.getCapacity() != null && request.getCapacity() > 0) entity.setCapacity(request.getCapacity());
        if (isValidString(request.getCurrency())) entity.setCurrency(request.getCurrency().trim());
        if (request.getActive() != null) entity.setActive(request.getActive());

        // ✅ Met à jour les horaires
        if (request.getOpeningHours() != null) {
        	Set<OpeningHour> hours = request.getOpeningHours().stream()
                    .map(RestaurantMapper::toOpeningHourEntity)
                    .collect(Collectors.toSet());
            entity.setOpeningHours(hours);
        }
    }

    // ==========================
    // VALIDATION
    // ==========================
    private static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static boolean isValidEmail(String email) {
        return isValidString(email) && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private static boolean isValidPhone(String phone) {
        return isValidString(phone) && phone.matches("^[0-9+()\\-\\s]{6,20}$");
    }

    // ==========================
    // TO REQUEST (UTILITY)
    // ==========================
    public static RestaurantRequest toRequest(Restaurant entity) {
        if (entity == null) return null;

        RestaurantRequest request = new RestaurantRequest();
        request.setName(entity.getName());
        request.setEmail(entity.getEmail());
        request.setPhone(entity.getPhone());
        request.setAddress(entity.getAddress());
        request.setCity(entity.getCity());
        request.setDescription(entity.getDescription());
        request.setCapacity(entity.getCapacity());
        request.setCurrency(entity.getCurrency());
        request.setLogo(entity.getLogo());
        request.setCoverImage(entity.getCoverImage());
        request.setActive(entity.isActive());
        request.setCuisine(entity.getCuisine());

        if (entity.getOpeningHours() != null) {
        	Set<OpeningHourRequest> hours = entity.getOpeningHours().stream()
                    .map(RestaurantMapper::toOpeningHourRequest)
                    .collect(Collectors.toSet());
            request.setOpeningHours(hours);
        }

        return request;
    }

    private static OpeningHourRequest toOpeningHourRequest(OpeningHour entity) {
        if (entity == null) return null;
        OpeningHourRequest req = new OpeningHourRequest();
        req.setId(entity.getId());
        req.setDays(entity.getDays());
        req.setOpen(entity.getOpen());
        req.setClose(entity.getClose());
        req.setIsClosed(entity.isClosed());
        return req;
    }
}
