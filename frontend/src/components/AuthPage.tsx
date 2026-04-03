import React, { useState } from 'react';
import { useAuthStore } from '../store/useAuthStore';
import { authService } from '../services/api';
import { Eye, EyeOff } from 'lucide-react';

const AuthPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  const { login } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');
    setIsLoading(true);
    try {
      if (isLogin) {
        const data = await authService.login(email, password);
        login(data.jwt);
      } else {
        await authService.register(email, password);
        setMessage('Registration successful! Please login.');
        setIsLogin(true);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'An error occurred.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-16 p-8 bg-gray-800/80 backdrop-blur-xl rounded-2xl shadow-2xl border border-gray-700">
      <h2 className="text-3xl font-bold text-center text-white mb-8">
        {isLogin ? 'Welcome Back' : 'Create Account'}
      </h2>
      
      {error && (
        <div className="bg-red-500/10 border border-red-500/50 text-red-500 text-sm p-3 rounded-lg mb-6 text-center">
          {error}
        </div>
      )}
      {message && (
        <div className="bg-green-500/10 border border-green-500/50 text-green-400 text-sm p-3 rounded-lg mb-6 text-center">
          {message}
        </div>
      )}
      
      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-gray-400 text-sm font-medium mb-2" htmlFor="email">Email Address</label>
          <input
            type="email"
            id="email"
            value={email}
             onChange={e => setEmail(e.target.value)}
            className="w-full p-3 rounded-xl bg-gray-900/50 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-green-500/50 hover:border-gray-500 transition-colors"
            required
            placeholder="you@example.com"
          />
        </div>
        <div>
          <label className="block text-gray-400 text-sm font-medium mb-2" htmlFor="password">Password</label>
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="w-full p-3 pr-12 rounded-xl bg-gray-900/50 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-green-500/50 hover:border-gray-500 transition-colors"
              required
              placeholder="••••••••"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
            >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>
        </div>
        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-400 hover:to-emerald-500 text-white font-bold py-3 px-4 rounded-xl shadow-lg hover:shadow-green-500/25 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? 'Processing...' : isLogin ? 'Login' : 'Sign Up'}
        </button>
      </form>
      
      <p className="text-center mt-6 text-gray-400 text-sm">
        {isLogin ? "Don't have an account?" : "Already have an account?"}
        <button
          onClick={() => setIsLogin(!isLogin)}
          className="text-green-400 hover:text-green-300 font-medium ml-2 hover:underline transition-colors"
        >
          {isLogin ? 'Register now' : 'Login instead'}
        </button>
      </p>
    </div>
  );
};

export default AuthPage;
