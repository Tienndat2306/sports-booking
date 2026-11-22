package com.huit.sportsbooking.model.neo4j;

import java.time.Instant;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Node: Match - Kèo giao lưu thể thao (Mục 3.6.1).
 * status: OPEN, FULL, CLOSED, BOOKED.
 */
@Node("Match")
public class MatchNode {

    @Id
    private String id;
    private String title;
    private String sportCode;
    private Instant startAt;
    private Instant deadline;
    private int needed;
    private int minLevel;
    private int maxLevel;
    private String status;          // OPEN | FULL | CLOSED | BOOKED
    private String note;

    @Relationship(type = "OF_SPORT", direction = Relationship.Direction.OUTGOING)
    private SportNode sport;

    @Relationship(type = "AT", direction = Relationship.Direction.OUTGOING)
    private CourtNode court;

    public MatchNode() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }
    public int getNeeded() { return needed; }
    public void setNeeded(int needed) { this.needed = needed; }
    public int getMinLevel() { return minLevel; }
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public SportNode getSport() { return sport; }
    public void setSport(SportNode sport) { this.sport = sport; }
    public CourtNode getCourt() { return court; }
    public void setCourt(CourtNode court) { this.court = court; }
}
