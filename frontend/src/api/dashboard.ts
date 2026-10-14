import { apiClient } from './client';
import type { DashboardSummary, HourlyOrderCount, RevenueReport, TopMenuItem } from '../types';

export async function getSummary(): Promise<DashboardSummary> {
  const response = await apiClient.get<DashboardSummary>('/dashboard/summary');
  return response.data;
}

export async function getRevenue(period: 'daily' | 'weekly' | 'monthly'): Promise<RevenueReport> {
  const response = await apiClient.get<RevenueReport>('/dashboard/revenue', { params: { period } });
  return response.data;
}

export async function getTopMenuItems(limit = 5): Promise<TopMenuItem[]> {
  const response = await apiClient.get<TopMenuItem[]>('/dashboard/top-menu-items', { params: { limit } });
  return response.data;
}

export async function getHourlyDistribution(period: 'daily' | 'weekly' | 'monthly'): Promise<HourlyOrderCount[]> {
  const response = await apiClient.get<HourlyOrderCount[]>('/dashboard/hourly-distribution', { params: { period } });
  return response.data;
}
