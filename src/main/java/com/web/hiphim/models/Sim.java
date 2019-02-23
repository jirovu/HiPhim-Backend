package com.web.hiphim.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sim")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sim {
    @Id
    @Column(name = "stt")
    private int stt;

    @Column(name = "ask")
    private String ask;

    @Column(name = "ans")
    private String ans;

    @Column(name = "bys")
    private String bys;

    @Column(name = "time")
    private String time;
}
