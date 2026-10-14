export function StatusBadge({ status }: { status: string }) {
  return <span className={`pill pill--${status.toLowerCase()}`}>{status.replace('_', ' ')}</span>;
}
