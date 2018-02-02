package com.jay.dao;

import com.jay.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person,Integer> {

    /**
     * 通过年龄来查询
     * @param age
     * @return
     */
    public List<Person> findByAge(Integer age);
}
