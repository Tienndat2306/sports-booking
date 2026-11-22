-- rate_limit.lua: tăng bộ đếm và đặt hạn trong cùng một bước
-- KEYS[1] = khóa đếm; ARGV[1] = cửa sổ giây
local c = redis.call('INCR', KEYS[1])
if c == 1 then redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1])) end
return c
