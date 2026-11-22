-- pop_due.lua: lấy và gỡ các phần tử đến hạn; nhiều worker cùng chạy không lấy trùng
-- KEYS[1] = hàng đợi; ARGV[1] = thời điểm hiện tại (epoch giây); ARGV[2] = số phần tử tối đa
local ids = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', ARGV[1], 'LIMIT', 0, tonumber(ARGV[2]))
for _, id in ipairs(ids) do redis.call('ZREM', KEYS[1], id) end
return ids
