package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: users (Mục 3.4.3).
 */
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;
    private String role;            // CUSTOMER | STAFF | OWNER | ADMIN
    private String fullName;
    private String phone;
    private String email;
    private String passwordHash;
    private String status;          // ACTIVE | LOCKED
    private Profile profile;
    private int noShowCount;
    private BookingRestriction bookingRestriction;
    private List<Note> notes;
    private List<String> facilityIds;
    private boolean isWalkIn;
    private Instant createdAt;
    private Instant deletedAt;

    public static class Profile {
        private List<String> favoriteSports;
        private Map<String, Integer> skillLevels;

        public Profile() {}

        public Profile(List<String> favoriteSports, Map<String, Integer> skillLevels) {
            this.favoriteSports = favoriteSports;
            this.skillLevels = skillLevels;
        }

        public List<String> getFavoriteSports() { return favoriteSports; }
        public void setFavoriteSports(List<String> favoriteSports) { this.favoriteSports = favoriteSports; }
        public Map<String, Integer> getSkillLevels() { return skillLevels; }
        public void setSkillLevels(Map<String, Integer> skillLevels) { this.skillLevels = skillLevels; }
    }

    public static class BookingRestriction {
        private boolean active;
        private boolean requirePrepay;
        private Instant since;

        public BookingRestriction() {}

        public BookingRestriction(boolean active, boolean requirePrepay, Instant since) {
            this.active = active;
            this.requirePrepay = requirePrepay;
            this.since = since;
        }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public boolean isRequirePrepay() { return requirePrepay; }
        public void setRequirePrepay(boolean requirePrepay) { this.requirePrepay = requirePrepay; }
        public Instant getSince() { return since; }
        public void setSince(Instant since) { this.since = since; }
    }

    public static class Note {
        private Instant at;
        private String by;
        private String text;

        public Note() {}

        public Note(Instant at, String by, String text) {
            this.at = at;
            this.by = by;
            this.text = text;
        }

        public Instant getAt() { return at; }
        public void setAt(Instant at) { this.at = at; }
        public String getBy() { return by; }
        public void setBy(String by) { this.by = by; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public int getNoShowCount() { return noShowCount; }
    public void setNoShowCount(int noShowCount) { this.noShowCount = noShowCount; }
    public BookingRestriction getBookingRestriction() { return bookingRestriction; }
    public void setBookingRestriction(BookingRestriction bookingRestriction) { this.bookingRestriction = bookingRestriction; }
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
    public List<String> getFacilityIds() { return facilityIds; }
    public void setFacilityIds(List<String> facilityIds) { this.facilityIds = facilityIds; }
    public boolean isWalkIn() { return isWalkIn; }
    public void setWalkIn(boolean walkIn) { isWalkIn = walkIn; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
