package com.kpjunaid.service;

import com.kpjunaid.common.AppConstants;
import com.kpjunaid.common.UserPrincipal;
import com.kpjunaid.entity.Comment;
import com.kpjunaid.entity.Country;
import com.kpjunaid.entity.Post;
import com.kpjunaid.entity.Subject;
import com.kpjunaid.entity.User;
import com.kpjunaid.enumeration.Role;
import com.kpjunaid.exception.EmailExistsException;
import com.kpjunaid.exception.InvalidOperationException;
import com.kpjunaid.exception.SameEmailUpdateException;
import com.kpjunaid.exception.UserNotFoundException;
import com.kpjunaid.mapper.MapStructMapper;
import com.kpjunaid.mapper.MapstructMapperUpdate;
import com.kpjunaid.repository.SubjectRepository;
import com.kpjunaid.repository.UserRepository;
import com.kpjunaid.response.UserResponse;
import com.kpjunaid.dto.*;
import com.kpjunaid.util.FileNamingUtil;
import com.kpjunaid.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    private final CountryService countryService;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MapStructMapper mapStructMapper;
    private final MapstructMapperUpdate mapstructMapperUpdate;
    private final Environment environment;
    private final FileNamingUtil fileNamingUtil;
    private final FileUploadUtil fileUploadUtil;

 

    @Override
    public Subject createNewSubject(String signupDto) {



            Subject newUser = new Subject();
            System.out.println("Dans subjectserviceimpl" + signupDto);

            newUser.setStudyGroup(signupDto);

            Subject savedUser = subjectRepository.save(newUser);


            return savedUser;
        

    }

 
    
}
