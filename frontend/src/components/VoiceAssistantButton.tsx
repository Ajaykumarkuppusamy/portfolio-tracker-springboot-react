import { useState, useEffect, useRef } from 'react';
import { aiService } from '../services/api';
import { Mic, Loader2 } from 'lucide-react';

interface Props {
  onCommandProcessed: () => void;
}

const VoiceAssistantButton = ({ onCommandProcessed }: Props) => {
  const [isListening, setIsListening] = useState(false);
  const [status, setStatus] = useState<'idle' | 'listening' | 'processing'>('idle');
  const recognitionRef = useRef<any>(null);

  const isSupported = 'SpeechRecognition' in window || 'webkitSpeechRecognition' in window;

  useEffect(() => {
    if (!isSupported) return;

    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.lang = 'en-US';
    recognition.interimResults = false;

    recognition.onstart = () => {
      setIsListening(true);
      setStatus('listening');
    };

    recognition.onend = () => {
      setIsListening(false);
      if (status === 'listening') {
        setStatus('idle');
      }
    };

    recognition.onerror = (event: any) => {
      console.error('Speech recognition error:', event.error);
      setStatus('idle');
    };

    recognition.onresult = async (event: any) => {
      const transcript = event.results[0][0].transcript;
      setStatus('processing');
      try {
        await aiService.sendVoiceCommand(transcript);
        onCommandProcessed();
      } catch (err: any) {
        alert(`Error processing command: ${err.message}`);
      } finally {
        setStatus('idle');
      }
    };

    recognitionRef.current = recognition;
  }, [isSupported, onCommandProcessed]);

  const handleToggleListen = () => {
    if (isListening) {
      recognitionRef.current?.stop();
    } else {
      recognitionRef.current?.start();
    }
  };

  if (!isSupported) return null;

  let buttonClass = "flex items-center gap-2 font-semibold py-2.5 px-5 rounded-full shadow-lg transition-all duration-300 ";
  let content = <><Mic size={18} /> AI Trade</>;

  if (status === 'listening') {
    buttonClass += "bg-red-500 hover:bg-red-600 text-white animate-pulse shadow-red-500/40";
    content = <><span className="relative flex h-3 w-3 mr-2"><span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-white opacity-75"></span><span className="relative inline-flex rounded-full h-3 w-3 bg-white"></span></span> Listening...</>;
  } else if (status === 'processing') {
    buttonClass += "bg-gray-600 cursor-not-allowed text-gray-300";
    content = <><Loader2 size={18} className="animate-spin" /> Thinking...</>;
  } else {
    buttonClass += "bg-purple-600 hover:bg-purple-500 text-white shadow-purple-500/30 hover:scale-105";
  }

  return (
    <button onClick={handleToggleListen} disabled={status === 'processing'} className={buttonClass}>
      {content}
    </button>
  );
};

export default VoiceAssistantButton;
