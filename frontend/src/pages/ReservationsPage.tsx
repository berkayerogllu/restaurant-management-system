import { useEffect, useState, type FormEvent } from 'react';
import { checkInReservation, createReservation, getReservations, getTodaysReservations } from '../api/reservations';
import { getTables } from '../api/tables';
import type { Reservation, RestaurantTable } from '../types';
import { extractErrorMessage } from '../api/client';

function formatDateTime(value: string): string {
  return new Date(value).toLocaleString(undefined, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function ReservationsPage() {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [tables, setTables] = useState<RestaurantTable[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [showTodayOnly, setShowTodayOnly] = useState(false);
  const [showForm, setShowForm] = useState(false);

  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [tableId, setTableId] = useState<number | ''>('');

  async function loadReservations() {
    try {
      const page = showTodayOnly ? await getTodaysReservations() : await getReservations({ size: 50 });
      setReservations(page.content);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  useEffect(() => {
    getTables({ size: 100 }).then((page) => setTables(page.content));
  }, []);

  useEffect(() => {
    loadReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showTodayOnly]);

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    setError(null);
    if (tableId === '') {
      setError('Select a table.');
      return;
    }
    try {
      await createReservation({ customerName, customerPhone, startTime, endTime, tableId });
      setCustomerName('');
      setCustomerPhone('');
      setStartTime('');
      setEndTime('');
      setTableId('');
      setShowForm(false);
      await loadReservations();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleCheckIn(id: number) {
    setError(null);
    try {
      await checkInReservation(id);
      await loadReservations();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Reservations</h1>
        <button onClick={() => setShowForm((v) => !v)}>{showForm ? 'Cancel' : '+ New reservation'}</button>
      </div>
      {error && <p className="form-error">{error}</p>}

      {showForm && (
        <form className="inline-form section" onSubmit={handleCreate}>
          <input placeholder="Customer name" value={customerName} onChange={(e) => setCustomerName(e.target.value)} required />
          <input placeholder="Phone" value={customerPhone} onChange={(e) => setCustomerPhone(e.target.value)} required />
          <input type="datetime-local" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />
          <input type="datetime-local" value={endTime} onChange={(e) => setEndTime(e.target.value)} required />
          <select value={tableId} onChange={(e) => setTableId(e.target.value === '' ? '' : Number(e.target.value))}>
            <option value="">Select table</option>
            {tables.map((t) => (
              <option key={t.id} value={t.id}>
                {t.tableNumber}
              </option>
            ))}
          </select>
          <button type="submit">Save reservation</button>
        </form>
      )}

      <div className="section">
        <label className="muted" style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 16 }}>
          <input type="checkbox" checked={showTodayOnly} onChange={(e) => setShowTodayOnly(e.target.checked)} />
          Show today only
        </label>

        <table className="data-table">
          <thead>
            <tr>
              <th>Customer</th>
              <th>Phone</th>
              <th>Start</th>
              <th>End</th>
              <th>Table</th>
              <th>Checked in</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservations.map((r) => (
              <tr key={r.id}>
                <td>{r.customerName}</td>
                <td>{r.customerPhone}</td>
                <td>{formatDateTime(r.startTime)}</td>
                <td>{formatDateTime(r.endTime)}</td>
                <td>{tables.find((t) => t.id === r.tableId)?.tableNumber ?? r.tableId}</td>
                <td>
                  <span className={`pill pill--${r.checkedIn ? 'available' : 'cleaning'}`}>
                    {r.checkedIn ? 'Yes' : 'No'}
                  </span>
                </td>
                <td>{!r.checkedIn && <button onClick={() => handleCheckIn(r.id)}>Check in</button>}</td>
              </tr>
            ))}
            {reservations.length === 0 && (
              <tr>
                <td colSpan={7} className="muted">
                  No reservations.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
