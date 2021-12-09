package com.ns.docmanager.controller;

import com.ns.docmanager.entity.Document;
import com.ns.tradestore.entity.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.ns.docmanager.controller.DocumentController.class);

    @Autowired
    private com.ns.docmanager.service.DocumentService service;

    @PostMapping("/uploadDocs")
    @CrossOrigin
    public ResponseEntity<Document> uploadDocs(@RequestParam("file")MultipartFile file) throws Exception
    {
        Document document = service.uploadDocuments(file);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/documents")
    @CrossOrigin
    public List<Document> getAllDocs() throws Exception
    {
        return service.getAll();
    }

    @GetMapping("/download/{id}")
    @CrossOrigin
    public ResponseEntity<Resource> download(@PathVariable Integer id) {
        try {
            HashMap<String, Resource> map = service.downloadFile(id);
            String contentType = map.keySet().stream().findFirst().get();
            Resource resource = map.values().stream().findFirst().get();
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}
