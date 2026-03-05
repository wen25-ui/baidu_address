package com.graduation.platform.service;

import com.graduation.platform.model.dto.NavigationDTO;
import com.graduation.platform.model.entity.Route;

import java.util.List;

public interface NavigationService {
    List<Route> getRoutes();
    Route getRouteById(Long id);
    void saveRoute(NavigationDTO navigationDTO);
    void deleteRoute(Long id);
}