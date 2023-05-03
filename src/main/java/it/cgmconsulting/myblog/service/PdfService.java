package it.cgmconsulting.myblog.service;


import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import it.cgmconsulting.myblog.entity.Post;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfService{

	public InputStream createPdf(Post p){
		ByteArrayOutputStream out =new ByteArrayOutputStream();//creo il byte inizializzo vuoto

		PdfDocument pdf =new PdfDocument(new PdfWriter(out));//creo un documento tipo pdf dentro il quale viene scritto out
		Document document= new Document(pdf, PageSize.A4);

		//to do

		document.close();

		InputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}
}
