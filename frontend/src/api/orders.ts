import { apiClient } from './client';
import type { Page, RestaurantOrder, RestaurantOrderRequest } from '../types';

export async function getOrders(params?: { page?: number; size?: number }): Promise<Page<RestaurantOrder>> {
  const response = await apiClient.get<Page<RestaurantOrder>>('/orders', { params });
  return response.data;
}

export async function getKitchenQueue(): Promise<RestaurantOrder[]> {
  const response = await apiClient.get<RestaurantOrder[]>('/orders/kitchen-queue');
  return response.data;
}

export async function createOrder(request: RestaurantOrderRequest): Promise<RestaurantOrder> {
  const response = await apiClient.post<RestaurantOrder>('/orders', request);
  return response.data;
}

export async function updateOrderStatus(id: number, status: string, reason?: string): Promise<RestaurantOrder> {
  const response = await apiClient.patch<RestaurantOrder>(`/orders/${id}/status`, { status, reason });
  return response.data;
}

export async function getOrderHistoryByPhone(phoneNumber: string): Promise<Page<RestaurantOrder>> {
  const response = await apiClient.get<Page<RestaurantOrder>>('/orders/history', { params: { phoneNumber } });
  return response.data;
}
