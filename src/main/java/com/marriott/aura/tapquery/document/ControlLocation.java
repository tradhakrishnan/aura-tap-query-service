package com.marriott.aura.tapquery.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "control-locations")
public class ControlLocation {

    @Id
    private String id;

    private String       locationName;
    private String       app;
    private String       status;
    private List<String> supervisorEids;
    private List<String> controlledHotels;
    private String       createdBy;
    private Date         createdOn;
    private String       updatedBy;
    private Date         updatedOn;
}
