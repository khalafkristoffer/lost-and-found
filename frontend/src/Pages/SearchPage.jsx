import { useState, useEffect } from 'react';
import { itemService } from '../services/api';
import '../App.css';

function SearchPage() {
  const [searchText, setSearchText] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /*
  useEffect(() => {
    const loadItems = async () => {
      try {
        const allItems = await itemService.getAllItems();
        setItems(allItems);
      } catch (err) {
        console.error('Error loading items:', err);
        setError('Failed to load items');
      }
    };
    
    loadItems();
  }, []);

   */

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      console.log('Submitting search:', searchText); // Debug log
      const searchParams = {
        title: searchText,
        description: searchText,
        location: searchText,
        tags: searchText
      };
      
      const results = await itemService.searchItems(searchParams);
      console.log('Search results:', results); // Debug log
      setItems(results);
    } catch (err) {
      console.error('Search error:', err);
      setError('Failed to search items. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="search-page">
      <h1>Search Lost Items</h1>
      <form onSubmit={handleSubmit} className="search-form">
        <input
          type="text"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          placeholder="Search for items..."
          className="search-input"
        />
        <button type="submit" disabled={loading} className="search-button">
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}

      <div className="results">
        {items.length === 0 ? (
          <p>No items found</p>
        ) : (
          items.map((item) => (
            <div key={item.id} className="item-card">
              <h3>{item.title}</h3>
              {item.description && <p>{item.description}</p>}
              {item.location && <p>Location: {item.location}</p>}
              {item.tags && <p>Tags: {item.tags}</p>}
              {item.image && (
                <img 
                  src={`data:image/jpeg;base64,${item.image}`}
                  alt={item.title}
                  className="item-image"
                />
              )}
            </div>
          ))
        )}
      </div>

      {/* eslint-disable-next-line react/no-unknown-property */}
      <style jsx>{`
        .search-page {
          padding: 20px;
          max-width: 800px;
          margin: 0 auto;
        }
        
        .search-form {
          display: flex;
          gap: 10px;
          margin-bottom: 20px;
        }
        
        .search-input {
          flex: 1;
          padding: 8px;
          font-size: 16px;
          border: 1px solid #ccc;
          border-radius: 4px;
        }
        
        .search-button {
          padding: 8px 16px;
          background-color: #007bff;
          color: white;
          border: none;
          border-radius: 4px;
          cursor: pointer;
        }
        
        .search-button:disabled {
          background-color: #ccc;
        }
        
        .item-card {
          border: 1px solid #ccc;
          border-radius: 8px;
          padding: 15px;
          margin-bottom: 15px;
          background-color: white;
        }
        
        .item-image {
          max-width: 200px;
          margin-top: 10px;
        }
        
        .error-message {
          color: red;
          margin: 10px 0;
        }
      `}</style>
    </div>
  );
}

export default SearchPage;