package com.javaTraining.session4.repository;

import com.javaTraining.session4.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // left empty for now, but can add custom methods if needed or for communicating
    // with db in the future
}
