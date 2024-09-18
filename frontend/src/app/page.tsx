import Image from "next/image";
import Link from "next/link"; // Import the Link component from Next.js

export default function Home() {

  return (
    <div style={styles.container}>
      {/* Header Section */}
      <header style={styles.header}>
        <h1>Boxing Tournament</h1>
        <div style={styles.buttons}>
          {/* Link to the login page */}
          <Link href="/login">
            <button style={styles.button}>Login</button>
          </Link>
          {/* Link to the register page */}
          <Link href="/register">
            <button style={styles.button}>Register</button>
          </Link>
        </div>
      </header>

      {/* Main Content */}
      <main>
        <p>We aim to create a fast, fun exciting tournament match making system!</p>
      </main>
    </div>
  );
}

// Custom styles for the layout
const styles = {
  container: {
    padding: '20px',
    fontFamily: 'Arial, sans-serif',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingBottom: '20px',
    borderBottom: '1px solid #ccc',
  },
  buttons: {
    display: 'flex',
    gap: '10px',
  },
  button: {
    backgroundColor: '#0070f3',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    cursor: 'pointer',
    borderRadius: '5px',
    fontSize: '16px',
  },
};
