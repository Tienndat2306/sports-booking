package com.huit.sportsbooking.model.neo4j;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Node: Player (Mục 3.6.1).
 * id trùng với users._id trên MongoDB.
 */
@Node("Player")
public class PlayerNode {

    @Id
    private String id;
    private String name;
    private Instant createdAt;

    @Relationship(type = "FRIEND", direction = Relationship.Direction.OUTGOING)
    private Set<PlayerNode> friends = new HashSet<>();

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<PlayerNode> following = new HashSet<>();

    public PlayerNode() {}

    public PlayerNode(String id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Set<PlayerNode> getFriends() { return friends; }
    public void setFriends(Set<PlayerNode> friends) { this.friends = friends; }
    public Set<PlayerNode> getFollowing() { return following; }
    public void setFollowing(Set<PlayerNode> following) { this.following = following; }
}
