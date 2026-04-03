import React, { useState, useEffect } from 'react';
import { portfolioService } from '../services/api';
import PositionsTable from './PositionsTable';
import TradesTable from './TradesTable';
import VoiceAssistantButton from './VoiceAssistantButton';
import AddTradeModal from './AddTradeModal';
import { Plus, TrendingUp, BriefcaseBusiness, ListPlus } from 'lucide-react';

const Dashboard: React.FC = () => {
  const [portfolios, setPortfolios] = useState<any[]>([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState<any>(null);
  const [data, setData] = useState({ positions: [], trades: [] });
  const [isLoading, setIsLoading] = useState(true);
  const [showAddTradeModal, setShowAddTradeModal] = useState(false);

  const fetchInitialData = async () => {
    try {
      const ports = await portfolioService.getPortfolios();
      setPortfolios(ports);
      if (ports.length > 0) setSelectedPortfolio(ports[0]);
    } catch {
      // error handling
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  const refreshPortfolioData = async () => {
    if (!selectedPortfolio) return;
    try {
      const [posData, tradeData] = await Promise.all([
        portfolioService.getPositions(selectedPortfolio.id),
        portfolioService.getTrades(selectedPortfolio.id),
      ]);
      setData({ positions: posData, trades: tradeData });
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    refreshPortfolioData();
  }, [selectedPortfolio]);

  if (isLoading) {
    return <div className="flex h-64 items-center justify-center text-gray-400">Loading your portfolios...</div>;
  }

  const addPortfolio = async () => {
    const name = prompt("Enter Portfolio Name:");
    if (name) {
      const newPort = await portfolioService.createPortfolio({ name, baseCurrency: 'INR' });
      setPortfolios([...portfolios, newPort]);
      setSelectedPortfolio(newPort);
    }
  };

  const totalValue = data.positions.reduce((sum: number, pos: any) => sum + (pos.marketValue || 0), 0);

  return (
    <div className="max-w-7xl mx-auto space-y-8 animate-in fade-in duration-500">
      
      {/* Top Header & Selector */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-gray-800/40 p-6 rounded-2xl border border-gray-700/50 backdrop-blur-sm">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-gray-700/50 rounded-xl">
             <BriefcaseBusiness className="text-green-400" size={24} />
          </div>
          <div>
            <p className="text-sm text-gray-400 font-medium mb-1">Active Portfolio</p>
            <select
              value={selectedPortfolio?.id || ''}
              onChange={(e) => setSelectedPortfolio(portfolios.find(p => p.id == e.target.value))}
              className="bg-transparent text-xl font-bold text-white focus:outline-none cursor-pointer hover:text-green-300 transition-colors"
            >
              {portfolios.map(p => <option key={p.id} value={p.id} className="bg-gray-800">{p.name}</option>)}
            </select>
          </div>
        </div>

        <button onClick={addPortfolio} className="flex items-center gap-2 bg-gray-700/50 hover:bg-gray-600 border border-gray-600 text-white py-2 px-4 rounded-xl transition-all">
          <Plus size={18} /> New Portfolio
        </button>
      </div>

      {/* Main Stats Card */}
      <div className="bg-gradient-to-br from-gray-800 to-gray-900 p-8 rounded-3xl border border-gray-700 shadow-2xl relative overflow-hidden">
        {/* Decorative elements */}
        <div className="absolute top-0 right-0 p-12 opacity-10">
          <TrendingUp size={120} />
        </div>
        
        <p className="text-gray-400 font-medium tracking-wide uppercase text-sm mb-2">Total Portfolio Value</p>
        <h2 className="text-5xl font-black text-transparent bg-clip-text bg-gradient-to-r from-white to-gray-400">
          ₹{totalValue.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </h2>
      </div>

      {/* Tables Section */}
      <div className="space-y-6">
        <div className="flex justify-between items-end">
           <h3 className="text-2xl font-bold text-white">Positions</h3>
           <div className="flex gap-3">
             <button onClick={() => setShowAddTradeModal(true)} className="flex items-center gap-2 bg-gray-700 hover:bg-gray-600 border border-gray-600 text-white font-semibold py-2.5 px-5 rounded-full transition-colors">
               <ListPlus size={18} /> Manual Trade
             </button>
             <VoiceAssistantButton onCommandProcessed={refreshPortfolioData} />
           </div>
        </div>
        <PositionsTable positions={data.positions} />
      </div>

      <div className="space-y-6 pt-8">
        <h3 className="text-2xl font-bold text-white">Trade History</h3>
        <TradesTable trades={data.trades} onTradeDeleted={refreshPortfolioData} />
      </div>

      {showAddTradeModal && selectedPortfolio && (
        <AddTradeModal 
           portfolioId={selectedPortfolio.id} 
           onClose={() => setShowAddTradeModal(false)}
           onTradeAdded={refreshPortfolioData}
        />
      )}
    </div>
  );
};

export default Dashboard;
