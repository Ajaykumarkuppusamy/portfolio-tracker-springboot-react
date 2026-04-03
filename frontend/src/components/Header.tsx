import React from 'react';
import { useAuthStore } from '../store/useAuthStore';
import { Briefcase, LogOut } from 'lucide-react';

const Header: React.FC = () => {
  const { token, logout } = useAuthStore();

  return (
    <header className="bg-gray-800 p-4 shadow-md flex justify-between items-center sm:px-6 lg:px-8 border-b border-gray-700">
      <div className="flex items-center gap-3">
        <Briefcase className="text-green-400" size={28} />
        <h1 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-green-400 to-emerald-600">
          Portfolio Tracker
        </h1>
      </div>
      {token && (
        <button
          onClick={logout}
          className="flex items-center gap-2 bg-gray-700 hover:bg-red-500/20 hover:text-red-400 text-gray-300 font-medium py-2 px-4 rounded-lg transition-all duration-300 border border-gray-600 hover:border-red-500/50 shadow-sm"
        >
          <LogOut size={18} />
          <span className="hidden sm:inline">Logout</span>
        </button>
      )}
    </header>
  );
};

export default Header;
