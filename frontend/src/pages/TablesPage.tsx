import { useEffect, useState, type FormEvent } from 'react';
import { createTable, getActiveOrderForTable, getTables, updateTableStatus } from '../api/tables';
import type { RestaurantOrder, RestaurantTable, TableStatus } from '../types';
import { extractErrorMessage } from '../api/client';
import { StatusBadge } from '../components/StatusBadge';

const ALL_STATUSES: TableStatus[] = ['AVAILABLE', 'OCCUPIED', 'RESERVED', 'CLEANING'];

export function TablesPage() {
  const [tables, setTables] = useState<RestaurantTable[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [tableNumber, setTableNumber] = useState('');
  const [capacity, setCapacity] = useState(2);
  const [activeOrderByTable, setActiveOrderByTable] = useState<Record<number, RestaurantOrder | null>>({});
  const [showAddForm, setShowAddForm] = useState(false);

  async function loadTables() {
    try {
      const page = await getTables({ size: 100 });
      setTables(page.content);

      const occupied = page.content.filter((t) => t.status === 'OCCUPIED');
      const entries = await Promise.all(
        occupied.map(async (t) => {
          try {
            const order = await getActiveOrderForTable(t.id);
            return [t.id, order] as const;
          } catch {
            return [t.id, null] as const;
          }
        })
      );
      setActiveOrderByTable((prev) => ({ ...prev, ...Object.fromEntries(entries) }));
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  useEffect(() => {
    loadTables();
  }, []);

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      await createTable({ tableNumber, capacity });
      setTableNumber('');
      setCapacity(2);
      setShowAddForm(false);
      await loadTables();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleStatusChange(id: number, status: string) {
    setError(null);
    try {
      await updateTableStatus(id, status);
      await loadTables();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  const summary = {
    available: tables.filter((t) => t.status === 'AVAILABLE').length,
    occupied: tables.filter((t) => t.status === 'OCCUPIED').length,
    reserved: tables.filter((t) => t.status === 'RESERVED').length,
    cleaning: tables.filter((t) => t.status === 'CLEANING').length,
  };

  return (
    <div>
      <div className="page-header">
        <h1>Tables</h1>
        <button onClick={() => setShowAddForm((v) => !v)}>{showAddForm ? 'Cancel' : '+ Add table'}</button>
      </div>
      {error && <p className="form-error">{error}</p>}

      <div className="card-grid">
        <div className="card">
          <div className="card__value">{summary.available}</div>
          <div className="card__label">🟢 Available</div>
        </div>
        <div className="card">
          <div className="card__value">{summary.occupied}</div>
          <div className="card__label">🔴 Occupied</div>
        </div>
        <div className="card">
          <div className="card__value">{summary.reserved}</div>
          <div className="card__label">🟣 Reserved</div>
        </div>
        <div className="card">
          <div className="card__value">{summary.cleaning}</div>
          <div className="card__label">🔵 Cleaning</div>
        </div>
      </div>

      {showAddForm && (
        <form className="inline-form section" onSubmit={handleCreate}>
          <input
            placeholder="Table number"
            value={tableNumber}
            onChange={(e) => setTableNumber(e.target.value)}
            required
          />
          <input
            type="number"
            min={1}
            placeholder="Capacity"
            value={capacity}
            onChange={(e) => setCapacity(Number(e.target.value))}
            required
          />
          <button type="submit">Save table</button>
        </form>
      )}

      <div className="table-grid">
        {tables.map((table) => {
          const order = activeOrderByTable[table.id];
          return (
            <div key={table.id} className={`table-card table-card--${table.status.toLowerCase()}`}>
              <div className="table-card__top">
                <div>
                  <div className="table-card__number">{table.tableNumber}</div>
                  <div className="table-card__capacity">👥 {table.capacity} seats</div>
                </div>
                <StatusBadge status={table.status} />
              </div>

              {table.status === 'OCCUPIED' && order && (
                <div className="table-card__order">
                  <div className="table-card__order-total">{order.totalPrice.toFixed(2)} ₺</div>
                  <div className="table-card__order-meta">
                    Order #{order.id} · {order.items.length} item{order.items.length === 1 ? '' : 's'} ·{' '}
                    {order.status}
                  </div>
                </div>
              )}
              {table.status === 'OCCUPIED' && order === null && (
                <div className="table-card__empty">No active order</div>
              )}
              {table.status !== 'OCCUPIED' && <div className="table-card__empty">&nbsp;</div>}

              <select value={table.status} onChange={(e) => handleStatusChange(table.id, e.target.value)}>
                {ALL_STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>
          );
        })}
        {tables.length === 0 && <p className="muted">No tables yet. Add one to get started.</p>}
      </div>
    </div>
  );
}
