package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.repository.UserRepository;
import it.cgmconsulting.myblog.repository.PostRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class XlsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public XlsService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }



    public InputStream createReport() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // creazione xls
        HSSFWorkbook workbook = new HSSFWorkbook();

        // creazione sheet
        createAuthorReport(workbook);

        workbook.write(out);
        workbook.close();

        InputStream in = new ByteArrayInputStream(out.toByteArray());

        return in;
    }



    public void createAuthorReport(HSSFWorkbook workBook){

        HSSFSheet sheet = workBook.createSheet("Author Report");

    }


}
