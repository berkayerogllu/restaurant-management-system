import { apiClient } from './client';
import type { Customer, Page, Reservation } from '../types';

export async function getReservations(params?: { page?: number; size?: number }): Promise<Page<Reservation>> {
  const response = await apiClient.get<Page<Reservation>>('/reservations', { params });
  return response.data;
}

export async function getTodaysReservations(): Promise<Page<Reservation>> {
  const response = await apiClient.get<Page<Reservation>>('/reservations/today');
  return response.data;
}

export async function getNoShowReservations(): Promise<Page<Reservation>> {
  const response = await apiClient.get<Page<Reservation>>('/reservations/no-show');
  return response.data;
}

export async function createReservation(request: {
  customerName: string;
  customerPhone: string;
  startTime: string;
  endTime: string;
  tableId: number;
}): Promise<Reservation> {
  const response = await apiClient.post<Reservation>('/reservations', request);
  return response.data;
}

export async function checkInReservation(id: number): Promise<Customer> {
  const response = await apiClient.post<Customer>(`/reservations/${id}/check-in`);
  return response.data;
}
