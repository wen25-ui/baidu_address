package com.graduation.platform.service;

import com.graduation.platform.model.dto.NavigationDTO;

public interface NavigationService {
    NavigationDTO startNavigation(Long userId, Long routeId);
    NavigationDTO getNavigationStatus(Long navigationId);
    void endNavigation(Long navigationId);
}