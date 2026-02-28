package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqMark;
import com.example.nazoratv2.dto.request.ReqMarkDTO;
import com.example.nazoratv2.dto.response.ResMark;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Mark;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.entity.enums.MarkCategoryStatus;
import com.example.nazoratv2.entity.enums.MarkStatus;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.exception.BadRequestException;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.MarkMapper;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.MarkRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final MarkMapper markMapper;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    @TrackAction(
            type = ActionType.MARK_CREATED,
            description = "Baho quyildi"
    )
    public ApiResponse<String> saveMark(ReqMark reqMark){
        Student student = studentRepository.findById(reqMark.getStudentId()).orElseThrow(
                () -> new DataNotFoundException("Student not found"));


        Mark mark;
        if (reqMark.getMarkStatus().equals(MarkStatus.KUNLIK_BAHO)){
            int activity = clampScore10(reqMark.getActivityScore(), "activityScore");
            int homework = clampScore10(reqMark.getHomeworkScore(), "homeworkScore");

            int score = (activity+ homework)/2;
            mark = Mark.builder()
                    .student(student)
                    .status(reqMark.getMarkStatus())
                    .homeworkScore(reqMark.getHomeworkScore())
                    .activeScore(activity)
                    .totalScore(homework)
                    .date(reqMark.getDate())
                    .markCategoryStatus(markCategoryStatus(score))
                    .build();
        } else {

            mark = Mark.builder()
                    .student(student)
                    .status(reqMark.getMarkStatus())
                    .homeworkScore(null)
                    .activeScore(null)
                    .totalScore(clampTotal10(reqMark.getTotalScore()))
                    .date(reqMark.getDate())
                    .markCategoryStatus(markCategoryStatus(reqMark.getTotalScore()))
                    .build();
        }

        plusCoin(mark.getTotalScore(), student); //coin hisoblash uchun

        markRepository.save(mark);
        return ApiResponse.success(null, "Success");
    }


    @TrackAction(
            type = ActionType.MARK_UPDATED,
            description = "Baho tahrirlandi")
    public ApiResponse<String> updateMark(ReqMarkDTO reqMarkDTO ){
        Mark mark = markRepository.findById(reqMarkDTO.getId()).orElseThrow(
                () -> new DataNotFoundException("Mark not found"));

        Student student = studentRepository.findById(reqMarkDTO.getStudentId()).orElseThrow(
                () -> new DataNotFoundException("Student not found"));

        LocalDate today = LocalDate.now();
        if (mark.getDate() != null && mark.getDate().isBefore(today)) {
            throw new BadRequestException("O'tgan kun bahosini o'zgartirib bo'lmaydi");
        }
        Integer oldTotal = mark.getTotalScore();

        if (reqMarkDTO.getMarkStatus().equals(MarkStatus.KUNLIK_BAHO)){

            int activity = clampScore10(reqMarkDTO.getActivityScore(), "activityScore");
            int homework = clampScore10(reqMarkDTO.getHomeworkScore(), "homeworkScore");

            int score = (activity+ homework)/2;
            mark.setStatus(reqMarkDTO.getMarkStatus());
            mark.setHomeworkScore(homework);
            mark.setActiveScore(activity);
            mark.setTotalScore(score);
            mark.setMarkCategoryStatus(markCategoryStatus(score));
            mark.setStudent(student);
        } else {
            mark.setStatus(reqMarkDTO.getMarkStatus());
            mark.setActiveScore(null);
            mark.setHomeworkScore(null);
            mark.setTotalScore(clampTotal10(reqMarkDTO.getTotalScore()));
            mark.setStudent(student);
            mark.setMarkCategoryStatus(markCategoryStatus(reqMarkDTO.getTotalScore()));
        }
        applyCoinDiff(oldTotal, mark.getTotalScore(), student);

        markRepository.save(mark);
        return ApiResponse.success(null, "Success");
    }



    public ApiResponse<String> deleteMark(Long markId){
        Mark mark = markRepository.findById(markId).orElseThrow(
                () -> new DataNotFoundException("Mark not found"));

        mark.setActive(false);
        markRepository.save(mark);
        return ApiResponse.success(null, "Success");
    }




    public ApiResponse<ResPageable> getAllMarkForAdmin(String keyword, Long groupId, int page, int size){

        Page<Mark> markPage = markRepository.findAllMark(keyword, groupId, PageRequest.of(page, size));

        isFoundMark(markPage.getTotalElements());

        List<ResMark> marks = markPage.getContent()
                .stream()
                .map(markMapper::toMarkDTO)
                .toList();

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
             userRepository.findByPhoneAndActiveTrue(customUserDetails.getPhone()).orElseThrow(
                    () -> new DataNotFoundException("Teacher not found")
            );
            String createdBy = customUserDetails.getPhone();
            markPage = markRepository.findAllByCreatedByAndActiveTrue(createdBy, pageRequest);
        } else if (customUserDetails.getRole().equals(Role.ROLE_SUPER_ADMIN.name())) {
            markPage = markRepository.findAll(pageRequest);
        } else {
            Student student = studentRepository.findByPhone(customUserDetails.getPhone()).orElseThrow(
                    () -> new DataNotFoundException("Student not found"));

            markPage = markRepository.findAllByStudentIdAndActiveTrue(student.getId(), pageRequest);
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

    public ApiResponse<ResPageable> getGroupByArchiveMarks(CustomUserDetails cud,Long groupId, String keyword, int page, int size){
        PageRequest pr = PageRequest.of(page, size);

        String createdBy = null;

        if (cud.getRole().equals(Role.ROLE_TEACHER.name())) {
            userRepository.findByPhoneAndActiveTrue(cud.getPhone())
                    .orElseThrow(() -> new DataNotFoundException("Teacher not found"));
            createdBy = cud.getPhone();
        } else if (!cud.getRole().equals(Role.ROLE_SUPER_ADMIN.name())) {
            throw new BadRequestException("Ruxsat yo'q");
        }

        LocalDate today = LocalDate.now();

        Page<Mark> markPage = markRepository.findArchiveMarksByGroup(
                groupId, today, keyword, createdBy, pr);

        isFoundMark(markPage.getTotalElements());

        List<ResMark> body = markPage.getContent()
                .stream()
                .map(markMapper::toMarkDTO)
                .toList();

        ResPageable res = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(markPage.getTotalElements())
                .totalPage(markPage.getTotalPages())
                .body(body)
                .build();

        return ApiResponse.success(res, "Success");
    }



    public ApiResponse<ResMark> getOneMark(Long markId){
        Mark mark = markRepository.findById(markId).orElseThrow(
                () -> new DataNotFoundException("Mark not found")
        );

        return ApiResponse.success(markMapper.toFullDTO(mark), "Success");
    }




    private void plusCoin(int score, Student student){
        int add = 0;

        if (score == 5) {
            add = 3;
        } else if (score == 4) {
            add = 2;
        } else if (score == 3) {
            add = 1;
        }

        if (add > 0) {
            student.setCoin(student.getCoin() + add);
            studentRepository.save(student);
        }
    }



    private MarkCategoryStatus markCategoryStatus(int score){
        if (score == 5) {
            return MarkCategoryStatus.YASHIL;
        } else if (score == 4 || score == 3) {
            return MarkCategoryStatus.SARIQ;
        } else {
            return MarkCategoryStatus.QIZIL;
        }
    }


    private void isFoundMark(long totalMark){
        if (totalMark == 0){
            ApiResponse.error("Mark not found");
        }
    }

    private int clampScore10(Integer score, String field) {
        if (score == null) throw new IllegalArgumentException(field + " is required");
        if (score < 0 || score > 5) {
            throw new IllegalArgumentException(field + " must be between 0 and 5");
        }
        return score;
    }

    private int clampTotal10(Integer total) {
        if (total == null) throw new IllegalArgumentException("totalScore is required");
        if (total < 0 || total > 100) {
            throw new IllegalArgumentException("totalScore must be between 0 and 5");
        }
        return total;
    }

    private void applyCoinDiff(Integer oldScore, Integer newScore, Student student) {
        int oldVal = oldScore == null ? 0 : oldScore;
        int newVal = newScore == null ? 0 : newScore;

        int diff = newVal - oldVal;
        if (diff != 0) {
            student.setCoin(student.getCoin() + diff);
            studentRepository.save(student);
        }
    }


}
