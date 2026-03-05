package com.graduation.platform.controller;

import com.graduation.platform.model.dto.NavigationDTO;
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
    public Result<List<NavigationDTO>> getRoutes() {
        List<NavigationDTO> routes = navigationService.getAllRoutes();
        return Result.success(routes);
    }

    @PostMapping("/start")
    public Result<String> startNavigation(@RequestBody NavigationDTO navigationDTO) {
        navigationService.startNavigation(navigationDTO);
        return Result.success("Navigation started successfully");
    }

    @PostMapping("/stop")
    public Result<String> stopNavigation(@RequestBody NavigationDTO navigationDTO) {
        navigationService.stopNavigation(navigationDTO);
        return Result.success("Navigation stopped successfully");
    }
}