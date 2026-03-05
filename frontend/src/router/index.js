import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import OrderManagement from '../views/OrderManagement.vue'
import NavigationMap from '../views/NavigationMap.vue'
import BillingManagement from '../views/BillingManagement.vue'
import UserManagement from '../views/UserManagement.vue'
import PlatformRevenue from '../views/PlatformRevenue.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/orders',
    name: 'OrderManagement',
    component: OrderManagement
  },
  {
    path: '/navigation',
    name: 'NavigationMap',
    component: NavigationMap
  },
  {
    path: '/billing',
    name: 'BillingManagement',
    component: BillingManagement
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: UserManagement
  },
  {
    path: '/revenue',
    name: 'PlatformRevenue',
    component: PlatformRevenue
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router