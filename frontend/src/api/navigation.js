import {
  getAdminParkingSpaces,
  auditAdminParkingSpace,
  forceOfflineAdminParkingSpace
} from './admin.js'

export const getRoutes = (params = {}) => getAdminParkingSpaces(params)

export const startNavigation = (spaceId) =>
  auditAdminParkingSpace(spaceId, true, '')

export const endNavigation = (spaceId) =>
  forceOfflineAdminParkingSpace(spaceId, '管理员手动下架')

export const getNavigationStatus = (params = {}) => getAdminParkingSpaces(params)
