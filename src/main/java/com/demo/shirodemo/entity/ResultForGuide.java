package com.demo.shirodemo.entity;

import com.demo.shirodemo.entity.table.InstallationGuide;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultForGuide implements Serializable {
    private int type;
    private List<InstallationGuide> guides;

    public ResultForGuide(int type) {
        this.type = type;
        this.guides = new ArrayList<>(5);
    }
}
