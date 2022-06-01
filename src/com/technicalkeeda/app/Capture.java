package com.technicalkeeda.app;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Capture
{

    public static void main(String[] args)
    {
        try
        {
            Thread.sleep(3000);
            File directory = new File("capturedImages");
            directory.mkdir();
            Robot robot = new Robot();
            String pageNumber = "";
            
            //캡처할 책의 장수를 고려해서 정해주기
            for(int i = 0; i < 200; i++)
            {
                //윈도우 크기 설정 문제인 것 같아서 100%로 바꿔서 하니까 맞다. 확대문제
                //캡처할 화면을 픽셀단위로 정해준다
                Rectangle rectangle = new Rectangle(0, 70, 1446, 2044);
                BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
                pageNumber = StringUtils.leftPad(String.valueOf(i + 1), 5, "0");
                //캡처한 파일을 저장할 곳
                File file = new File("capturedImages//capturedImage" + pageNumber + ".png");
                boolean status = ImageIO.write(bufferedImage, "png", file);
                //다음 장으로 넘기기 위한 클릭 위치
                robot.mouseMove(1410 , 1092);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                //다음 장이 전부 로딩 될때까지 기다려줘야 함. PC가 느린 경우 1초로 설정
                Thread.sleep(500);
            }

            //이미지로 PDF를 만든다
            PDDocument doc = new PDDocument();
            File[] imageList = directory.listFiles();
            for(int i = 0; i < imageList.length; i++)
            {
                // 추가할 JPG 파일 읽기
                File oneFile = new File("capturedImages//capturedImage" + pageNumber + ".png");
                InputStream inputStream = new FileInputStream(oneFile);
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                float width = bufferedImage.getWidth();
                float height = bufferedImage.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));
                doc.addPage(page);
                pageNumber = StringUtils.leftPad(String.valueOf(i + 1), 5, "0");
                PDImageXObject pdImage = PDImageXObject.createFromFile("capturedImages//capturedImage" + pageNumber + ".png", doc);
                PDPageContentStream contents = new PDPageContentStream(doc, page);
                contents.drawImage(pdImage, 0, 0, width, height);
                contents.close();
                doc.save("ebook.pdf");
            }
        }
        catch (AWTException | IOException | InterruptedException ex)
        {
            System.err.println(ex);
        }
    }
}