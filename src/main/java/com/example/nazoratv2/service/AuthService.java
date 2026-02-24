package com.example.nazoratv2.service;

import com.example.nazoratv2.configuration.TrackAction;
import com.example.nazoratv2.dto.TelegramLoginResult;
import com.example.nazoratv2.dto.request.*;
import com.example.nazoratv2.entity.enums.ActionType;
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
import org.springframework.transaction.annotation.Transactional;

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

        // student tgId bog'langanmi
        if (req.getStudentTelegramId() != null &&
                studentRepository.existsByTelegramId(req.getStudentTelegramId())) {
            return ApiResponse.error("Bu Telegram akkaunt allaqachon studentga bog'langan");
        }

        if (studentRepository.existsByPhone(req.getPhone())) {
            return ApiResponse.error("Student already exists");
        }

        Group group = groupRepository.findById(req.getGroupId()).orElseThrow(
                () -> new DataNotFoundException("Group not found")
        );

        User parent = null;

        // 1) username orqali qidiramiz
        if (req.getParentUsername() != null && !req.getParentUsername().isBlank()) {
            parent = userRepository.findByTelegramUsername(req.getParentUsername()).orElse(null);
        }

        // 2) topilmasa phone orqali qidiramiz
        if (parent == null && req.getParentPhone() != null && !req.getParentPhone().isBlank()) {
            String p = normalizePhone(req.getParentPhone());
            parent = userRepository.findByPhoneAndActiveTrue(p).orElse(null);
            req.setParentPhone(p);
        }

        // 3) parent topilmasa -> yaratamiz (phone majburiy)
        if (parent == null) {
            if (req.getParentPhone() == null || !req.getParentPhone().matches("^998\\d{9}$")) {
                return ApiResponse.error("Parent phone required. Parent contact must be phone number (998...)");
            }
            if (req.getParentName() == null || req.getParentName().isBlank()) {
                return ApiResponse.error("Parent name required");
            }

            // phone unique bo'lsin
            if (userRepository.existsByPhoneAndActiveTrue(req.getParentPhone())) {
                return ApiResponse.error("Parent phone already exists");
            }

            parent = User.builder()
                    .fullName(req.getParentName())
                    .role(Role.ROLE_PARENT)
                    .phone(req.getParentPhone())
                    .telegramUsername(req.getParentUsername()) // ✅ username saqlanadi
                    .telegramId(null) // parent keyin kirib bog'laydi
                    .password(passwordEncoder.encode(last4(req.getParentPhone())))
                    .build();

            userRepository.save(parent);
        }

        // 4) student yaratamiz
        Student student = Student.builder()
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .telegramId(req.getStudentTelegramId())
                .password(passwordEncoder.encode(last4(req.getPhone())))
                .group(group)
                .parent(parent)
                .imgUrl(req.getImgUrl())
                .build();

        studentRepository.save(student);

        return ApiResponse.success(null, "Successfully registered student");
    }

    private String normalizePhone(String s) {
        if (s == null) return null;
        return s.replace("+", "").replace(" ", "").trim();
    }

    private String last4(String phone) {
        return phone.substring(phone.length() - 4);
    }

    @Transactional
    public ApiResponse<String> linkParentTelegram(String phone,
                                                  Long telegramId,
                                                  String telegramUsername) {

        if (phone == null || phone.isBlank()) {
            return ApiResponse.error("Telefon raqam kiritilmadi");
        }

        // 1️⃣ Parentni topamiz
        Optional<User> optionalParent =
                userRepository.findByPhoneAndRole(
                        phone,
                        Role.ROLE_PARENT
                );

        if (optionalParent.isEmpty()) {
            return ApiResponse.error("Parent topilmadi");
        }

        User parent = optionalParent.get();

        // 2️⃣ Agar boshqa telegramga allaqachon bog'langan bo'lsa
        if (parent.getTelegramId() != 0 &&
                !parent.getTelegramId().equals(telegramId)) {

            return ApiResponse.error("Bu parent boshqa Telegram akkauntga bog'langan");
        }

        // 3️⃣ TelegramId boshqa userda ishlatilmaganini tekshiramiz
        boolean telegramAlreadyUsed =
                userRepository.existsByTelegramIdAndIdNot(telegramId, parent.getId());

        if (telegramAlreadyUsed) {
            return ApiResponse.error("Bu Telegram akkaunt boshqa foydalanuvchiga bog'langan");
        }

        // 4️⃣ Parentga telegramni biriktiramiz
        parent.setTelegramId(telegramId);

        if (telegramUsername != null && !telegramUsername.isBlank()) {
            parent.setTelegramUsername(telegramUsername);
        }

        userRepository.save(parent);

        return ApiResponse.success(null, "Telegram muvaffaqiyatli bog'landi");
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

    public boolean parentExist(String parentContact) {

        if (parentContact == null || parentContact.isBlank()) {
            return false;
        }

        String contact = parentContact.trim();

        // Username (@abc)
        if (contact.startsWith("@")) {
            String username = contact.substring(1);
            return userRepository.existsByTelegramUsername(username);
        }

        // Phone (998...)
        if (contact.matches("^998\\d{9}$")) {
            return userRepository.existsByPhoneAndActiveTrue(contact);
        }

        return false;
    }

}
