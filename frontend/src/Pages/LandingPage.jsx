import 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function LandingPage() {
  const navigate = useNavigate();

  const handleButtonClick = () => {
    navigate('/search');
  };

  return (
    <div className="landing-page">
      <h1>Welcome to Lost and Found</h1>
      <p>Your one-stop solution to find lost items</p>
      <button onClick={handleButtonClick}>Find Your Item</button>
    </div>
  );
}

export default LandingPage;