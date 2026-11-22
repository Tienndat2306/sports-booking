// Ràng buộc và chỉ mục Neo4j (mục 3.6.4). Chạy lại nhiều lần vẫn an toàn.
CREATE CONSTRAINT player_id   IF NOT EXISTS FOR (p:Player)   REQUIRE p.id IS UNIQUE;
CREATE CONSTRAINT court_id    IF NOT EXISTS FOR (c:Court)    REQUIRE c.id IS UNIQUE;
CREATE CONSTRAINT facility_id IF NOT EXISTS FOR (f:Facility) REQUIRE f.id IS UNIQUE;
CREATE CONSTRAINT sport_code  IF NOT EXISTS FOR (s:Sport)    REQUIRE s.code IS UNIQUE;
CREATE CONSTRAINT match_id    IF NOT EXISTS FOR (m:Match)    REQUIRE m.id IS UNIQUE;
CREATE INDEX match_open IF NOT EXISTS FOR (m:Match) ON (m.status, m.sportCode, m.startAt);

SHOW CONSTRAINTS YIELD name, labelsOrTypes, properties RETURN name, labelsOrTypes, properties;
SHOW INDEXES YIELD name, type, labelsOrTypes, properties WHERE type <> 'LOOKUP' RETURN name, type, labelsOrTypes, properties;
