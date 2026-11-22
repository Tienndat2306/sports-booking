// Dữ liệu mẫu cho Neo4j theo Chương 3 (mục 3.6.4).
// Sử dụng MERGE nên chạy lại nhiều lần không tạo trùng nút hay quan hệ.

// 1. Nút Sport (3 môn)
UNWIND [
  {code: 'FOOTBALL5', name: 'Bóng đá 5 người'},
  {code: 'BADMINTON', name: 'Cầu lông'},
  {code: 'VOLLEYBALL', name: 'Bóng chuyền'}
] AS s
MERGE (sp:Sport {code: s.code})
SET sp.name = s.name;

// 2. Nút Facility (2 cơ sở)
UNWIND [
  {id: 'FAC01', name: 'Tổ hợp Thể thao HUIT Cơ sở 1 (Tân Phú)'},
  {id: 'FAC02', name: 'Cụm Sân Bóng Đá HUIT Cơ sở 2 (Tân Bình)'}
] AS f
MERGE (fac:Facility {id: f.id})
SET fac.name = f.name;

// 3. Nút Court (10 sân) và quan hệ LOCATED_IN, OF_SPORT
UNWIND [
  {id: 'FAC01-FB-01', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 1'},
  {id: 'FAC01-FB-02', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2'},
  {id: 'FAC01-FB-03', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 3 (Có mái che)'},
  {id: 'FAC01-BM-01', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 1'},
  {id: 'FAC01-BM-02', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 2'},
  {id: 'FAC01-BM-03', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 3'},
  {id: 'FAC01-VB-01', facilityId: 'FAC01', sportCode: 'VOLLEYBALL', name: 'Sân bóng chuyền 1'},
  {id: 'FAC02-FB-01', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2A'},
  {id: 'FAC02-FB-02', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2B'},
  {id: 'FAC02-FB-03', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2C'}
] AS c
MATCH (fac:Facility {id: c.facilityId}), (sp:Sport {code: c.sportCode})
MERGE (ct:Court {id: c.id})
SET ct.name = c.name, ct.sportCode = c.sportCode
MERGE (ct)-[:LOCATED_IN]->(fac)
MERGE (ct)-[:OF_SPORT]->(sp);

// 4. Nút Player (5 khách hàng)
UNWIND [
  {id: '01JU0001', name: 'Nguyễn Văn An'},
  {id: '01JU0002', name: 'Trần Thị Bình'},
  {id: '01JU0003', name: 'Lê Hoàng Cường'},
  {id: '01JU0004', name: 'Phạm Minh Dũng'},
  {id: '01JU0005', name: 'Hoàng Thảo Em'}
] AS p
MERGE (pl:Player {id: p.id})
SET pl.name = p.name, pl.createdAt = datetime('2026-10-01T03:00:00Z');

// 5. Quan hệ FRIEND, FOLLOWS, PLAYS, PLAYED_WITH, BOOKED (mục 3.6.4)
MATCH (a:Player {id: '01JU0001'}),
      (b:Player {id: '01JU0002'}),
      (c:Player {id: '01JU0003'}),
      (d:Player {id: '01JU0004'}),
      (e:Player {id: '01JU0005'}),
      (fb:Sport {code: 'FOOTBALL5'}),
      (bm:Sport {code: 'BADMINTON'}),
      (vb:Sport {code: 'VOLLEYBALL'}),
      (ct1:Court {id: 'FAC01-FB-01'})
MERGE (a)-[:FRIEND {since: date('2026-09-01')}]->(b)
MERGE (b)-[:FRIEND {since: date('2026-09-05')}]->(c)
MERGE (b)-[:FRIEND {since: date('2026-09-09')}]->(d)
MERGE (a)-[:FOLLOWS {since: date('2026-10-10')}]->(c)
MERGE (a)-[:PLAYS {level: 3}]->(fb)
MERGE (a)-[:PLAYS {level: 2}]->(bm)
MERGE (b)-[:PLAYS {level: 3}]->(fb)
MERGE (c)-[:PLAYS {level: 3}]->(fb)
MERGE (c)-[:PLAYS {level: 4}]->(vb)
MERGE (d)-[:PLAYS {level: 2}]->(fb)
MERGE (d)-[:PLAYS {level: 3}]->(bm)
MERGE (e)-[:PLAYS {level: 4}]->(bm)
MERGE (b)-[:PLAYED_WITH {times: 4, lastAt: date('2026-11-10'), sportCode: 'FOOTBALL5'}]->(c)
MERGE (a)-[bk:BOOKED]->(ct1)
SET bk.count = 3, bk.lastAt = datetime('2026-11-16T09:03:00Z');

// 6. Nút Match (Kèo giao lưu mẫu đang mở)
MATCH (a:Player {id: '01JU0001'}),
      (b:Player {id: '01JU0002'}),
      (fb:Sport {code: 'FOOTBALL5'}),
      (ct1:Court {id: 'FAC01-FB-01'})
MERGE (m:Match {id: '01JMATCH01'})
SET m.title = 'Giao lưu bóng đá 5v5 tối thứ Sáu (Cần thêm 3 bạn)',
    m.sportCode = 'FOOTBALL5',
    m.startAt = datetime('2026-11-20T11:00:00Z'),
    m.deadline = datetime('2026-11-20T08:00:00Z'),
    m.needed = 3,
    m.minLevel = 2,
    m.maxLevel = 4,
    m.status = 'OPEN',
    m.note = 'Chia tiền sân đều, đá vui vẻ hòa đồng'
MERGE (a)-[:CREATED {at: datetime('2026-11-16T09:10:00Z')}]->(m)
MERGE (b)-[:JOINED {status: 'ACCEPTED', at: datetime('2026-11-16T10:00:00Z')}]->(m)
MERGE (m)-[:OF_SPORT]->(fb)
MERGE (m)-[:AT]->(ct1);
