import React, { useState } from 'react';
import './App.css';

function App() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otp, setOtp] = useState('');
  const [step, setStep] = useState('login'); // login → otp → dashboard
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    // Simulate backend login credentials (admin only)
    if (email === 'admin@gmail.com' && password === 'admin@123') {
      setStep('otp'); // Go to 2FA step
    } else {
      alert('Invalid credentials. Please contact backend for admin login.');
    }
  };


  const handleOTPVerify = (e) => {
    e.preventDefault();
    // Simulated OTP check (normally generated backend)
    if (otp === '123456') {
      setIsAuthenticated(true);
    } else {
      alert('Invalid OTP');
    }
  };

  if (isAuthenticated) {
    return (
      <div className="login-container">
        <img
          src="/logo_guardian.png"
          alt="Guardian Logo"
          style={{ width: '100px', marginBottom: '20px' }}
        />
        <h2>Admin Dashboard</h2>
        <p>Welcome, {email}</p>
        {/* Add admin dashboard components here */}
      </div>
    );
  }

  return (
    <div className="login-container">
      <img
        src="/logo_guardian.png"
        alt="Guardian Logo"
        style={{ width: '100px', marginBottom: '20px' }}
      />
      <h2>Guardian Admin Login</h2>

      {step === 'login' && (
        <form onSubmit={handleLogin} className="login-form">
          <input
            type="email"
            placeholder="Admin Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Login</button>
        </form>
      )}

      {step === 'otp' && (
        <form onSubmit={handleOTPVerify} className="login-form">
          <input
            type="text"
            placeholder="Enter 2FA OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            required
          />
          <button type="submit">Verify OTP</button>
        </form>
      )}
    </div>
  );
}

export default App;
