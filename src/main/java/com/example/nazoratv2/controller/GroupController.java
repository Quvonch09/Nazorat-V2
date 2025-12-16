package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqGroup;
import com.example.nazoratv2.dto.response.ResGroup;
import com.example.nazoratv2.dto.response.ResPageable;
import com.example.nazoratv2.security.CustomUserDetails;
import com.example.nazoratv2.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;


    @PostMapping
    @Operation(description = "MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> saveGroup(@RequestBody ReqGroup reqGroup){
        return ResponseEntity.ok(groupService.saveGroup(reqGroup));
    }


    @PutMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateGroup(@PathVariable Long groupId,
                                                           @RequestBody ReqGroup reqGroup){
        return ResponseEntity.ok(groupService.updateGroup(groupId, reqGroup));
    }


    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteGroup(@PathVariable Long groupId){
        return ResponseEntity.ok(groupService.deleteGroup(groupId));
    }


    @GetMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Guruhni bittasini kurish")
    public ResponseEntity<ApiResponse<ReqGroup>> getGroup(@PathVariable Long groupId){
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Guruhni filter qilish")
    public ResponseEntity<ApiResponse<ResPageable>> getAllGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String teacherName,
                                                                @RequestParam(required = false) String roomName,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(groupService.searchGroup(userDetails,name, teacherName, roomName, page, size));
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Guruhni hammasini kurish")
    public ResponseEntity<ApiResponse<List<ResGroup>>> getAllGroup(){
        return ResponseEntity.ok(groupService.getAllGroup());
    }


    @GetMapping("/getDays")
    @Operation(description = "yearMonth = format -> yyyy-mm")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getDays(@RequestParam Long groupId,
                                                                @RequestParam YearMonth yearMonth){
        return ResponseEntity.ok(groupService.getLessonDaysForMonth(groupId, yearMonth));
    }
}
