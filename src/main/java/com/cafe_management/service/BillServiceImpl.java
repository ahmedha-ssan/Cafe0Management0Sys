package com.cafe_management.service;

import com.cafe_management.API.ApiFillter;
import com.cafe_management.Model.Bill;
import com.cafe_management.constents.CafeConstants;
import com.cafe_management.dao.BillDao;
import com.cafe_management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService{

    @Autowired
    ApiFillter apiFillter;
    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("INSIDE GENERATE REPORT...");
        try {
            String fileName;
            if (ValidateRequestMap(requestMap)){

                if (requestMap.containsKey("isGenerate")&& !(Boolean) requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid",fileName);
                    insertBill(requestMap);
                }

                String data = "Name: "+requestMap.get("name") + "\n"+
                        "Contact Number: "+requestMap.get("contactNumber")+"\n"+
                        "Email: "+requestMap.get("email")+"\n"+
                        "Payment Method: "+requestMap.get("paymentMethod")+"\n";

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION+"\\"+fileName+".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph title = new Paragraph("Cafe Management", getFont("Header"));

                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetail"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRow(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : " + requestMap.get("total") + "\n" +
                        "Thank You For Your Visit :)",getFont("Data"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Required date not found. ",HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    private void setRectangleInPdf(Document document) throws DocumentException{
        log.info("inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,25);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private Font getFont(String type) {
        switch (type) {
            case "Header" -> {
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            }
            case "Data" -> {
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            }
            default -> {
                return new Font();
            }
        }
    }
    private void addTableHeader(PdfPTable table) {
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(col -> {
            PdfPCell head = new PdfPCell();
            head.setBackgroundColor(BaseColor.LIGHT_GRAY);
            head.setBorderWidth(2);
            head.setPhrase(new Phrase(col));
            head.setBackgroundColor(BaseColor.RED);
            head.setHorizontalAlignment(Element.ALIGN_CENTER);
            head.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(head);
        });
    }
    private void addRow(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("total")));
            bill.setProductDetail((String) requestMap.get("productDetail"));
            bill.setCreatedBy(apiFillter.getCurrentUser());
            billDao.save(bill);
        }catch (Exception x){
            x.printStackTrace();
        }
    }

    private boolean ValidateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetail") &&
                requestMap.containsKey("total");


    }

    @Override
    public ResponseEntity<List<Bill>> gatBills() {
        List<Bill> list = new ArrayList<>();
        if (apiFillter.isAdmin()){
            list = billDao.getAllBills();
        }else {
            list = billDao.getBillByUserName(apiFillter.getCurrentUser());
        }
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
           byte[] bytes= new byte[0];
           if (!requestMap.containsKey("uuid")&& ValidateRequestMap(requestMap)){
               return new ResponseEntity<>(bytes,HttpStatus.BAD_REQUEST);
           }
           String filePath= CafeConstants.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+".pdf";
           if (CafeUtils.isFileExist(filePath)){
               bytes = getByteArray(filePath);
               return new ResponseEntity<>(bytes,HttpStatus.OK);
           }else {
               requestMap.put("isGenerate",false);
               generateReport(requestMap);
               bytes = getByteArray(filePath);
               return new ResponseEntity<>(bytes,HttpStatus.OK);
           }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }



    private byte[] getByteArray(String filePath) throws Exception{
        File initFial = new File(filePath);
        InputStream targetStream = new FileInputStream(initFial);
        byte[] bytes = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return bytes;
    }
    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional = billDao.findById(id);
            if (!optional.isEmpty()){
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill deleted successfullly...",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill id does not exist",HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.BAD_REQUEST);
    }
}
