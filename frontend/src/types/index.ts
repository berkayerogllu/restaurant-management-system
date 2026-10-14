// These types mirror the backend DTOs exactly (field names, nullability, string enums).
// Keep them in sync with src/main/java/com/berkay/restaurant_system/dtos/*.java.

export type OrderStatus =
  | 'RECEIVED'
  | 'IN_PREPARATION'
  | 'READY'
  | 'SERVED'
  | 'PAID'
  | 'CANCELLED';

export type TableStatus = 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' | 'CLEANING';

export type MenuCategory = 'STARTER' | 'MAIN_COURSE' | 'DESSERT' | 'BEVERAGE' | 'SIDE';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface ApiErrorResponse {
  timestamp: string;
  message: string;
  path: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
}

export interface StaffCreateRequest {
  username: string;
  password: string;
}

export interface Staff {
  id: number;
  username: string;
}

export interface RestaurantTable {
  id: number;
  tableNumber: string;
  capacity: number;
  status: TableStatus;
}

export interface RestaurantTableRequest {
  id?: number;
  tableNumber: string;
  capacity: number;
}

export interface TableOccupancyLog {
  id: number;
  tableId: number;
  occupiedAt: string;
  freedAt: string | null;
}

export interface MenuItem {
  id: number;
  name: string;
  price: number;
  category: MenuCategory;
  available: boolean;
}

export interface MenuItemRequest {
  id?: number;
  name: string;
  price: number;
  category: MenuCategory;
  available?: boolean;
}

export interface OrderItem {
  id?: number;
  menuItemId: number;
  menuItemName?: string;
  quantity: number;
  unitPrice?: number;
  note?: string | null;
}

export interface RestaurantOrder {
  id: number;
  details: string;
  totalPrice: number;
  status: OrderStatus;
  cancellationReason: string | null;
  customerPhone: string | null;
  tableId: number | null;
  items: OrderItem[];
}

export interface RestaurantOrderRequest {
  details: string;
  tableId: number;
  items: { menuItemId: number; quantity: number; note?: string }[];
}

export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  tableId: number | null;
}

export interface CustomerRequest {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  tableId?: number;
}

export interface Reservation {
  id: number;
  customerName: string;
  customerPhone: string;
  startTime: string;
  endTime: string;
  tableId: number;
  checkedIn: boolean;
}

export interface DashboardSummary {
  activeOrders: number;
  kitchenQueueSize: number;
  totalTables: number;
  availableTables: number;
  occupiedTables: number;
  averageOccupancyMinutes: number | null;
}

export interface RevenueReport {
  from: string;
  to: string;
  totalRevenue: number;
  paidOrderCount: number;
  averageOrderValue: number;
}

export interface TopMenuItem {
  menuItemId: number;
  name: string;
  quantitySold: number;
  revenue: number;
}

export interface HourlyOrderCount {
  hour: number;
  orderCount: number;
}
