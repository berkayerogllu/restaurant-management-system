import { apiClient } from './client';
import type { Page, RestaurantTable, RestaurantTableRequest, RestaurantOrder, TableOccupancyLog } from '../types';

export async function getTables(params?: { status?: string; page?: number; size?: number }): Promise<Page<RestaurantTable>> {
  const response = await apiClient.get<Page<RestaurantTable>>('/tables', { params });
  return response.data;
}

export async function createTable(request: RestaurantTableRequest): Promise<RestaurantTable> {
  const response = await apiClient.post<RestaurantTable>('/tables', request);
  return response.data;
}

export async function updateTable(id: number, request: RestaurantTableRequest): Promise<RestaurantTable> {
  const response = await apiClient.put<RestaurantTable>(`/tables/${id}`, request);
  return response.data;
}

export async function updateTableStatus(id: number, status: string): Promise<RestaurantTable> {
  const response = await apiClient.patch<RestaurantTable>(`/tables/${id}/status`, { status });
  return response.data;
}

export async function deleteTable(id: number): Promise<void> {
  await apiClient.delete(`/tables/${id}`);
}

export async function getActiveOrderForTable(id: number): Promise<RestaurantOrder | null> {
  try {
    const response = await apiClient.get<RestaurantOrder>(`/tables/${id}/active-order`);
    return response.data;
  } catch (error: unknown) {
    const axiosError = error as { response?: { status?: number } };
    if (axiosError.response?.status === 404) {
      return null;
    }
    throw error;
  }
}

export async function getOccupancyHistory(id: number): Promise<Page<TableOccupancyLog>> {
  const response = await apiClient.get<Page<TableOccupancyLog>>(`/tables/${id}/occupancy-history`);
  return response.data;
}
