package it.cgmconsulting.myblog.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import it.cgmconsulting.myblog.payload.response.PostPdfResponse;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
//import it.cgmconsulting.myblog.payload.response.PostPdfResponse;

@Service
public class PdfService{

	public InputStream createPdf(PostPdfResponse p){
		ByteArrayOutputStream out =new ByteArrayOutputStream();//creo il byte inizializzo vuoto

		PdfDocument pdf =new PdfDocument(new PdfWriter(out));//creo un documento tipo pdf dentro il quale viene scritto out
		Document document= new Document(pdf, PageSize.A4);
         String ll
		//to do
		//riportare nel pdf: title, image, content, author(username), updatedAt
		//cercare di aggiungere anche metadati al pdf )text 7


		document.close();

		InputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}
}
