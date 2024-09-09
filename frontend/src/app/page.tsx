// src/app/page.tsx or pages/index.tsx
'use client';

import React from 'react';

const Home: React.FC = () => {
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    const form = event.currentTarget;
    const formData = new FormData(form);

    const username = formData.get('username') as string;
    const password = formData.get('password') as string;

    console.log('Username:', username);
    console.log('Password:', password);

    // Add form submission logic here (e.g., send data to a server)
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', padding: '20px' }}>
      <h1 style={{ marginBottom: '20px' }}>Welcome to Tournament</h1>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', width: '300px' }}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="username" style={{ display: 'block', marginBottom: '5px' }}>Login ID:</label>
          <input
            id="username"
            name="username"
            type="text"
            required
            style={{ display: 'block', width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="password" style={{ display: 'block', marginBottom: '5px' }}>Password:</label>
          <input
            id="password"
            name="password"
            type="password"
            required
            style={{ display: 'block', width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        <button
          type="submit"
          style={{ padding: '10px', backgroundColor: '#0070f3', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
        >
          Login
        </button>
      </form>
    </div>
  );
};

export default Home;
