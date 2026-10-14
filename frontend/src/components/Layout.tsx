import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: '📊' },
  { to: '/tables', label: 'Tables', icon: '🍽️' },
  { to: '/menu', label: 'Menu', icon: '📋' },
  { to: '/orders', label: 'Orders', icon: '🧾' },
  { to: '/reservations', label: 'Reservations', icon: '📅' },
];

export function Layout() {
  const { username, logout } = useAuth();

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <div className="app-sidebar__brand">🍴 Restaurant Panel</div>
        <nav className="app-nav">
          {NAV_ITEMS.map((item) => (
            <NavLink key={item.to} to={item.to}>
              <span>{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="app-sidebar__user">
          <span>👤 {username}</span>
          <button onClick={logout}>Log out</button>
        </div>
      </aside>
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  );
}
