package com.paras.validationDemo;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;



@Controller
public class CustomerController {

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        // Trim leading/trailing whitespace from every incoming String.
        // The ctor flag `true` means: if the trimmed value is empty (""),
        // convert it to null — so @NotBlank/@NotNull validations will fire.
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        // Tell Spring’s WebDataBinder to apply that editor to *all* String
        // fields during form‑to‑object binding.  Prevents users from sneaking
        // in whitespace‑only values and keeps your model clean.
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
        
    }

    @GetMapping("/")
    public String showForm(Model theModel){
        theModel.addAttribute("customer",new Customer());
        return "customer-form";
    }

    @PostMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("customer") Customer theCustomer,BindingResult theBindingResult){

        

        if(theBindingResult.hasErrors()){
            System.out.println("hii");
            System.out.println("bnding "+theBindingResult);
            return "customer-form";
        }
        return "customer-confirmation";
    }

} 
