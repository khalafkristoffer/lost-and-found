import 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function LandingPage() {
  const navigate = useNavigate();

  const handleSearchClick = () => {
    navigate('/search');
  };

  const handleUploadClick = () => {
    navigate('/upload');
  };

  return (
    <div className="landing-page">
      <h1>Welcome to Lost and Found</h1>
      <p>Your one-stop solution to find lost items</p>
      <div className="button-container">
        <button onClick={handleSearchClick}>Find Your Item</button>
        <button onClick={handleUploadClick}>Upload Lost Item</button>
      </div>
    </div>
  );
}

export default LandingPage;