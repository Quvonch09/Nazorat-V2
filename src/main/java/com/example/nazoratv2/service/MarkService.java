package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqMark;
import com.example.nazoratv2.dto.response.ResMark;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Mark;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.MarkCategoryStatus;
import com.example.nazoratv2.entity.enums.MarkStatus;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.MarkMapper;
import com.example.nazoratv2.repository.MarkRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final MarkMapper markMapper;
    private final UserRepository userRepository;

    public ApiResponse<String> saveMark(ReqMark reqMark){
        Student student = studentRepository.findById(reqMark.getStudentId()).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        Mark mark;
        if (reqMark.getMarkStatus().equals(MarkStatus.KUNLIK_BAHO)){
            int score = (reqMark.getActivityScore()+ reqMark.getHomeworkScore())/2;
            mark = Mark.builder()
                    .student(student)
                    .status(reqMark.getMarkStatus())
                    .homeworkScore(reqMark.getHomeworkScore())
                    .activeScore(reqMark.getActivityScore())
                    .totalScore(score)
                    .date(reqMark.getDate())
                    .markCategoryStatus(markCategoryStatus(score))
                    .build();
        } else {
            mark = Mark.builder()
                    .student(student)
                    .status(reqMark.getMarkStatus())
                    .homeworkScore(null)
                    .activeScore(null)
                    .totalScore(reqMark.getTotalScore())
                    .date(reqMark.getDate())
                    .markCategoryStatus(markCategoryStatus(reqMark.getTotalScore()))
                    .build();
        }

        plusCoin(mark.getTotalScore(), student); //coin hisoblash uchun

        markRepository.save(mark);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> updateMark(Long markId, ReqMark reqMark){
        Mark mark = markRepository.findById(markId).orElseThrow(
                () -> new DataNotFoundException("Mark not found")
        );

        Student student = studentRepository.findById(reqMark.getStudentId()).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        if (reqMark.getMarkStatus().equals(MarkStatus.KUNLIK_BAHO)){
            int score = (reqMark.getActivityScore()+ reqMark.getHomeworkScore())/2;
            mark.setStatus(reqMark.getMarkStatus());
            mark.setHomeworkScore(reqMark.getHomeworkScore());
            mark.setActiveScore(reqMark.getActivityScore());
            mark.setDate(reqMark.getDate());
            mark.setTotalScore(score);
            mark.setMarkCategoryStatus(markCategoryStatus(score));
            mark.setStudent(student);
        } else {
            mark.setStatus(reqMark.getMarkStatus());
            mark.setActiveScore(null);
            mark.setHomeworkScore(null);
            mark.setDate(reqMark.getDate());
            mark.setTotalScore(reqMark.getTotalScore());
            mark.setStudent(student);
            mark.setMarkCategoryStatus(markCategoryStatus(reqMark.getTotalScore()));
        }

        plusCoin(mark.getTotalScore(), student);

        markRepository.save(mark);
        return ApiResponse.success(null, "Success");
    }



    public ApiResponse<String> deleteMark(Long markId){
        Mark mark = markRepository.findById(markId).orElseThrow(
                () -> new DataNotFoundException("Mark not found")
        );

        markRepository.delete(mark);
        return ApiResponse.success(null, "Success");
    }




    public ApiResponse<ResPageable> getAllMarkForAdmin(String keyword, int page, int size){
        Page<Mark> markPage = markRepository.findAllMark(keyword, PageRequest.of(page, size));

        isFoundMark(markPage.getTotalElements());

        List<ResMark> marks = markPage.getContent().stream().map(markMapper::toDTO).toList();
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(markPage.getTotalElements())
                .totalPage(markPage.getTotalPages())
                .body(marks)
                .build();
        return ApiResponse.success(resPageable, "Success");
    }



    public ApiResponse<ResPageable> getMyMarks(CustomUserDetails customUserDetails, int page, int size){
        Page<Mark> markPage;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (customUserDetails.getRole().equals(Role.ROLE_TEACHER.name())){
            User teacher = userRepository.findByPhone(customUserDetails.getPhone()).orElseThrow(
                    () -> new DataNotFoundException("Teacher not found")
            );

            markPage = markRepository.findAllByCreatedBy(teacher.getFullName(), pageRequest);
        } else {
            Student student = studentRepository.findByPhone(customUserDetails.getPhone()).orElseThrow(
                    () -> new DataNotFoundException("Student not found")
            );

            markPage = markRepository.findAllByStudentId(student.getId(), pageRequest);
        }


        isFoundMark(markPage.getTotalElements());

        List<ResMark> marks = markPage.getContent().stream().map(markMapper::toDTO).toList();
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(markPage.getTotalElements())
                .totalPage(markPage.getTotalPages())
                .body(marks)
                .build();
        return ApiResponse.success(resPageable, "Success");

    }



    public ApiResponse<ResMark> getOneMark(Long markId){
        Mark mark = markRepository.findById(markId).orElseThrow(
                () -> new DataNotFoundException("Mark not found")
        );

        return ApiResponse.success(markMapper.toFullDTO(mark), "Success");
    }




    private void plusCoin(int score, Student student){
        if (score > 7){
            student.setCoin(student.getCoin() + 3);
        } else if (score > 5) {
            student.setCoin(student.getCoin() + 2);
        } else if (score <= 4) {
            student.setCoin(student.getCoin() + 1);
        }
        studentRepository.save(student);
    }



    private MarkCategoryStatus markCategoryStatus(int score){
        if (score > 7){
            return MarkCategoryStatus.YASHIL;
        } else if (score > 5) {
            return MarkCategoryStatus.SARIQ;
        } else if (score <= 4) {
            return MarkCategoryStatus.QIZIL;
        } else {
            return MarkCategoryStatus.QIZIL;
        }
    }


    private void isFoundMark(long totalMark){
        if (totalMark == 0){
            ApiResponse.error("Mark not found");
        }
    }

}
