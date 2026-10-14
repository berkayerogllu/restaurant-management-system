import { apiClient } from './client';
import type { Customer, CustomerRequest, Page } from '../types';

export async function getCustomers(params?: { page?: number; size?: number }): Promise<Page<Customer>> {
  const response = await apiClient.get<Page<Customer>>('/customers', { params });
  return response.data;
}

export async function createCustomer(request: CustomerRequest): Promise<Customer> {
  const response = await apiClient.post<Customer>('/customers', request);
  return response.data;
}

export async function deleteCustomer(id: number): Promise<void> {
  await apiClient.delete(`/customers/${id}`);
}
