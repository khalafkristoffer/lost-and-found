import 'react';
import { useHistory } from 'react-router-dom';
import './index.css';

function LandingPage() {
  const history = useHistory();

  const handleButtonClick = () => {
    history.push('/search');
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