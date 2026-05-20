package com.marriott.aura.tapquery.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "control-hotels")
public class ControlHotel {

    @Id
    private String id;

    private String locationName;
    private String crsSystem;
    private String status;
    private String createdBy;
    private Date   createdOn;
    private String updatedBy;
    private Date   updatedOn;
}
