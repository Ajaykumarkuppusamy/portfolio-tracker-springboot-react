
import { portfolioService } from '../services/api';
import { Trash2 } from 'lucide-react';

interface Trade {
  id: number;
  tradeDatetime: string;
  symbolTicker: string;
  side: 'BUY' | 'SELL';
  quantity: number;
  price: number;
}

interface Props {
  trades: Trade[];
  onTradeDeleted: () => void;
}

const TradesTable = ({ trades, onTradeDeleted }: Props) => {
  const handleDelete = async (tradeId: number) => {
    if (confirm("Are you sure you want to delete this trade?")) {
      try {
        await portfolioService.deleteTrade(tradeId);
        onTradeDeleted();
      } catch (err: any) {
        alert("Failed to delete trade: " + (err.response?.data?.message || err.message));
      }
    }
  };

  if (trades.length === 0) {
    return (
      <div className="text-center text-gray-400 mt-6 bg-gray-800/40 p-10 rounded-2xl border border-gray-700/50 backdrop-blur-sm">
        <h3 className="text-xl font-medium mb-2">No trade history.</h3>
        <p>Your recorded trades for this portfolio will appear here.</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-800/60 rounded-2xl shadow-xl overflow-hidden border border-gray-700 backdrop-blur-md">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-700">
          <thead className="bg-gray-900/50">
            <tr>
              <th className="p-4 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Date</th>
              <th className="p-4 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Symbol</th>
              <th className="p-4 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Side</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Quantity</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Price</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Total</th>
              <th className="p-4 text-center text-xs font-semibold text-gray-400 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-700/50">
            {trades.map((trade) => (
              <tr key={trade.id} className="hover:bg-gray-700/30 transition-colors">
                <td className="p-4 text-gray-300">{new Date(trade.tradeDatetime).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })}</td>
                <td className="p-4 font-bold text-white">{trade.symbolTicker}</td>
                <td className="p-4">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                    trade.side === 'BUY' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'
                  }`}>
                    {trade.side}
                  </span>
                </td>
                <td className="p-4 text-right text-gray-300">{Number(trade.quantity).toFixed(2)}</td>
                <td className="p-4 text-right text-gray-300">₹{Number(trade.price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className="p-4 text-right font-medium text-white">₹{(trade.quantity * trade.price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className="p-4 text-center">
                  <button onClick={() => handleDelete(trade.id)} className="text-gray-400 hover:text-red-400 hover:bg-red-400/10 p-2 rounded-lg transition-colors">
                    <Trash2 size={16} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TradesTable;
