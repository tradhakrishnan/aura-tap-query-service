package com.marriott.aura.tapquery.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "user-eids")
public class UserEid {

    @Id
    private String id;

    private String       eid;
    private String       app;
    private String       status;
    private List<String> locations;
    private List<String> assignments;
    private Date         createdOn;
    private Date         updatedOn;
}
