import Link from "next/link";

export default function Register() {
  return (
    <div style={styles.container}>
      <div style={styles.buttons}>
        {/* Link to the login page */}
        <Link href="/register/admin">
          <button style={styles.button}>Creating as an Admin</button>
        </Link>
        {/* Link to the register page */}
        <Link href="/register/player">
          <button style={styles.button}>Creating as a Player</button>
        </Link>
      </div>
      <div>
        <nav>
          {" "}
          Already have an account?
          <Link href="/login">
            <span style={styles.clickhere}>Click here!</span>
          </Link>
        </nav>
      </div>
    </div>
  );
}

const styles = {
  container: {
    padding: "20px",
    fontFamily: "Arial, sans-serif",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    height: "100vh",
    flexDirection: "column",
  },
  buttons: {
    display: "flex",
    gap: "10px",
    paddingBottom: "20px",
  },
  button: {
    backgroundColor: "#0070f3",
    color: "#fff",
    border: "none",
    padding: "10px 20px",
    cursor: "pointer",
    borderRadius: "5px",
    fontSize: "16px",
  },
  clickhere: {
    color: "blue",
    textDecoration: "underline",
    cursor: "pointer",
    paddingLeft: "10px",
  },
};
