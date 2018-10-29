package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "installation_guide")
public class InstallationGuide implements Serializable {
    @Id
    @Column(length = 4)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String version;
    @Column
    private int type;
    @Column
    private String company;
    @Column
    private String logo;
    @Column
    private String bigLogo;
    @Column(length = 5)
    private int num;//查看次数
    @Column(length = 800)
    private String stepImage;
    @Column
    private String script;
    @Column(length = 900)
    private String content;
}
