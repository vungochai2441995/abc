package com.example.demo.controller;

import com.example.demo.exception.RecordNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.request.CreateUserRequest;
import com.example.demo.model.request.UploadForm;
import com.example.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Api(value = "User APIs")
public class UserController {
    private static String UPLOAD_DIR = System.getProperty("user.home") + "/upload";

    @Autowired
    private UserService userService;

    @ApiOperation(value="Create a user", response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 400, message="Bad request"),
            @ApiResponse(code = 500, message="Internal Server Error"),
    })
    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        UserDto user = userService.createUser(createUserRequest);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value="Update info of a user", response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 400, message="Bad request"),
            @ApiResponse(code = 500, message="Internal Server Error"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid CreateUserRequest createUserRequest, @PathVariable int id) {
        UserDto user = userService.updateUser(createUserRequest, id);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value="Delete a user by id", response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 400, message="Bad request"),
            @ApiResponse(code = 500, message="Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        UserDto user = userService.deleteUser(id);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value="Get a user by id", response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message="Not found"),
            @ApiResponse(code = 500, message="Internal Server Error"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @ApiOperation(value = "Get a list of user", response = UserDto.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 500, message="Internal Server Error")
    })
    @GetMapping("")
    public ResponseEntity<?> getListUser(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page) {
        List<UserDto> users = userService.getListUser(keyword, page);
        return ResponseEntity.ok(users);
    }

    @ApiOperation(value = "Upload file", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 500, message="Internal Server Error"),
            @ApiResponse(code = 400, message="Bad request")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@ModelAttribute("uploadForm") UploadForm form) {
        // Create folder to save file if not exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        MultipartFile fileData = form.getFileData();
        String name = fileData.getOriginalFilename();
        if (name != null && name.length() > 0) {
            try {
                // Create file
                File serverFile = new File(UPLOAD_DIR + "/" + name);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(fileData.getBytes());
                stream.close();
                return ResponseEntity.ok("Upload success");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error when uploading");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
    }

    @ApiOperation(value = "Get file")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request")
    })
    @GetMapping("/file/{filename}")
    public ResponseEntity<?> download(@PathVariable String filename) {
        File file = new File(UPLOAD_DIR + "/" + filename);
        if (!file.exists()) {
            throw new RecordNotFoundException("File not found");
        }

        UrlResource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            throw new RecordNotFoundException("File not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
