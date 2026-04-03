import React from 'react';

interface Position {
  symbol: { id: number; ticker: string };
  quantity: number;
  averageCost: number;
  lastPrice: number;
  marketValue: number;
  unrealizedPL: number;
}

interface Props {
  positions: Position[];
}

const PositionsTable: React.FC<Props> = ({ positions }) => {
  if (positions.length === 0) {
    return (
      <div className="text-center text-gray-400 mt-6 bg-gray-800/40 p-10 rounded-2xl border border-gray-700/50 backdrop-blur-sm">
        <h3 className="text-xl font-medium mb-2">No positions yet.</h3>
        <p>Add a 'BUY' trade to see your first position here!</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-800/60 rounded-2xl shadow-xl overflow-hidden border border-gray-700 backdrop-blur-md">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-700">
          <thead className="bg-gray-900/50">
            <tr>
              <th className="p-4 text-left text-xs font-semibold text-gray-400 uppercase tracking-wider">Symbol</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Quantity</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Avg. Cost</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Last Price</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Market Value</th>
              <th className="p-4 text-right text-xs font-semibold text-gray-400 uppercase tracking-wider">Unrealized P&L</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-700/50">
            {positions.map((pos) => {
              const isProfit = (pos.unrealizedPL || 0) >= 0;
              return (
                <tr key={pos.symbol.id} className="hover:bg-gray-700/30 transition-colors">
                  <td className="p-4 font-bold text-white">{pos.symbol.ticker}</td>
                  <td className="p-4 text-right text-gray-300">{Number(pos.quantity).toFixed(2)}</td>
                  <td className="p-4 text-right text-gray-300">₹{Number(pos.averageCost).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                  <td className="p-4 text-right text-white">₹{Number(pos.lastPrice).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                  <td className="p-4 text-right font-medium text-white">₹{Number(pos.marketValue).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                  <td className={`p-4 text-right font-bold ${isProfit ? 'text-emerald-400' : 'text-red-400'}`}>
                    {isProfit ? '+' : ''}₹{Number(pos.unrealizedPL).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PositionsTable;
