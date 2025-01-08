package com.kpjunaid.service;

import com.kpjunaid.dto.*;
import com.kpjunaid.entity.Comment;
import com.kpjunaid.entity.Post;
import com.kpjunaid.entity.Subject;
import com.kpjunaid.entity.User;
import com.kpjunaid.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubjectService {
    Subject createNewSubject(String signupDto);
 
}
