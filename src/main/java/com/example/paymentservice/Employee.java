package com.example.paymentservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="employee")
public class Employee {
   private String Employee_ID;
    private String FirstName;
    private String LastName;
    private String Email;

    private List<String> PhoneNumber;

    private String DOJ;

    private double Salary;
}
