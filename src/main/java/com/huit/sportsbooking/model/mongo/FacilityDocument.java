package com.huit.sportsbooking.model.mongo;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: facilities (Mục 3.4.3).
 * Hỗ trợ tìm kiếm sân gần nhất thông qua chỉ mục 2dsphere trên trường location.
 */
@Document(collection = "facilities")
public class FacilityDocument {

    @Id
    private String id;              // FAC01, FAC02
    private String name;
    private Address address;
    private GeoJsonPoint location;  // [kinh độ, vĩ độ]
    private Hours hours;
    private List<String> sports;    // ["FOOTBALL5", "BADMINTON", "VOLLEYBALL"]
    private String ownerId;
    private String status;          // ACTIVE | INACTIVE

    public static class Address {
        private String street;
        private String district;
        private String city;

        public Address() {}

        public Address(String street, String district, String city) {
            this.street = street;
            this.district = district;
            this.city = city;
        }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    public static class Hours {
        private String open;
        private String close;
        private int slotMinutes;

        public Hours() {}

        public Hours(String open, String close, int slotMinutes) {
            this.open = open;
            this.close = close;
            this.slotMinutes = slotMinutes;
        }

        public String getOpen() { return open; }
        public void setOpen(String open) { this.open = open; }
        public String getClose() { return close; }
        public void setClose(String close) { this.close = close; }
        public int getSlotMinutes() { return slotMinutes; }
        public void setSlotMinutes(int slotMinutes) { this.slotMinutes = slotMinutes; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public GeoJsonPoint getLocation() { return location; }
    public void setLocation(GeoJsonPoint location) { this.location = location; }
    public Hours getHours() { return hours; }
    public void setHours(Hours hours) { this.hours = hours; }
    public List<String> getSports() { return sports; }
    public void setSports(List<String> sports) { this.sports = sports; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
