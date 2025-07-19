package com.paras.crud.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paras.crud.entity.Student;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    List<Student>theStudents;

    @PostConstruct
    public void loadData(){
        theStudents=new ArrayList<>();
        theStudents.add(new Student("paras","singh"));
        theStudents.add(new Student("suarav","banty"));
        theStudents.add(new Student("prabhat","mishra"));
    }


    @GetMapping("/students")
    public List<Student> getStudents(){
        return theStudents;
    }


    @GetMapping("/students/{studentID}")
    public Student getStudent(@PathVariable int studentID){

        if((studentID<0) || (theStudents.size()<studentID)){
            throw new StudentNotFoundException("Student id not found "+studentID);
        }

        return theStudents.get(studentID);
    }



    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(StudentNotFoundException exc){
        StudentErrorResponse error=new StudentErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(Exception exc){
        StudentErrorResponse error=new StudentErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

}
