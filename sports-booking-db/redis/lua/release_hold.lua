-- release_hold.lua: chỉ xóa khóa do chính phiên giữ chỗ này giữ
-- KEYS = khóa hold; ARGV[1] = holdId
local n = 0
for _, k in ipairs(KEYS) do
  if redis.call('GET', k) == ARGV[1] then n = n + redis.call('DEL', k) end
end
return n
