package com.demo.shirodemo.entity.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table
public class SlideEntity {
    @Id
    @Column(length = 3)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String path;
    @Column
    private int type; //
}
