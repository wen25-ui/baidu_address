package com.graduation.platform.service.impl;

import com.graduation.platform.service.NavigationService;
import com.graduation.platform.model.dto.NavigationDTO;
import com.graduation.platform.model.entity.Route;
import com.graduation.platform.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavigationServiceImpl implements NavigationService {

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public List<Route> getRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }

    @Override
    public void saveRoute(NavigationDTO navigationDTO) {
        Route route = new Route();
        route.setStartLocation(navigationDTO.getStartLocation());
        route.setEndLocation(navigationDTO.getEndLocation());
        routeRepository.save(route);
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}