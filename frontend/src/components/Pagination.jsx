export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const hasPrev = page > 0;
  const hasNext = page < totalPages - 1;

  const maxVisible = 5;
  let start = Math.max(0, page - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages, start + maxVisible);
  if (end - start < maxVisible) {
    start = Math.max(0, end - maxVisible);
  }

  const pages = [];
  for (let i = start; i < end; i++) {
    pages.push(i);
  }

  return (
    <nav aria-label="Page navigation" className="mt-3">
      <ul className="pagination justify-content-center mb-0">
        <li className={`page-item ${hasPrev ? "" : "disabled"}`}>
          <button className="page-link" onClick={() => onPageChange(0)} disabled={!hasPrev}>
            &laquo;
          </button>
        </li>
        <li className={`page-item ${hasPrev ? "" : "disabled"}`}>
          <button className="page-link" onClick={() => onPageChange(page - 1)} disabled={!hasPrev}>
            &lsaquo;
          </button>
        </li>
        {pages.map((p) => (
          <li key={p} className={`page-item ${p === page ? "active" : ""}`}>
            <button className="page-link" onClick={() => onPageChange(p)}>
              {p + 1}
            </button>
          </li>
        ))}
        <li className={`page-item ${hasNext ? "" : "disabled"}`}>
          <button className="page-link" onClick={() => onPageChange(page + 1)} disabled={!hasNext}>
            &rsaquo;
          </button>
        </li>
        <li className={`page-item ${hasNext ? "" : "disabled"}`}>
          <button className="page-link" onClick={() => onPageChange(totalPages - 1)} disabled={!hasNext}>
            &raquo;
          </button>
        </li>
      </ul>
    </nav>
  );
}
