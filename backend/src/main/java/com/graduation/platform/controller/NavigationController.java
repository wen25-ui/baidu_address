package com.graduation.platform.controller;

import com.graduation.platform.model.dto.NavigationDTO;
import com.graduation.platform.model.entity.Route;
import com.graduation.platform.service.NavigationService;
import com.graduation.platform.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @GetMapping("/routes")
    public Result<List<Route>> getRoutes() {
        List<Route> routes = navigationService.getRoutes();
        return Result.success(routes);
    }

    @PostMapping("/save")
    public Result<String> saveRoute(@RequestBody NavigationDTO navigationDTO) {
        navigationService.saveRoute(navigationDTO);
        return Result.success("Route saved successfully");
    }

    @DeleteMapping("/routes/{id}")
    public Result<String> deleteRoute(@PathVariable Long id) {
        navigationService.deleteRoute(id);
        return Result.success("Route deleted successfully");
    }
}