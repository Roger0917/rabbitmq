package com.jay.controller;

import com.jay.dao.PersonRepository;
import com.jay.entity.Person;
import com.jay.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    public class PersonController{

        @Autowired
        private PersonService personService;
        @Autowired
        PersonRepository personRepository;
        @GetMapping(value = "/person")
        private List<Person> personList(){
            return personRepository.findAll();
        }

        /**
         * 添加一个人员
         */
        @PostMapping(value = "/person")
        public Person personAdd(@RequestParam("name") String name,@RequestParam("age") Integer age){
            Person person=new Person();
            person.setName(name);
            person.setAge(age);

            return personRepository.save(person);
        }

        /**
         * 查询一个人员
         */
        @GetMapping(value = "/person/{id}")
        public Person personFindOne(@PathVariable("id") Integer id){
            return personRepository.findOne(id);
        }

        /**
         * 删除一个人员
         */
        @DeleteMapping(value = "/person/{id}")
        public void personDelete(@PathVariable("id") Integer id){
            personRepository.delete(id);
        }
        /**
         * 更新一个人员
         */
        @PutMapping(value = "/person/{id}")
        public Person personUpdate(@PathVariable("id") Integer id,
                                   @RequestParam("name")String name,
                                   @RequestParam("age") Integer age){

            Person person=new Person();
            person.setId(id);
            person.setName(name);
            person.setAge(age);
            return personRepository.save(person);
        }

        /**
         * 通过年龄查询
         * @param age
         * @
         */
        @GetMapping(value = "/person/age/{age}")
        public List<Person> personListByAge(@PathVariable("age") Integer age){
            return personRepository.findByAge(age);
        }

        /**
         * 事务测试
         */
        @PostMapping("/person/two")
        public void personTwo(){
            personService.insertTwo();
        }
    }