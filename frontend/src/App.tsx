
import Header from './components/Header';
import AuthPage from './components/AuthPage';
import Dashboard from './components/Dashboard';
import { useAuthStore } from './store/useAuthStore';

function App() {
  const { token } = useAuthStore();

  return (
    <div className="min-h-screen bg-[#0f172a] selection:bg-green-500/30 text-slate-200">
      <Header />
      <main className="p-4 sm:p-8 max-w-7xl mx-auto">
        {token ? <Dashboard /> : <AuthPage />}
      </main>
    </div>
  );
}

export default App;
