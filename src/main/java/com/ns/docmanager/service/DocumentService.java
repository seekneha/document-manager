package com.ns.docmanager.service;

import com.ns.docmanager.entity.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private com.ns.docmanager.repository.DocumentRepository repo;

    private static final Path UPLOAD_PATH = Paths.get("/UploadedDocs/").toAbsolutePath();

    public List<Document> getAll() {
        return repo.findAll();
    }

    public Document uploadDocuments(MultipartFile file) throws Exception {
        List<Document> documents = new ArrayList<>();
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim() == "") {
                throw new Exception("File Name is Empty");
            }
            if(!Files.exists(UPLOAD_PATH)){
                Files.createDirectory(UPLOAD_PATH);
            }
            int extIndex = originalFilename.lastIndexOf(".");
            String fileExt = originalFilename.substring(extIndex);
            String fileName = originalFilename.substring(0, extIndex);
            Document doc = new Document();
            doc.setFileName(fileName);
            doc.setExtension(fileExt);
            doc.setSize(file.getSize());
            doc.setContent(file.getInputStream().toString());
            doc.setCreatedDate(new Date());
            Files.copy(file.getInputStream(),Paths.get(UPLOAD_PATH+"/"+originalFilename).toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
            Document savedDoc = repo.save(doc);
            documents.add(savedDoc);
        } catch (Exception ex) {
            LOGGER.error("File can not be uploaded : {}", ex.getMessage());
        }

        return documents.size() > 0 ? documents.get(0) : null;
    }

    public HashMap<String, Resource> downloadFile(Integer fileId) throws FileNotFoundException {
        Optional<Document> optionalDocument = repo.findById(fileId);
        HashMap<String, Resource> map = new HashMap<>();
        try {
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                String fileName = document.getFileName()+document.getExtension();
                Path filePath = UPLOAD_PATH.resolve(fileName).normalize();
                //Paths.get(UPLOAD_PATH+fileName).resolve();
                Resource resource = null;

                resource = new UrlResource(filePath.toUri());

                if (resource.exists()) {
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(fileName);
                    map.put(mimeType, resource);
                    return map;
                } else {
                    throw new FileNotFoundException("File not found with fileName :" + fileName);
                }
            }else {
                throw new Exception("No document found with given id");
            }
            } catch(Exception e){
                throw new FileNotFoundException("File not found ");
            }

    }
}
