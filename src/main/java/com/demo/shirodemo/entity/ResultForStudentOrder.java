package com.demo.shirodemo.entity;

import com.demo.shirodemo.entity.table.OrdersEntity;
import com.demo.shirodemo.entity.table.WeChatEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultForStudentOrder {
    private List<ResultForOrder> list;
    private String customerName;
    private String studentId;

    public ResultForStudentOrder(String customerName, String studentId) {
        this.customerName = customerName;
        this.studentId = studentId;
    }

    public ResultForStudentOrder(List<ResultForOrder> list, String customerName, String studentId) {
        this.list = list;
        this.customerName = customerName;
        this.studentId = studentId;
    }
}
