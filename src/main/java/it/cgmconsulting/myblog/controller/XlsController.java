package it.cgmconsulting.myblog.controller;


import it.cgmconsulting.myblog.service.XlsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("xls")
public class XlsController {

    private final XlsService xlsService;
    public XlsController(XlsService xlsService) {
        this.xlsService = xlsService;
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createReport() {

        InputStream xlsFile = null;
        ResponseEntity<InputStreamResource> responseEntity = null;

        try {
            xlsFile = xlsService.createReport();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Method", "GET");
            headers.add("Access-Control-Allow-Header", "Content-Type");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-disposition", "inline; filename=Report.xls");

            responseEntity = new ResponseEntity<InputStreamResource>(
                    new InputStreamResource(xlsFile),
                    headers,
                    HttpStatus.OK
            );

        } catch (Exception ex) {
            responseEntity = new ResponseEntity<InputStreamResource>(
                    new InputStreamResource(null, "Error creating Report"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return responseEntity;

    }

}
