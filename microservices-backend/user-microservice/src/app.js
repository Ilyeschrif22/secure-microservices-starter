require('dotenv').config();

const express   = require('express');
const connectDB = require('./config/db');

const app  = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.use('/users', require('./routes/users'));

app.get('/health', (_req, res) =>
  res.json({ status: 'ok', timestamp: new Date() })
);

async function start() {
  await connectDB();
  app.listen(PORT, () => {
    console.log(`[App] Server running on http://localhost:${PORT}`);
  });
}

start().catch((err) => {
  console.error('[App] Fatal startup error:', err);
  process.exit(1);
});