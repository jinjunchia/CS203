// File: app/login/page.tsx (for Next.js 13+ app directory setup)
export default function Login() {
  const styles = {
    container: {
      display: 'flex',
      flexDirection: 'column' as const, // Specify 'column' as a constant to prevent TypeScript errors
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
    },
    inputContainer: {
      display: 'flex',
      flexDirection: 'column' as const,
      gap: '10px',
      marginBottom: '20px',
    },
    input: {
      padding: '10px',
      fontSize: '16px',
      width: '250px',
      borderRadius: '5px',
      border: '1px solid #ccc',
    },
    button: {
      padding: '10px 20px',
      fontSize: '16px',
      cursor: 'pointer',
      borderRadius: '5px',
      border: 'none',
      backgroundColor: '#0070f3',
      color: '#fff',
    },
  };

  return (
    <div style={styles.container}>
      <h1>Login Page</h1>
      <div style={styles.inputContainer}>
      <input type="text" placeholder="Username" id="username" name="username" style={styles.input} required />
      <input type="password" placeholder="Password" id="password" name="password" style={styles.input} required />
      </div>
      <button style={styles.button}>Enter</button>
    </div>
  );
}
