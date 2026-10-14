import { useEffect, useState, type FormEvent } from 'react';
import { createMenuItem, getMenuItems, updateMenuItem } from '../api/menuItems';
import type { MenuCategory, MenuItem } from '../types';
import { extractErrorMessage } from '../api/client';

const CATEGORIES: MenuCategory[] = ['STARTER', 'MAIN_COURSE', 'DESSERT', 'BEVERAGE', 'SIDE'];

export function MenuPage() {
  const [items, setItems] = useState<MenuItem[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState('');
  const [price, setPrice] = useState(0);
  const [category, setCategory] = useState<MenuCategory>('MAIN_COURSE');
  const [categoryFilter, setCategoryFilter] = useState<string>('');
  const [showAddForm, setShowAddForm] = useState(false);

  async function loadItems() {
    try {
      const page = await getMenuItems({ size: 100, category: categoryFilter || undefined });
      setItems(page.content);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  useEffect(() => {
    loadItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryFilter]);

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      await createMenuItem({ name, price, category });
      setName('');
      setPrice(0);
      setShowAddForm(false);
      await loadItems();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleToggleAvailable(item: MenuItem) {
    setError(null);
    try {
      await updateMenuItem(item.id, {
        name: item.name,
        price: item.price,
        category: item.category,
        available: !item.available,
      });
      await loadItems();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Menu</h1>
        <button onClick={() => setShowAddForm((v) => !v)}>{showAddForm ? 'Cancel' : '+ Add item'}</button>
      </div>
      {error && <p className="form-error">{error}</p>}

      {showAddForm && (
        <form className="inline-form section" onSubmit={handleCreate}>
          <input placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} required />
          <input
            type="number"
            min={0.01}
            step="0.01"
            placeholder="Price"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            required
          />
          <select value={category} onChange={(e) => setCategory(e.target.value as MenuCategory)}>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <button type="submit">Save item</button>
        </form>
      )}

      <div className="section">
        <div className="inline-form" style={{ marginBottom: 16 }}>
          <label className="muted">Filter by category:</label>
          <select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
            <option value="">All</option>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>

        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>Price</th>
              <th>Available</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td>{item.category}</td>
                <td className="order-total">{item.price.toFixed(2)} ₺</td>
                <td>
                  <span className={`pill pill--${item.available ? 'available' : 'cancelled'}`}>
                    {item.available ? 'Available' : 'Unavailable'}
                  </span>
                </td>
                <td>
                  <button className="secondary" onClick={() => handleToggleAvailable(item)}>
                    {item.available ? 'Mark unavailable' : 'Mark available'}
                  </button>
                </td>
              </tr>
            ))}
            {items.length === 0 && (
              <tr>
                <td colSpan={5} className="muted">
                  No menu items yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
