package com.kpjunaid.mapper;

import com.kpjunaid.dto.UpdateUserInfoDto;
import com.kpjunaid.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-16T20:52:12+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class MapstructMapperUpdateImpl implements MapstructMapperUpdate {

    @Override
    public void updateUserFromUserUpdateDto(UpdateUserInfoDto updateUserInfoDto, User user) {
        if ( updateUserInfoDto == null ) {
            return;
        }

        if ( updateUserInfoDto.getFirstName() != null ) {
            user.setFirstName( updateUserInfoDto.getFirstName() );
        }
        if ( updateUserInfoDto.getLastName() != null ) {
            user.setLastName( updateUserInfoDto.getLastName() );
        }
        if ( updateUserInfoDto.getIntro() != null ) {
            user.setIntro( updateUserInfoDto.getIntro() );
        }
        if ( updateUserInfoDto.getGender() != null ) {
            user.setGender( updateUserInfoDto.getGender() );
        }
        if ( updateUserInfoDto.getHometown() != null ) {
            user.setHometown( updateUserInfoDto.getHometown() );
        }
        if ( updateUserInfoDto.getCurrentCity() != null ) {
            user.setCurrentCity( updateUserInfoDto.getCurrentCity() );
        }
        if ( updateUserInfoDto.getEduInstitution() != null ) {
            user.setEduInstitution( updateUserInfoDto.getEduInstitution() );
        }
        if ( updateUserInfoDto.getWorkplace() != null ) {
            user.setWorkplace( updateUserInfoDto.getWorkplace() );
        }
        if ( updateUserInfoDto.getReportExp() != null ) {
            user.setReportExp( updateUserInfoDto.getReportExp() );
        }
        if ( updateUserInfoDto.getBirthDate() != null ) {
            user.setBirthDate( updateUserInfoDto.getBirthDate() );
        }
    }
}
