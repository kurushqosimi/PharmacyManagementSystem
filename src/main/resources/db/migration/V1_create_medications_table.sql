CREATE TABLE IF NOT EXISTS medications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    quantity INTEGER,
    barcode TEXT UNIQUE,
    isActive BOOLEAN DEFAULT 1,
    isPrescriptionRequired BOOLEAN DEFAULT 0
);
