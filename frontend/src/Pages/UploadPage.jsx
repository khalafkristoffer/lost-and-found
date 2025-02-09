import { useState } from 'react';
import { itemService } from '../services/api';
import '../App.css';

function UploadPage() {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    location: '',
    file: null
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData();
    data.append('title', formData.title);
    data.append('description', formData.description);
    data.append('location', formData.location);
    data.append('file', formData.file);

    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      await itemService.uploadItem(data);
      setSuccess(true);
      setFormData({
        title: '',
        description: '',
        location: '',
        file: null
      });
      // Reset the form
      e.target.reset();
    } catch (err) {
      console.error('Upload error details:', err);
      setError(err.response?.data?.message || err.message || 'Failed to upload item. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-page">
      <h1>Upload Lost Item</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title:</label>
          <input
            id="title"
            type="text"
            placeholder="Title"
            value={formData.title}
            onChange={(e) => setFormData({...formData, title: e.target.value})}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="description">Description:</label>
          <textarea
            id="description"
            placeholder="Description"
            value={formData.description}
            onChange={(e) => setFormData({...formData, description: e.target.value})}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="location">Location:</label>
          <input
            id="location"
            type="text"
            placeholder="Location"
            value={formData.location}
            onChange={(e) => setFormData({...formData, location: e.target.value})}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="file">Image:</label>
          <input
            id="file"
            type="file"
            accept="image/*"
            onChange={(e) => {
              const selectedFile = e.target.files[0];
              console.log('Selected file:', selectedFile);
              setFormData({...formData, file: selectedFile});
            }}
            required
          />
        </div>

        <button type="submit" disabled={loading || !formData.file}>
          {loading ? 'Uploading...' : 'Upload'}
        </button>
      </form>

      {error && (
        <div className="error-message">
          <p>Error: {error}</p>
        </div>
      )}
      {success && (
        <div className="success-message">
          <p>Item uploaded successfully!</p>
        </div>
      )}
    </div>
  );
}

export default UploadPage; 