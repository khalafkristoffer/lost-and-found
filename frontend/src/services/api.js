import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const itemService = {
    searchItems: async (searchParams) => {
        const params = new URLSearchParams();
        if (searchParams.title) params.append('title', searchParams.title);
        if (searchParams.description) params.append('description', searchParams.description);
        if (searchParams.location) params.append('location', searchParams.location);
        if (searchParams.tags) params.append('tags', searchParams.tags);
        if (searchParams.type) params.append('type', searchParams.type);

        const response = await api.get(`/items/search?${params.toString()}`);
        return response.data;
    },

    uploadItem: async (formData) => {
        try {
            const response = await api.post('/items/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                }
            });
            return response.data;
        } catch (error) {
            console.error('Upload error:', error);
            throw error;
        }
    },

    getAllItems: async () => {
        try {
            const response = await api.get('/items/all');
            console.log('All items:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching all items:', error);
            throw error;
        }
    },

    getItemById: async (id) => {
        const response = await api.get(`/items/${id}`);
        return response.data;
    },
};