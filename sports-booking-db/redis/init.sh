#!/bin/sh
# Khởi tạo Redis: tạo nhóm tiêu thụ cho Stream, nạp và kiểm tra 4 script Lua (mục 3.7).
R="redis-cli -h redis"
LUA=/scripts/lua

until $R ping 2>/dev/null | grep -q PONG; do sleep 1; done

echo "== Nhóm tiêu thụ của sb:stream:events =="
for g in cg-neo4j cg-stats cg-cache; do
  out=$($R XGROUP CREATE sb:stream:events "$g" '$' MKSTREAM 2>&1)
  case "$out" in
    OK)        echo "tạo $g: OK" ;;
    *BUSYGROUP*) echo "tạo $g: đã tồn tại" ;;
    *)         echo "tạo $g: LỖI $out"; exit 1 ;;
  esac
done

echo "== Nạp script Lua (SCRIPT LOAD) =="
for f in "$LUA"/*.lua; do
  sha=$($R -x script load < "$f")
  echo "$(basename "$f") -> $sha"
done

echo "== Kiểm thử nhanh =="
fail=0
check() { # tên, kết quả thật, kết quả mong đợi
  if [ "$2" = "$3" ]; then echo "PASS  $1"; else echo "FAIL  $1 (nhận '$2', mong đợi '$3')"; fail=1; fi
}
K1='sb:test:{T:2026-01-01}:18:00'
K2='sb:test:{T:2026-01-01}:19:00'

check "hold_slots giữ 2 khung"            "$($R --eval $LUA/hold_slots.lua "$K1" "$K2" , H1 30)" "1"
check "hold_slots từ chối khi đã bị giữ"  "$($R --eval $LUA/hold_slots.lua "$K1" "$K2" , H2 30)" "0"
check "release_hold sai holdId không xóa" "$($R --eval $LUA/release_hold.lua "$K1" "$K2" , WRONG)" "0"
check "release_hold đúng holdId xóa 2"    "$($R --eval $LUA/release_hold.lua "$K1" "$K2" , H1)" "2"

$R DEL sb:test:rl > /dev/null
check "rate_limit lần 1"                  "$($R --eval $LUA/rate_limit.lua sb:test:rl , 60)" "1"
check "rate_limit lần 2"                  "$($R --eval $LUA/rate_limit.lua sb:test:rl , 60)" "2"
ttl=$($R TTL sb:test:rl); if [ "$ttl" -gt 0 ]; then check "rate_limit đặt TTL" ok ok; else check "rate_limit đặt TTL" "$ttl" ">0"; fi

$R DEL sb:test:q > /dev/null
$R ZADD sb:test:q 100 a 200 b 999999999999 c > /dev/null
popped=$($R --eval $LUA/pop_due.lua sb:test:q , 300 10 | tr '\n' ',')
check "pop_due lấy phần tử đến hạn"       "$popped" "a,b,"
check "pop_due để lại phần tử chưa đến"   "$($R ZCARD sb:test:q)" "1"

$R DEL sb:test:rl sb:test:q "$K1" "$K2" > /dev/null
[ $fail -eq 0 ] && echo "== Redis OK ==" || { echo "== Redis có lỗi =="; exit 1; }
