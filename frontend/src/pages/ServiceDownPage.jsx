import { Link } from 'react-router-dom';

export function ServiceDownPage() {
  return (
    <div className="service-down">
      <div className="service-down__card">
        <p className="service-down__eyebrow">Service status</p>
        <h1 className="service-down__title">The server is taking a break.</h1>
        <p className="service-down__text">
          Your device and connection look fine. The API is not responding right now, so we
          cannot load data. Please try again in a minute.
        </p>
        <div className="service-down__actions">
          <button
            className="btn btn--primary"
            type="button"
            onClick={() => window.location.reload()}
          >
            Retry
          </button>
          <Link className="btn btn--ghost" to="/">
            Back to home
          </Link>
        </div>
      </div>
    </div>
  );
}
