package com.marcoaga02.modularhub.shared.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class AuditDTO {
    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
    private OffsetDateTime deletedOn;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
}
