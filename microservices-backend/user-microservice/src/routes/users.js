const router = require('express').Router();
const User   = require('../models/User');
const { syncUsers } = require('../services/userSyncService');

router.post('/sync', async (req, res) => {
  try {
    const result = await syncUsers();
    res.json({ message: 'Sync completed', upserted: result.upsertedCount, modified: result.modifiedCount });
  } catch (err) {
    res.status(502).json({ error: err.message });
  }
});

router.get('/', async (req, res) => {
  try {
    const filter = {};
    if (req.query.enabled !== undefined) filter.enabled = req.query.enabled === 'true';
    if (req.query.search) {
      const re = new RegExp(req.query.search, 'i');
      filter.$or = [{ username: re }, { email: re }, { firstName: re }, { lastName: re }];
    }

    const page  = Math.max(1, parseInt(req.query.page)  || 1);
    const limit = Math.min(200, parseInt(req.query.limit) || 50);
    const skip  = (page - 1) * limit;

    const [items, total] = await Promise.all([
      User.find(filter).sort({ username: 1 }).skip(skip).limit(limit).select('-raw -__v'),
      User.countDocuments(filter),
    ]);

    res.json({ total, page, limit, items });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findOne(
      /^[a-f\d]{24}$/i.test(id) ? { _id: id } : { username: id }
    ).select('-__v');

    if (!user) return res.status(404).json({ error: 'User not found' });
    res.json(user);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;