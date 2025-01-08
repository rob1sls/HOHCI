package com.kpjunaid.repository;

import com.kpjunaid.entity.Comment;
import com.kpjunaid.entity.Post;
import com.kpjunaid.entity.Subject;
import com.kpjunaid.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
}
