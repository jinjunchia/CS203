import Image from "next/image";
import Link from "next/link"; // Import the Link component from Next.js


export default function Login() {
    return (
      <div style={styles.container}>
        <div style={styles.formContainer}>
          <h1 style={styles.heading}>Login Page</h1>
          <form style={styles.form}>
            <div style={styles.inputContainer}>
              <label htmlFor="username" style={styles.label}>Username</label>
              <input
                type="text"
                id="username"
                name="username"
                placeholder="Enter username"
                style={styles.input}
              />
            </div>
            <div style={styles.inputContainer}>
              <label htmlFor="password" style={styles.label}>Password</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter password"
                style={styles.input}
              />
            </div>
            <button type="submit" style={styles.button}>Enter</button>
          </form>
        </div>
      </div>
    );
  }
  
  // Custom styles for the layout
  const styles = {
    container: {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh', // Full viewport height
      backgroundColor: '#f0f0f0',
      fontFamily: 'Arial, sans-serif',
    },
    formContainer: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      background: '#fff',
      padding: '20px',
      borderRadius: '8px',
      boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
    },
    heading: {
      marginBottom: '20px',
    },
    form: {
      display: 'flex',
      flexDirection: 'row',
      alignItems: 'center',
      gap: '10px',
    },
    inputContainer: {
      display: 'flex',
      flexDirection: 'column',
      margin: '0 10px',
    },
    label: {
      marginBottom: '5px',
    },
    input: {
      padding: '8px',
      fontSize: '16px',
      borderRadius: '4px',
      border: '1px solid #ddd',
    },
    button: {
      backgroundColor: '#0070f3',
      color: '#fff',
      border: 'none',
      padding: '10px 20px',
      cursor: 'pointer',
      borderRadius: '4px',
      fontSize: '16px',
    },
  };
  