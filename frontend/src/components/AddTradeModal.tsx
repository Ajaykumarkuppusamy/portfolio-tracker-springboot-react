import { useState, useEffect } from 'react';
import { portfolioService, marketService } from '../services/api';
import { X, Search } from 'lucide-react';

interface Props {
  portfolioId: number;
  onClose: () => void;
  onTradeAdded: () => void;
}

const AddTradeModal = ({ portfolioId, onClose, onTradeAdded }: Props) => {
  const [query, setQuery] = useState('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [selectedSymbol, setSelectedSymbol] = useState<any>(null);
  const [side, setSide] = useState('BUY');
  const [quantity, setQuantity] = useState('');
  const [price, setPrice] = useState('');

  useEffect(() => {
    if (query.length < 2) {
      setSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      setIsSearching(true);
      try {
        const data = await marketService.searchSymbols(query);
        setSearchResults(data.quotes || []);
      } catch (e) {
        console.error(e);
      } finally {
        setIsSearching(false);
      }
    }, 500);
    return () => clearTimeout(timer);
  }, [query]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!selectedSymbol) return alert("Please select a stock.");
    
    try {
      // 1. Ensure the symbol is registered in our local DB
      const symbolCreated = await marketService.addSymbol({
        ticker: selectedSymbol.symbol,
        name: selectedSymbol.longname || selectedSymbol.shortname || selectedSymbol.symbol,
        exchange: selectedSymbol.exchDisp || selectedSymbol.exchange,
        currency: 'INR'
      });

      // 2. Insert the actual trade
      await portfolioService.addTrade(portfolioId, symbolCreated.id, {
        side,
        quantity: parseFloat(quantity),
        price: parseFloat(price),
        tradeDatetime: new Date().toISOString()
      });
      onTradeAdded();
      onClose();
    } catch (err: any) {
      alert("Failed to add trade. " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 animate-in fade-in duration-200">
      <div className="bg-gray-800 rounded-2xl w-full max-w-md shadow-2xl border border-gray-700 overflow-hidden relative">
        <div className="p-6 border-b border-gray-700 flex justify-between items-center bg-gray-800/80">
          <h3 className="text-xl font-bold">Add Manual Trade</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-white transition-colors">
            <X size={20} />
          </button>
        </div>
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          
          {/* Dynamic Search using Yahoo Finance API */}
          <div className="relative">
            <label className="block text-sm text-gray-400 mb-1">Search Any Global Stock</label>
            {!selectedSymbol ? (
              <>
                <div className="relative">
                  <input 
                    type="text" 
                    value={query} 
                    onChange={e => setQuery(e.target.value)} 
                    placeholder="Search by Ticker or Name (e.g., TSLA, Reliance)" 
                    className="w-full bg-gray-900 border border-gray-600 rounded-lg p-2.5 pl-10 text-white" 
                  />
                  <Search className="absolute left-3 top-3 text-gray-500" size={18} />
                </div>
                {query.length >= 2 && (
                  <div className="absolute z-10 w-full mt-1 bg-gray-900 border border-gray-700 rounded-lg shadow-xl overflow-hidden max-h-48 overflow-y-auto">
                    {isSearching ? <div className="p-3 text-sm text-gray-400">Searching...</div> : null}
                    {!isSearching && searchResults.length === 0 ? <div className="p-3 text-sm text-gray-400">No results found.</div> : null}
                    {searchResults.map(res => (
                      <div 
                        key={res.symbol} 
                        onClick={() => setSelectedSymbol(res)}
                        className="p-3 hover:bg-gray-800 cursor-pointer border-b border-gray-800 last:border-0"
                      >
                        <div className="font-bold text-white">{res.symbol}</div>
                        <div className="text-xs text-gray-400">{res.longname || res.shortname} &bull; {res.exchDisp || res.exchange}</div>
                      </div>
                    ))}
                  </div>
                )}
              </>
            ) : (
               <div className="flex justify-between items-center bg-gray-900 border border-emerald-500/50 rounded-lg p-3">
                 <div>
                   <div className="font-bold text-emerald-400">{selectedSymbol.symbol}</div>
                   <div className="text-xs text-gray-400">{selectedSymbol.longname || selectedSymbol.shortname}</div>
                 </div>
                 <button type="button" onClick={() => { setSelectedSymbol(null); setQuery(''); setSearchResults([]); }} className="text-gray-400 hover:text-white px-2">Change</button>
               </div>
            )}
          </div>
          
          <div className="flex gap-4">
             <div className="flex-1">
                <label className="block text-sm text-gray-400 mb-1">Action</label>
                <select value={side} onChange={e => setSide(e.target.value)} className="w-full bg-gray-900 border border-gray-600 rounded-lg p-2.5 text-white">
                  <option value="BUY">BUY</option>
                  <option value="SELL">SELL</option>
                </select>
             </div>
             <div className="flex-1">
                <label className="block text-sm text-gray-400 mb-1">Quantity</label>
                <input type="number" step="any" min="0.1" value={quantity} onChange={e => setQuantity(e.target.value)} required className="w-full bg-gray-900 border border-gray-600 rounded-lg p-2.5 text-white" placeholder="e.g. 10.5" />
             </div>
          </div>
          <div>
            <label className="block text-sm text-gray-400 mb-1">Price per share</label>
            <input type="number" step="any" min="0.01" value={price} onChange={e => setPrice(e.target.value)} required className="w-full bg-gray-900 border border-gray-600 rounded-lg p-2.5 text-white" placeholder="₹" />
          </div>
          <div className="pt-4 flex gap-3">
             <button type="button" onClick={onClose} className="flex-1 py-2.5 rounded-lg border border-gray-600 hover:bg-gray-700 transition-colors">Cancel</button>
             <button type="submit" disabled={!selectedSymbol} className={`flex-1 py-2.5 rounded-lg font-bold shadow-lg transition-all ${selectedSymbol ? 'bg-emerald-600 hover:bg-emerald-500 shadow-emerald-600/20' : 'bg-gray-700 text-gray-500 cursor-not-allowed hidden'}`}>Record Trade</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddTradeModal;
