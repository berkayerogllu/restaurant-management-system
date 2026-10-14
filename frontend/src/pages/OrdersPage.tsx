import { useEffect, useState, type FormEvent } from 'react';
import { createOrder, getKitchenQueue, getOrders, updateOrderStatus } from '../api/orders';
import { getMenuItems } from '../api/menuItems';
import { getTables } from '../api/tables';
import type { MenuItem, OrderStatus, RestaurantOrder, RestaurantTable } from '../types';
import { extractErrorMessage } from '../api/client';
import { StatusBadge } from '../components/StatusBadge';

const NEXT_STATUSES: Record<OrderStatus, OrderStatus[]> = {
  RECEIVED: ['IN_PREPARATION', 'CANCELLED'],
  IN_PREPARATION: ['READY', 'CANCELLED'],
  READY: ['SERVED', 'CANCELLED'],
  SERVED: ['PAID'],
  PAID: [],
  CANCELLED: [],
};

interface DraftItem {
  menuItemId: number;
  quantity: number;
}

export function OrdersPage() {
  const [orders, setOrders] = useState<RestaurantOrder[]>([]);
  const [kitchenQueue, setKitchenQueue] = useState<RestaurantOrder[]>([]);
  const [tables, setTables] = useState<RestaurantTable[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);

  const [tableId, setTableId] = useState<number | ''>('');
  const [details, setDetails] = useState('');
  const [draftItems, setDraftItems] = useState<DraftItem[]>([]);

  async function loadAll() {
    try {
      const [ordersPage, queue, tablesPage, menuPage] = await Promise.all([
        getOrders({ size: 50 }),
        getKitchenQueue(),
        getTables({ size: 100 }),
        getMenuItems({ size: 200, available: true }),
      ]);
      setOrders(ordersPage.content);
      setKitchenQueue(queue);
      setTables(tablesPage.content);
      setMenuItems(menuPage.content);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  useEffect(() => {
    loadAll();
  }, []);

  function addDraftItem() {
    if (menuItems.length === 0) return;
    setDraftItems((prev) => [...prev, { menuItemId: menuItems[0].id, quantity: 1 }]);
  }

  function updateDraftItem(index: number, field: keyof DraftItem, value: number) {
    setDraftItems((prev) => prev.map((item, i) => (i === index ? { ...item, [field]: value } : item)));
  }

  function removeDraftItem(index: number) {
    setDraftItems((prev) => prev.filter((_, i) => i !== index));
  }

  async function handleCreateOrder(event: FormEvent) {
    event.preventDefault();
    setError(null);
    if (tableId === '' || draftItems.length === 0) {
      setError('Select a table and at least one item.');
      return;
    }
    try {
      await createOrder({
        details: details || 'Order',
        tableId,
        items: draftItems,
      });
      setDetails('');
      setDraftItems([]);
      setTableId('');
      setShowForm(false);
      await loadAll();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleStatusChange(order: RestaurantOrder, status: OrderStatus) {
    setError(null);
    let reason: string | undefined;
    if (status === 'CANCELLED') {
      reason = window.prompt('Cancellation reason:') ?? undefined;
      if (!reason) return;
    }
    try {
      await updateOrderStatus(order.id, status, reason);
      await loadAll();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  function renderStatusActions(order: RestaurantOrder) {
    return (
      <div className="action-row">
        {NEXT_STATUSES[order.status].map((next) => (
          <button
            key={next}
            className={next === 'CANCELLED' ? 'danger' : 'secondary'}
            onClick={() => handleStatusChange(order, next)}
          >
            {next.replace('_', ' ')}
          </button>
        ))}
        {NEXT_STATUSES[order.status].length === 0 && <span className="muted">—</span>}
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <h1>Orders</h1>
        <button onClick={() => setShowForm((v) => !v)}>{showForm ? 'Cancel' : '+ New order'}</button>
      </div>
      {error && <p className="form-error">{error}</p>}

      {showForm && (
        <form className="order-form section" onSubmit={handleCreateOrder}>
          <div className="inline-form">
            <select value={tableId} onChange={(e) => setTableId(e.target.value === '' ? '' : Number(e.target.value))}>
              <option value="">Select table</option>
              {tables.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.tableNumber}
                </option>
              ))}
            </select>
            <input placeholder="Details (optional)" value={details} onChange={(e) => setDetails(e.target.value)} />
          </div>

          {draftItems.map((item, index) => (
            <div key={index} className="inline-form">
              <select
                value={item.menuItemId}
                onChange={(e) => updateDraftItem(index, 'menuItemId', Number(e.target.value))}
              >
                {menuItems.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.name} ({m.price.toFixed(2)} ₺)
                  </option>
                ))}
              </select>
              <input
                type="number"
                min={1}
                value={item.quantity}
                onChange={(e) => updateDraftItem(index, 'quantity', Number(e.target.value))}
                style={{ width: 70 }}
              />
              <button type="button" className="secondary" onClick={() => removeDraftItem(index)}>
                Remove
              </button>
            </div>
          ))}
          <div className="inline-form">
            <button type="button" className="secondary" onClick={addDraftItem}>
              + Add item
            </button>
            <button type="submit">Place order</button>
          </div>
        </form>
      )}

      <div className="section">
        <h2>👨‍🍳 Kitchen queue</h2>
        <table className="data-table">
          <thead>
            <tr>
              <th>Order</th>
              <th>Table</th>
              <th>Items</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {kitchenQueue.map((order) => (
              <tr key={order.id}>
                <td>#{order.id}</td>
                <td>{tables.find((t) => t.id === order.tableId)?.tableNumber ?? order.tableId}</td>
                <td>{order.items.map((i) => `${i.quantity}x ${i.menuItemName}`).join(', ')}</td>
                <td>
                  <StatusBadge status={order.status} />
                </td>
                <td>{renderStatusActions(order)}</td>
              </tr>
            ))}
            {kitchenQueue.length === 0 && (
              <tr>
                <td colSpan={5} className="muted">
                  Kitchen queue is empty.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="section">
        <h2>All orders</h2>
        <table className="data-table">
          <thead>
            <tr>
              <th>Order</th>
              <th>Table</th>
              <th>Total</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>#{order.id}</td>
                <td>{tables.find((t) => t.id === order.tableId)?.tableNumber ?? order.tableId}</td>
                <td className="order-total">{order.totalPrice.toFixed(2)} ₺</td>
                <td>
                  <StatusBadge status={order.status} />
                  {order.cancellationReason && <div className="muted">{order.cancellationReason}</div>}
                </td>
                <td>{renderStatusActions(order)}</td>
              </tr>
            ))}
            {orders.length === 0 && (
              <tr>
                <td colSpan={5} className="muted">
                  No orders yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
