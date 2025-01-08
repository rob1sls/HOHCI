package com.kpjunaid.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {


    // a subject has an id, a study group, an number of exp
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String studyGroup;
    private int numberofReports;
    private int exp;


    
}
