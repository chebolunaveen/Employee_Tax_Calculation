package com.example.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
public class EmployeeController {
    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/createEmployee")
    public ResponseEntity<String> createEmployee(@RequestBody Employee employee){


        if(!isValidEmployee(employee)){
            return ResponseEntity.badRequest().body("Invalid employee data");
        }
        employeeRepository.save(employee); ///saving employee details
        return ResponseEntity.ok("Employee Created");
    }
    private  boolean isValidEmployee(Employee employee){
        if(employee.getEmployee_ID()==null ||
          employee.getEmail()==null ||
             employee.getDOJ()==null ||
                  employee.getFirstName() ==null ||
                employee.getLastName() ==null ||
                employee.getSalary()==0 ||
                employee.getPhoneNumber().size()==0){
            return false;
        }
                return true;
    }

     @GetMapping("/tax-deduction")
    public ResponseEntity<List<EmployeeTaxInfo>> getTaxDeductions(){
         List<EmployeeTaxInfo> taxInfoList=new ArrayList<>();
         //fetching all employees from database
      List<Employee> employees=employeeRepository.findAll();
        for(Employee emp:employees){

            double yearlysalary=calculateYearlySalary(emp);
            double taxAmount=calculateTaxAmount(yearlysalary);
            double cessAmount=calculateCessAmount(yearlysalary);
            EmployeeTaxInfo employeeTaxInfo=new EmployeeTaxInfo(
                    emp.getEmployee_ID(),emp.getFirstName(),emp.getLastName(),
                    yearlysalary,taxAmount,cessAmount
            );
            // adding all employees with tax details
            taxInfoList.add(employeeTaxInfo);
        }
        return  ResponseEntity.ok(taxInfoList);
     }

     private double calculateYearlySalary(Employee employee){
         LocalDate today=LocalDate.now();
         LocalDate doj=LocalDate.parse(employee.getDOJ());
         int monthsworked=(int) ChronoUnit.MONTHS.between(doj,today);
         //calculate salary based on months worked
         double monthlysalary=employee.getSalary();
         double totalsalary=monthlysalary*monthsworked;

         //loss of pay per day
         double lossofpayperday=monthlysalary/30;

         //subtract loss of day not worked

         if(doj.getMonthValue()!=today.getMonthValue()){
             int daysnotWorked=doj.lengthOfMonth()-doj.getDayOfMonth();
             totalsalary-=lossofpayperday*daysnotWorked;

         }
         return  totalsalary;
     }

     public double calculateTaxAmount(double yearlySalary){
        double taxAmount=0;
        if(yearlySalary<=250000){
            taxAmount=0;
        }else if(yearlySalary<=500000){
            taxAmount=(yearlySalary-250000)*0.05;
        }else if(yearlySalary<=1000000){
            taxAmount=(2500000*0.05)+(yearlySalary-500000)*0.1;
        }
        else{
            taxAmount=(2500000*0.05)+(500000*0.1)+(yearlySalary-1000000)*0.2;
        }
        return taxAmount;
     }

     private double calculateCessAmount(double yearlySalary){
        double cessAmount=0;
        if(yearlySalary>=2500000){
           double  remainAmount=yearlySalary-2500000;
           cessAmount=(int)remainAmount/300000 * (300000*0.02);
        }
        return cessAmount;
     }

}
