CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    tz VARCHAR(50) DEFAULT 'UTC',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE portfolios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    base_currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE symbols (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(50) NOT NULL,
    exchange VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    name VARCHAR(255),
    UNIQUE KEY uk_symbol (ticker, exchange)
) ENGINE=InnoDB;

CREATE TABLE trades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    symbol_id BIGINT NOT NULL,
    side VARCHAR(4) NOT NULL, -- BUY/SELL
    quantity DECIMAL(18,6) NOT NULL,
    price DECIMAL(18,6) NOT NULL,
    trade_datetime DATETIME NOT NULL,
    fees DECIMAL(18,6) DEFAULT 0.0,
    reason TEXT,
    tag VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE,
    FOREIGN KEY (symbol_id) REFERENCES symbols(id)
) ENGINE=InnoDB;

CREATE TABLE lots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trade_buy_id BIGINT NOT NULL,
    trade_sell_id BIGINT,
    quantity DECIMAL(18,6) NOT NULL,
    cost_price DECIMAL(18,6) NOT NULL,
    realized_pl DECIMAL(18,6),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trade_buy_id) REFERENCES trades(id) ON DELETE CASCADE,
    FOREIGN KEY (trade_sell_id) REFERENCES trades(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE price_ticks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol_id BIGINT NOT NULL,
    as_of DATETIME NOT NULL,
    last DECIMAL(18,6),
    day_open DECIMAL(18,6),
    day_high DECIMAL(18,6),
    day_low DECIMAL(18,6),
    prev_close DECIMAL(18,6),
    volume BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (symbol_id) REFERENCES symbols(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cashflows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    symbol_id BIGINT,
    flow_datetime DATETIME NOT NULL,
    amount DECIMAL(18,6) NOT NULL,
    kind VARCHAR(20) NOT NULL, -- DIVIDEND, DEPOSIT, WITHDRAWAL, FEE, INTEREST
    note TEXT,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE,
    FOREIGN KEY (symbol_id) REFERENCES symbols(id)
) ENGINE=InnoDB;

-- Pre-populate some Indian stocks
INSERT INTO symbols (ticker, exchange, currency, name) VALUES
('RELIANCE', 'NSE', 'INR', 'Reliance Industries Ltd.'),
('TCS', 'NSE', 'INR', 'Tata Consultancy Services Ltd.'),
('HDFCBANK', 'NSE', 'INR', 'HDFC Bank Ltd.'),
('INFY', 'NSE', 'INR', 'Infosys Ltd.'),
('ICICIBANK', 'NSE', 'INR', 'ICICI Bank Ltd.'),
('HINDUNILVR', 'NSE', 'INR', 'Hindustan Unilever Ltd.'),
('SBIN', 'NSE', 'INR', 'State Bank of India'),
('BAJFINANCE', 'NSE', 'INR', 'Bajaj Finance Ltd.');
