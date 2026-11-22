-- hold_slots.lua: giữ tất cả các khung hoặc không giữ khung nào
-- KEYS = khóa hold của từng khung; ARGV[1] = holdId; ARGV[2] = TTL giây
for _, k in ipairs(KEYS) do
  if redis.call('EXISTS', k) == 1 then return 0 end
end
for _, k in ipairs(KEYS) do
  redis.call('SET', k, ARGV[1], 'EX', tonumber(ARGV[2]))
end
return 1
