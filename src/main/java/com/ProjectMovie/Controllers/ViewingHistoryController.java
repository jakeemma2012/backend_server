package com.ProjectMovie.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ProjectMovie.Interface.ViewingHistoryService;
import com.ProjectMovie.dto.ViewingHistoryDTO;
import com.ProjectMovie.entities.ViewingHistory;

@RestController
@RequestMapping("/api/v1/history")
public class ViewingHistoryController {

    private final ViewingHistoryService viewingHistoryService;

    public ViewingHistoryController(ViewingHistoryService viewingHistoryService) {
        this.viewingHistoryService = viewingHistoryService;
    }

    @PostMapping("/update")
    public ResponseEntity<?> addViewingHistory(@RequestBody ViewingHistoryDTO viewingHistoryDTO) {
        try {
            System.out.println("viewingHistoryDTO: " + viewingHistoryDTO);
            ViewingHistory savedViewingHistory = viewingHistoryService.saveOrUpdate(viewingHistoryDTO);
            return ResponseEntity.ok(savedViewingHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getViewingHistory(@PathVariable Long userId) {
        List<ViewingHistoryDTO> viewingHistory = viewingHistoryService.getUserHistory(userId);
        return ResponseEntity.ok(viewingHistory);
    }

}
