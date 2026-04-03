const mongoose = require('mongoose');

const userSchema = new mongoose.Schema(
  {
    keycloakId:  { type: String, required: true, unique: true },
    username:    { type: String, required: true, index: true },
    email:       String,
    firstName:   String,
    lastName:    String,
    enabled:     { type: Boolean, default: true },
    emailVerified: Boolean,
    attributes:  { type: Map, of: mongoose.Schema.Types.Mixed },
    realmRoles:  [String],
    raw:         { type: mongoose.Schema.Types.Mixed },
    lastSyncedAt:{ type: Date, default: Date.now },
  },
  { timestamps: true }
);

module.exports = mongoose.model('User', userSchema);