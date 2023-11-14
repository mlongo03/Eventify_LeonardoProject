package com.eventify.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "photo", schema = "eventify")
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String photoName;

	@NotNull
	private String photoType;

	@NotNull
	private Boolean is_deleted;

	@Lob
	@NotNull
	private byte[] data;

	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;

	public Photo(String photoType, String photoName, Boolean is_deleted, byte[] data) {
		this.photoType = photoType;
		this.photoName = photoName;
		this.is_deleted = is_deleted;
		this.data = data;
	}

	public Long getId() {
		return (id);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhotoType() {
		return (this.photoType);
	}

	public void setPhotoType(String photoType) {
		this.photoType = photoType;
	}

	public String getPhotoName() {
		return (this.photoName);
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	public Boolean getIsDeleted() {
		return (this.is_deleted);
	}

	public void setIsDeleted(Boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public byte[] getData() {
		return (this.data);
	}

	public void setData(byte[] data) {
		this.data = data;
	}

    @Override
    public String toString() {
        return "Eventi{" +
                "id=" + id +
                ", photoName='" + photoName + '\'' +
                ", photoType=" + photoType +
                ", data=" + data +
                ", is_deleted='" + is_deleted + '\'' +
                '}';
    }
}
