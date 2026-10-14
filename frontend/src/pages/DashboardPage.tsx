import { useEffect, useState } from 'react';
import { getRevenue, getSummary, getTopMenuItems } from '../api/dashboard';
import type { DashboardSummary, RevenueReport, TopMenuItem } from '../types';
import { extractErrorMessage } from '../api/client';

export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [revenue, setRevenue] = useState<RevenueReport | null>(null);
  const [topItems, setTopItems] = useState<TopMenuItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([getSummary(), getRevenue('daily'), getTopMenuItems(5)])
      .then(([summaryData, revenueData, topItemsData]) => {
        setSummary(summaryData);
        setRevenue(revenueData);
        setTopItems(topItemsData);
      })
      .catch((err) => setError(extractErrorMessage(err)));
  }, []);

  if (error) return <p className="form-error">{error}</p>;
  if (!summary || !revenue) return <p className="muted">Loading...</p>;

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
      </div>

      <div className="card-grid">
        <div className="card">
          <div className="card__value">{summary.activeOrders}</div>
          <div className="card__label">🧾 Active orders</div>
        </div>
        <div className="card">
          <div className="card__value">{summary.kitchenQueueSize}</div>
          <div className="card__label">👨‍🍳 Kitchen queue</div>
        </div>
        <div className="card">
          <div className="card__value">
            {summary.availableTables} / {summary.totalTables}
          </div>
          <div className="card__label">🟢 Available tables</div>
        </div>
        <div className="card">
          <div className="card__value">{summary.occupiedTables}</div>
          <div className="card__label">🔴 Occupied tables</div>
        </div>
        <div className="card">
          <div className="card__value">
            {summary.averageOccupancyMinutes !== null ? `${summary.averageOccupancyMinutes.toFixed(0)} min` : '—'}
          </div>
          <div className="card__label">⏱️ Avg. occupancy</div>
        </div>
        <div className="card">
          <div className="card__value">{revenue.totalRevenue.toFixed(2)} ₺</div>
          <div className="card__label">💰 Today's revenue ({revenue.paidOrderCount} orders)</div>
        </div>
      </div>

      <div className="section">
        <h2>Top selling items</h2>
        <table className="data-table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Quantity sold</th>
              <th>Revenue</th>
            </tr>
          </thead>
          <tbody>
            {topItems.map((item) => (
              <tr key={item.menuItemId}>
                <td>{item.name}</td>
                <td>{item.quantitySold}</td>
                <td className="order-total">{item.revenue.toFixed(2)} ₺</td>
              </tr>
            ))}
            {topItems.length === 0 && (
              <tr>
                <td colSpan={3} className="muted">
                  No sales yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
