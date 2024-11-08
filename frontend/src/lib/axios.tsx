import axios from "axios";
import { getToken } from "next-auth/jwt";

const axiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL, // Replace with your actual backend URL
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to add the token
axiosInstance.interceptors.request.use(
  async (config) => {
    const token = await getToken({ req: { cookies: document.cookie } }); // or simply getToken() if this is server-side

    console.log(document.cookie);

    if (token && config.headers) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor (optional)
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    // Optionally handle global errors
    return Promise.reject(error);
  }
);

export default axiosInstance;
