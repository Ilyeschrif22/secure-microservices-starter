const axios = require('axios');
const User  = require('../models/User');

const BASE_URL  = process.env.KEYCLOAK_BASE_URL;
const ADMIN_URL = `${BASE_URL}/admin/realms/${process.env.KEYCLOAK_REALM}`;

async function getAdminToken() {
  const params = new URLSearchParams({
    grant_type: 'password',
    client_id:  'admin-cli',
    username:   process.env.KEYCLOAK_ADMIN_USER,
    password:   process.env.KEYCLOAK_ADMIN_PASSWORD,
  });
  const { data } = await axios.post(
    `${BASE_URL}/realms/master/protocol/openid-connect/token`,
    params.toString(),
    { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
  );
  return data.access_token;
}

async function fetchAllUsers() {
  console.log('[Sync] Fetching from:', ADMIN_URL);
  const token   = await getAdminToken();
  const headers = { Authorization: `Bearer ${token}` };
  const users   = [];
  let first = 0;
  const max = 100;

  while (true) {
    const { data } = await axios.get(`${ADMIN_URL}/users`, {
      headers,
      params: { first, max },
    });
    users.push(...data);
    if (data.length < max) break;
    first += max;
  }
  return users;
}

async function syncUsers() {
  console.log('[Sync] Starting user sync…');

  let rawUsers;
  try {
    rawUsers = await fetchAllUsers();
  } catch (err) {
    console.error('[Sync] Failed to fetch users:', err.response?.data || err.message);
    throw err;
  }

  console.log(`[Sync] Fetched ${rawUsers.length} users`);
  if (rawUsers.length > 0) {
    console.log('[Sync] Sample user from Keycloak:', JSON.stringify(rawUsers[0], null, 2));
  }

  const operations = rawUsers.map((raw) => ({
    updateOne: {
      filter: { keycloakId: raw.id },
      update: {
        $set: {
          keycloakId:    raw.id,
          username:      raw.username      || null,
          email:         raw.email         || null,
          firstName:     raw.firstName     || null,
          lastName:      raw.lastName      || null,
          enabled:       raw.enabled,
          emailVerified: raw.emailVerified || false,
          attributes:    raw.attributes    || {},
          raw,
          lastSyncedAt:  new Date(),
        },
      },
      upsert: true,
    },
  }));

  const result = await User.bulkWrite(operations, { ordered: false });
  console.log(`[Sync] Done — upserted: ${result.upsertedCount}, modified: ${result.modifiedCount}`);
  return result;
}

module.exports = { syncUsers };