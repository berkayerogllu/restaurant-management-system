import { apiClient } from './client';
import type { MenuItem, MenuItemRequest, Page } from '../types';

export async function getMenuItems(params?: {
  category?: string;
  available?: boolean;
  page?: number;
  size?: number;
}): Promise<Page<MenuItem>> {
  const response = await apiClient.get<Page<MenuItem>>('/menu-items', { params });
  return response.data;
}

export async function createMenuItem(request: MenuItemRequest): Promise<MenuItem> {
  const response = await apiClient.post<MenuItem>('/menu-items', request);
  return response.data;
}

export async function updateMenuItem(id: number, request: MenuItemRequest): Promise<MenuItem> {
  const response = await apiClient.put<MenuItem>(`/menu-items/${id}`, request);
  return response.data;
}

export async function deleteMenuItem(id: number): Promise<void> {
  await apiClient.delete(`/menu-items/${id}`);
}
