package com.demo.shirodemo.entity.table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "software_version")
@Getter
@Setter
@NoArgsConstructor
public class SoftwareVersion implements Serializable{
    @Id
    @Column(length = 2,nullable = false)
    private int id;
    @Column(length = 4,nullable = false)
    private int softwareId;
    @Column(length = 10,nullable = false)
    private String version;
}
