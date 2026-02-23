package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.TelegramLoginResult;
import com.example.nazoratv2.dto.request.*;
import com.example.nazoratv2.entity.enums.ActionType;
import com.example.nazoratv2.telegram.TelegramUserExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.StudentRepository;
import com.example.nazoratv2.repository.UserRepository;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.security.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    @Value("${telegram.bot.token}")
    String botToken;

    public ApiResponse<String> login(String phone, String password) {
        Optional<User> optionalUser = userRepository.findByPhoneAndActiveTrue(phone);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.isActive()){
                return ApiResponse.error("User is not enabled");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ApiResponse.error("Invalid password");
            }

            CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            notificationService.saveNotification(new ReqNotification("Sfera Academy xabarnomasi",
                    "Siz tizimga muvaffaqiyatli kirdingiz!", null, user.getId()));

            return ApiResponse.success(token, userDetails.getRole());
        }

        Optional<Student> optionalStudent = studentRepository.findByPhone(phone);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            if (!student.isActive()){
                return ApiResponse.error("Student is not active");
            }

            if (!passwordEncoder.matches(password, student.getPassword())) {
                return ApiResponse.error("Invalid password");
            }

            CustomUserDetails userDetails = CustomUserDetails.fromStudent(student);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            notificationService.saveNotification(new ReqNotification("Sfera Academy xabarnomasi",
                    "Siz tizimga muvaffaqiyatli kirdingiz!", student.getId(), null));
            return ApiResponse.success(token, userDetails.getRole());
        }

        return ApiResponse.error("User topilmadi");
    }

    public TelegramLoginResult login(String initData) {
        if (!com.example.nazoratv2.security.telegram.TelegramInitDataVerifier.verify(initData, botToken)) {
            throw new RuntimeException("Telegram initData verify failed");
        }

        long telegramId = TelegramUserExtractor.extractTelegramId(initData);

        // 1) User dan qidiramiz
        User user = userRepository.findByTelegramId(telegramId).orElse(null);
        if (user != null) {
            CustomUserDetails details = CustomUserDetails.fromUser(user);
            String token = jwtService.generateTokenWithTelegram(details); // pastda moslab beraman
            return new TelegramLoginResult(token, details.getRole(), details.getFullName());
        }

        // 2) Student dan qidiramiz
        Student student = studentRepository.findByTelegramId(telegramId).orElse(null);
        if (student != null) {
            CustomUserDetails details = CustomUserDetails.fromStudent(student);
            String token = jwtService.generateTokenWithTelegram(details);
            return new TelegramLoginResult(token, details.getRole(), details.getFullName());
        }

        // Topilmasa: demak hali bog‘lanmagan
        throw new RuntimeException("Bu Telegram akkaunt hali tizimga bog'lanmagan");
    }






    public ApiResponse<String> saveUser(AuthRegister authRegister, Role role){

        boolean b = userRepository.existsByPhone(authRegister.getPhone());
        if (b){
            return ApiResponse.error("Teacher already exists");
        }

        User teacher = User.builder()
                .phone(authRegister.getPhone())
                .fullName(authRegister.getFullName())
                .password(passwordEncoder.encode(authRegister.getPassword()))
                .role(role)
                .build();
        userRepository.save(teacher);
        return ApiResponse.success(null, "Successfully added user");
    }


    @TrackAction(
            type = ActionType.STUDENT_CREATED,
            description = "Student yaratildi"
    )


    public ApiResponse<String> saveStudent(ReqStudent reqStudent){

        boolean b = studentRepository.existsByPhone(reqStudent.getPhone());

        if (b){
            return ApiResponse.error("User already exists");
        }

        User parent = userRepository.findByPhoneAndRole(reqStudent.getParentPhone(), Role.ROLE_PARENT).orElseThrow(
                () -> new DataNotFoundException("Parent not found")
        );

        Group group = groupRepository.findById(reqStudent.getGroupId()).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        Student student = Student.builder()
                .fullName(reqStudent.getFullName())
                .parent(parent)
                .phone(reqStudent.getPhone())
                .password(passwordEncoder.encode(reqStudent.getPassword()))
                .group(group)
                .imgUrl(reqStudent.getImgUrl())
                .build();
        studentRepository.save(student);
        return ApiResponse.success(null, "Successfully saved student");
    }



    public ApiResponse<String> updatePassword(ReqPassword reqPassword){
        Optional<User> optionalUser = userRepository.findByPhoneAndActiveTrue(reqPassword.getPhone());

        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(reqPassword.getPassword()));
            userRepository.save(user);

            CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            return ApiResponse.success(token, "Successfully updated password");
        }

        Optional<Student> optionalStudent = studentRepository.findByPhone(reqPassword.getPhone());
        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            student.setPassword(passwordEncoder.encode(reqPassword.getPassword()));
            studentRepository.save(student);
            CustomUserDetails userDetails = CustomUserDetails.fromStudent(student);
            String token = jwtService.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole()
            );

            return ApiResponse.success(token, "Successfully updated password");
        }
        throw new DataNotFoundException("User not found");

    }


    public ApiResponse<String> registerUser(ReqStudent reqStudent){
        boolean b = userRepository.existsByPhoneAndActiveTrue(reqStudent.getParentPhone());
        if (!b){

            boolean b1 = studentRepository.existsByPhone(reqStudent.getPhone());
            if (!b1){
                User parent = User.builder()
                        .fullName(reqStudent.getParentName())
                        .role(Role.ROLE_PARENT)
                        .phone(reqStudent.getParentPhone())
                        .password(passwordEncoder.encode(reqStudent.getParentPhone().substring(8,12)))
                        .build();
                userRepository.save(parent);

                Group group = groupRepository.findById(reqStudent.getGroupId()).orElseThrow(
                        () -> new DataNotFoundException("Group not found")
                );

                Student student = Student.builder()
                        .fullName(reqStudent.getFullName())
                        .parent(parent)
                        .phone(reqStudent.getPhone())
                        .password(passwordEncoder.encode(reqStudent.getPassword()))
                        .group(group)
                        .imgUrl(reqStudent.getImgUrl())
                        .build();
                studentRepository.save(student);
                return ApiResponse.success(null, "Successfully registered user");
            }

            return ApiResponse.error("Student already exists");
        }

        return ApiResponse.error("User already exists");

    }


    public ApiResponse<String> registerFromTelegram(ReqStudentBot req) {

        // 0) TelegramId allaqachon bog'langan bo'lsa
        if (userRepository.existsByTelegramId(req.getParentTelegramId())) {
            return ApiResponse.error("Bu Telegram akkaunt allaqachon bog'langan");
        }

        boolean parentExists = userRepository.existsByPhoneAndActiveTrue(req.getParentPhone());
        if (parentExists) return ApiResponse.error("User already exists");

        boolean studentExists = studentRepository.existsByPhone(req.getPhone());
        if (studentExists) return ApiResponse.error("Student already exists");

        User parent = User.builder()
                .fullName(req.getParentName())
                .role(Role.ROLE_PARENT)
                .phone(req.getParentPhone())
                .telegramId(req.getParentTelegramId()) // ✅ muhim
                .password(passwordEncoder.encode(req.getParentPhone().substring(8,12)))
                .build();
        userRepository.save(parent);

        Group group = groupRepository.findById(req.getGroupId()).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        Student student = Student.builder()
                .fullName(req.getFullName())
                .parent(parent)
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword()))
                .group(group)
                .imgUrl(req.getImgUrl())
                .build();
        studentRepository.save(student);

        return ApiResponse.success(null, "Successfully registered user");
    }


    public ApiResponse<String> validate(Token token) {
        if (token.getToken() == null || token.getToken().trim().isEmpty()) {
            return ApiResponse.error("Token is required");
        }

        try {
            // Tokenni parsing
            Claims claims = jwtService.extractAllClaims(token.getToken());

            // Token muddati tugaganini tekshirish
            if (jwtService.isTokenExpired(token.getToken())) {
                return ApiResponse.error("Token expired");
            }

            // Token ichidagi username/phone
            String phone = claims.getSubject();
            if (phone == null || phone.isEmpty()) {
                return ApiResponse.error("Invalid token");
            }

            // DB da bormi tekshirish
            if (!userRepository.existsByPhoneAndActiveTrue(phone)) {
                throw new DataNotFoundException("User not found");
            }

            // Hammasi to‘g‘ri
            return ApiResponse.success(phone, "Token is valid");

        } catch (ExpiredJwtException e) {
            return ApiResponse.error("Token expired");
        } catch (Exception e) {
            // Shu yerda token parsingda xatolik bo‘lsa
            return ApiResponse.error("Invalid token");
        }
    }

}
