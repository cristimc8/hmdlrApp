package com.heimdallr.hmdlrapp.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFWriter {
    private String pdfOutputDirectory;
    private String pdfFileName;
    private PDDocument doc = null;
    private PDFont font = null;

    public PDFWriter(String pdfOutputDirectory, String pdfFileName) {
        this.pdfOutputDirectory = pdfOutputDirectory;
        if (!this.pdfOutputDirectory.endsWith("/"))
            this.pdfOutputDirectory += "/";
        if (!pdfFileName.endsWith(".pdf")) {
            pdfFileName = pdfFileName + ".pdf";
        }
        this.pdfFileName = pdfFileName;
    }

    public void createPdfFile() {
        doc = new PDDocument();
        font = PDType1Font.COURIER;
    }

    public void addPage(String pageHeader, StringBuffer pageText) {
        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contents = null;

        float fontSize = 12;
        float leading = 1.5f * fontSize;
        PDRectangle mediabox = page.getMediaBox();
        float margin = 75;
        float width = mediabox.getWidth() - 2 * margin;
        float startX = mediabox.getLowerLeftX() + margin;
        float startY = mediabox.getUpperRightY() - margin;
        float yOffset = startY;

        try {
            contents = new PDPageContentStream(doc, page);
            contents.beginText();
            contents.setFont(font, 14);
            contents.newLineAtOffset(startX, startY);
            yOffset -= leading;
            contents.showText(pageHeader);
            contents.newLineAtOffset(0, -leading);
            yOffset -= leading;

            List<String> lines = new ArrayList<>();
            parseIndividualLines(pageText, lines, fontSize, font, width);

            contents.setFont(font, fontSize);
            for (String line : lines) {
                contents.showText(line);
                contents.newLineAtOffset(0, -leading);
                yOffset -= leading;

                if (yOffset <= 0) {
                    contents.endText();
                    try {
                        contents.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    page = new PDPage();
                    doc.addPage(page);
                    contents = new PDPageContentStream(doc, page);
                    contents.beginText();
                    contents.setFont(font, fontSize);
                    yOffset = startY;
                    contents.newLineAtOffset(startX, startY);
                }
            }
            contents.endText();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (contents != null)
                    contents.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAndClose() {
        try {
            doc.save(pdfOutputDirectory + pdfFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                doc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseIndividualLines(StringBuffer wholeLetter, List<String> lines, float fontSize, PDFont pdfFont, float width) throws IOException {
        String[] paragraphs = wholeLetter.toString().split("\n");
        for (int i = 0; i < paragraphs.length; i++) {
            int lastSpace = -1;
            lines.add(" ");
            while (paragraphs[i].length() > 0) {
                int spaceIndex = paragraphs[i].indexOf(' ', lastSpace + 1);
                if (spaceIndex < 0) {
                    spaceIndex = paragraphs[i].length();
                }
                String subString = paragraphs[i].substring(0, spaceIndex);
                float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
                if (size > width) {
                    if (lastSpace < 0) {
                        lastSpace = spaceIndex;
                    }
                    subString = paragraphs[i].substring(0, lastSpace);
                    lines.add(subString);
                    paragraphs[i] = paragraphs[i].substring(lastSpace).trim();
                    lastSpace = -1;
                } else if (spaceIndex == paragraphs[i].length()) {
                    lines.add(paragraphs[i]);
                    paragraphs[i] = "";
                } else {
                    lastSpace = spaceIndex;
                }
            }
        }
    }
}
