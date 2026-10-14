import { apiClient } from './client';
import type { LoginRequest, LoginResponse, Staff, StaffCreateRequest } from '../types';

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await apiClient.post<LoginResponse>('/auth/login', request);
  return response.data;
}

export async function registerStaff(request: StaffCreateRequest): Promise<Staff> {
  const response = await apiClient.post<Staff>('/auth/register', request);
  return response.data;
}
