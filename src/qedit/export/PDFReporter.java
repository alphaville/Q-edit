package qedit.export;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.core.component.Feature;
import org.opentox.toxotis.core.component.qprf.QprfAuthor;
import org.opentox.toxotis.core.component.qprf.QprfReport;
import org.opentox.toxotis.core.component.qprf.QprfReportMeta;
import org.opentox.toxotis.ontology.MetaInfo;

/**
 *
 * @author Pantelis Sopasakis
 * @author Charalampos Chomenides
 */
public class PDFReporter {

    private static final String NOT_AVAILABLE = "N/A";
    private QprfReport qprfReport;
    private static final int TEXT_SIZE = 12;
    private static final int HEADING_SIZE = 18;
    private static final Font HEADING_FONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, HEADING_SIZE, Font.BOLD, new BaseColor(java.awt.Color.BLUE));
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, TEXT_SIZE, Font.BOLD);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, TEXT_SIZE, Font.NORMAL);
    private static final Font ITALICS_FONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, TEXT_SIZE, Font.ITALIC);
    private static final ArrayList<String> MONTHS = new ArrayList<String>() {

        {
            add("January");
            add("February");
            add("March");
            add("April");
            add("May");
            add("June");
            add("July");
            add("August");
            add("September");
            add("October");
            add("November");
            add("December");
        }
    };

    public PDFReporter() {
    }

    public PDFReporter(QprfReport qprfReport) {
        this.qprfReport = qprfReport;
    }

    public QprfReport getQprfReport() {
        return qprfReport;
    }

    public void setQprfReport(QprfReport qprfReport) {
        this.qprfReport = qprfReport;
    }

    public PDFObject createPdf() {
        PDFObject pdf = new PDFObject();
        QprfReportMeta reportMeta = qprfReport.getReportMeta();

        String reportTitle = "";
        if (qprfReport.getMeta() != null && qprfReport.getMeta().getTitles() != null
                && !qprfReport.getMeta().getTitles().isEmpty()) {
            reportTitle = qprfReport.getMeta().getTitles().iterator().next().getValueAsString();
        }
        Paragraph heading = new Paragraph(reportTitle,
                HEADING_FONT);
        heading.setAlignment(Element.ALIGN_CENTER);
        pdf.addElement(heading);
        pdf.addElement(new Paragraph(Chunk.NEWLINE));

        Paragraph firstParagraph = new Paragraph();
        firstParagraph.add(new Chunk("The adequacy of a prediction depends on the following conditions: a)", NORMAL_FONT));
        firstParagraph.add(new Chunk("the (Q)SAR model is scientifically valid: ", BOLD_FONT));
        firstParagraph.add(new Chunk("the scientific validity is established according to the "
                + "OECD principles for (Q)SAR validation; b)", NORMAL_FONT));
        firstParagraph.add(new Chunk("the (Q)SAR model is applicable to the query chemical: ", BOLD_FONT));
        firstParagraph.add(new Chunk("a (Q)SAR is applicable if the query chemical falls within the "
                + "defined applicability domain of the model; c)", NORMAL_FONT));
        firstParagraph.add(new Chunk("the (Q)SAR result is reliable: ", BOLD_FONT));
        firstParagraph.add(new Chunk("a valid (Q)SAR that is applied to a chemical falling "
                + "within its applicability domain provides a reliable result; d) ", NORMAL_FONT));
        firstParagraph.add(new Chunk("the (Q)SAR model "
                + "is relevant for the regulatory purpose:", BOLD_FONT));
        firstParagraph.add(new Chunk("the predicted endpoint can be used "
                + "directly or following an extrapolation, possibly in combination with other "
                + "information, for a particular regulatory purpose. ", NORMAL_FONT));
        firstParagraph.setAlignment("JUSTIFY");
        pdf.addElement(firstParagraph);
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        Paragraph secondParagraph = new Paragraph(new Chunk("A (Q)SAR prediction (model result) may be "
                + "considered adequate if it is reliable and relevant, and depending on the totality of "
                + "information available in a weight-of-evidence assessment (see Section 4 of the QPRF).", NORMAL_FONT));
        secondParagraph.setAlignment("JUSTIFY");
        pdf.addElement(secondParagraph);
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        /*
         * Section 1: Substance
         */
        Compound substance = qprfReport.getCompound();
        if (substance != null) {
            pdf.addElement(new MyParagraph(new Chunk("1. Substance", BOLD_FONT)));
            pdf.addElement(new MyParagraph(new Chunk("1.1. CAS number:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(substance.getCasrn() != null && !substance.getCasrn().isEmpty()
                    ? substance.getCasrn() : NOT_AVAILABLE, NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("1.2. EC number:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(substance.getEinecs() != null
                    ? substance.getEinecs().isEmpty() ? NOT_AVAILABLE : substance.getEinecs() : NOT_AVAILABLE, NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("1.3. Chemical Name:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(substance.getIupacName() != null && !substance.getIupacName().isEmpty()
                    ? substance.getIupacName() : NOT_AVAILABLE, NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("1.4. Structural Formula:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(NOT_AVAILABLE, NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("1.5. Structure Codes:", BOLD_FONT)).applyIndent(10));
            try {
                Image image = Image.getInstance(substance.getDepiction(null).getImage(), java.awt.Color.CYAN);
                image.scalePercent((float) 50.00);
                pdf.addElement(image);
            } catch (BadElementException ex) {
                Logger.getLogger(PDFReporter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDFReporter.class.getName()).log(Level.SEVERE, null, ex);
            }
            pdf.addElement(new MyParagraph(new Chunk("a. SMILES:", BOLD_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk(substance.getSmiles() != null
                    ? substance.getSmiles() : NOT_AVAILABLE, NORMAL_FONT)).applyIndent(40));
            pdf.addElement(new MyParagraph(new Chunk("b. InChI:", BOLD_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk(substance.getInchi() != null
                    ? substance.getInchi() : NOT_AVAILABLE, NORMAL_FONT)).applyIndent(40));
            /*
             * Other structural representation goes here
             */
            Anchor anchor = new Anchor(new Chunk(substance.getUri().toString(), NORMAL_FONT));
            anchor.setReference(substance.getUri().toString());
            pdf.addElement(new MyParagraph(new Chunk("c. Other structural representation:",
                    BOLD_FONT)).applyIndent(20));
            MyParagraph additionalRepresentations = new MyParagraph(new Chunk("SDF, MOL, "
                    + "and CML-formatted "
                    + "representations of this compound are available from the online location ",
                    NORMAL_FONT));
            additionalRepresentations.add(anchor);
            additionalRepresentations.applyIndent(40);
            pdf.addElement(additionalRepresentations);

            if (reportMeta.getStereoFeatures() != null) {
                if (!reportMeta.getStereoFeatures().isEmpty()) {
                    pdf.addElement(new MyParagraph(new Chunk("d. Stereochemical Features:",
                            BOLD_FONT)).applyIndent(20));
                    pdf.addElement(new MyParagraph(new Chunk(reportMeta.getStereoFeatures(),
                            NORMAL_FONT)).applyIndent(40));
                }
            }
            pdf.addElement(new Paragraph(Chunk.NEWLINE));
            pdf.addElement(new Paragraph(Chunk.NEWLINE));
        }
        /*
         * Section 2: General Info
         */
        pdf.addElement(new MyParagraph(new Chunk("2. General Information", BOLD_FONT)));
        pdf.addElement(new MyParagraph(new Chunk("Date of QPRF Report:", BOLD_FONT)).applyIndent(10));


        Long reportDate = qprfReport.getReportDate();
        Calendar cal = Calendar.getInstance();
        int reportYear = 0;
        String reportMonth = null;
        int reportDay = 0;
        if (reportDate != null) {
            java.util.Date reportJDate = new java.util.Date(reportDate);
            cal.setTime(reportJDate);
            reportDay = (cal.get(Calendar.DAY_OF_MONTH));
            reportMonth = MONTHS.get(cal.get(Calendar.MONTH));
            reportYear = (cal.get(Calendar.YEAR));
        }

        pdf.addElement(new MyParagraph(reportYear + ", " + "" + reportMonth
                + " " + reportDay, NORMAL_FONT).applyIndent(20));
        pdf.addElement(new MyParagraph(new Chunk("2.1. QPRF Author and Contact Details:",
                BOLD_FONT)).applyIndent(10));
        // Authors:
        for (QprfAuthor author : qprfReport.getAuthors()) {
            if (author != null) {
                String authorString = author.getFirstName() + " " + author.getLastName();
                if (author.getAffiliation() != null && !author.getAffiliation().isEmpty()) {
                    authorString += " (" + author.getAffiliation() + ")";
                }

                if (author.getEmail() != null && !author.getEmail().isEmpty()) {
                    authorString += ", e-mail: " + author.getEmail();
                }
                if (author.getURL() != null && !author.getURL().isEmpty()) {
                    authorString += ", URL: " + author.getURL();
                }
                if (author.getAddress() != null && !author.getAddress().isEmpty()) {
                    authorString += ", Address: " + author.getAddress();
                }

                pdf.addElement(new MyParagraph(new Chunk(authorString, NORMAL_FONT)).applyIndent(20));
            }
        }
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));

        /*
         * Section 3: Prediction
         */
//        if (qprfReport.getModel() != null) {
        pdf.addElement(new MyParagraph(new Chunk("3. Prediction", BOLD_FONT)));
        pdf.addElement(new MyParagraph(new Chunk("3.1. EndPoint (OECD Principle 1)", BOLD_FONT)).applyIndent(10));
        pdf.addElement(new MyParagraph(new Chunk("a. Endpoint", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("b. Dependent Variable:", BOLD_FONT)).applyIndent(30));

        // Dependent feature
        Feature df = qprfReport.getModel().getDependentFeatures().iterator().next();
        String dfTitle = NOT_AVAILABLE;
        if (df.getMeta() != null && df.getMeta().getTitles() != null
                && !df.getMeta().getTitles().isEmpty()) {
            dfTitle = df.getMeta().getTitles().iterator().next().getValueAsString();
        }
        pdf.addElement(new MyParagraph(new Chunk(dfTitle,
                NORMAL_FONT)).applyIndent(30));

        pdf.addElement(new Paragraph(Chunk.NEWLINE));

        pdf.addElement(new MyParagraph(new Chunk("3.2. Algorithm (OECD Principle 2)",
                BOLD_FONT)).applyIndent(10));
        pdf.addElement(new MyParagraph(new Chunk("a. Model or Submodel Name",
                BOLD_FONT)).applyIndent(30));

        String modelTitle = qprfReport.getModel().getUri().toString();
        MetaInfo modelMI = qprfReport.getModel().getMeta();
        if (modelMI != null && modelMI.getTitles() != null
                & !modelMI.getTitles().isEmpty()) {
            modelTitle = modelMI.getTitles().iterator().next().getValueAsString();
        }
        pdf.addElement(new MyParagraph(new Chunk(modelTitle, NORMAL_FONT)).applyIndent(30));

        pdf.addElement(new MyParagraph(new Chunk("b. Model Version", BOLD_FONT)).applyIndent(30));

        Long modelInfoDate = qprfReport.getReportDate();
        int mvYear = 0;
        String mvMonth = null;
        int mvDay = 0;
        if (modelInfoDate != null) {
            java.util.Date mvJDate = new java.util.Date(modelInfoDate);
            cal.setTime(mvJDate);
            mvDay = (cal.get(Calendar.DAY_OF_MONTH));
            mvMonth = MONTHS.get(cal.get(Calendar.MONTH));
            mvYear = (cal.get(Calendar.YEAR));
        }

        pdf.addElement(new MyParagraph(new Chunk("Date : " + mvYear + ", "
                + mvMonth + " " + mvDay, NORMAL_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("Version Info : " + reportMeta.getModelVersion(),
                NORMAL_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("c. Reference to QMRF", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getQMRFReportReference(),
                NORMAL_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("d. Predicted Value (model result)", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk(qprfReport.getPredictionResult() + " "
                + qprfReport.getPredResultUnits(), NORMAL_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("e. Predicted Value (comments)", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getSec_3_2_e(),
                NORMAL_FONT)).applyIndent(30));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));

        pdf.addElement(new MyParagraph(new Chunk("3.3. Applicability Domain (OECD Principle 3)",
                BOLD_FONT)).applyIndent(10));
        pdf.addElement(new MyParagraph(new Chunk("a. Domains", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new MyParagraph(new Chunk("i. descriptor domain", ITALICS_FONT)).applyIndent(40));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getDescriptorDomain(), NORMAL_FONT)).applyIndent(40));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new MyParagraph(new Chunk("ii. structural fragment domain", ITALICS_FONT)).applyIndent(40));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getStructuralDomain(), NORMAL_FONT)).applyIndent(40));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new MyParagraph(new Chunk("iii. mechanism domain", ITALICS_FONT)).applyIndent(40));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getMechanismDomain(), NORMAL_FONT)).applyIndent(40));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new MyParagraph(new Chunk("iv. metabolic domain, if relevant", ITALICS_FONT)).applyIndent(40));
        pdf.addElement(new MyParagraph(new Chunk(reportMeta.getMetabolicDomain(), NORMAL_FONT)).applyIndent(40));

        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new MyParagraph(new Chunk("b. Structural Analogues", BOLD_FONT)).applyIndent(30));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        PdfPTable structuralAnalogues = new PdfPTable(4);
        structuralAnalogues.setHorizontalAlignment(Element.ALIGN_CENTER);
        try {
            structuralAnalogues.setWidths(new int[]{100, 150, 170, 170});
        } catch (DocumentException ex) {
            Logger.getLogger(PDFReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        structuralAnalogues.addCell(new PdfPCell(new Phrase("CAS", BOLD_FONT)));
        structuralAnalogues.addCell(new PdfPCell(new Phrase("Structure", BOLD_FONT)));
        structuralAnalogues.addCell(new PdfPCell(new Phrase("SMILES", BOLD_FONT)));
        structuralAnalogues.addCell(new PdfPCell(new Phrase("Exp. Value", BOLD_FONT)));
        List<Compound> analogues = qprfReport.getStructuralAnalogues();

        /*
         * 
         * 
         * Structural Analogues
         */

        if (analogues != null && !analogues.isEmpty()) {
            ArrayList<String> experimentalValues = qprfReport.getExperimentalValues();
            boolean expValuesPresent = experimentalValues != null && !experimentalValues.isEmpty();
            Iterator<String> experimentalValuesIterator = null;
            if (expValuesPresent) {
                experimentalValuesIterator = experimentalValues.iterator();
            }
            for (Compound anal : analogues) {
                structuralAnalogues.addCell(new PdfPCell(new Phrase(anal.getCasrn(), NORMAL_FONT)));
                if (anal.getDepiction(null) != null) {
                    try {
                        Image my = Image.getInstance(anal.getDepiction(null).getImage(), java.awt.Color.BLACK);
                        PdfPCell imageCell = new PdfPCell(my, true);
                        imageCell.setPadding(1);
                        structuralAnalogues.addCell(imageCell);
                    } catch (BadElementException ex) {
                        Logger.getLogger(PDFReporter.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PDFReporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {// No Image available
                    PdfPCell noImage = new PdfPCell(new Phrase("No Image", NORMAL_FONT));
                    noImage.setHorizontalAlignment(Element.ALIGN_CENTER);
                    structuralAnalogues.addCell(noImage);
                }
                structuralAnalogues.addCell(new PdfPCell(new Phrase(anal.getSmiles(), NORMAL_FONT)));
                if (expValuesPresent) {
                    structuralAnalogues.addCell(new PdfPCell(new Phrase(experimentalValuesIterator.next(), NORMAL_FONT)));
                } else {
                    structuralAnalogues.addCell(new PdfPCell(new Phrase("", NORMAL_FONT)));
                }
            }
            pdf.addElement(structuralAnalogues);
        }
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        pdf.addElement(new Paragraph(Chunk.NEWLINE));
        /*
         * Section 4: Adequacy Information
         */

        boolean anyAdequacyInfo = reportMeta != null && ((reportMeta.getSec_4_1() != null && !reportMeta.getSec_4_1().isEmpty())
                || (reportMeta.getSec_4_2() != null && !reportMeta.getSec_4_2().isEmpty())
                || (reportMeta.getSec_4_3() != null && !reportMeta.getSec_4_3().isEmpty())
                || (reportMeta.getSec_4_4() != null && !reportMeta.getSec_4_4().isEmpty()));

        if (anyAdequacyInfo) {
            pdf.addElement(new MyParagraph(new Chunk("4. Adequacy", BOLD_FONT)));
            pdf.addElement(new MyParagraph(new Chunk("The information provided in this section might be useful, "
                    + "depending on the reporting needs and formats of the regulatory framework of interest.",
                    NORMAL_FONT)).justify());
            pdf.addElement(new MyParagraph(new Chunk("This information aims to facilitate "
                    + "considerations about the adequacy of the (Q)SAR prediction (result) estimate. "
                    + "A (Q)SAR prediction may or may not be considered adequate (“fit-for-purpose”),"
                    + " depending on whether the prediction is sufficiently reliable and relevant in relation "
                    + "to the particular regulatory purpose. The adequacy of the prediction also depends on the "
                    + "availability of other information, and is determined in a weight-of-evidence assessment.",
                    NORMAL_FONT)).justify());
            pdf.addElement(new Paragraph(Chunk.NEWLINE));
            pdf.addElement(new MyParagraph(new Chunk("4.1. Regulatory Purpose:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(reportMeta.getSec_4_1(), NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("4.2. Approach for regulatory interpretation of the model result:",
                    BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(reportMeta.getSec_4_2(), NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("4.3. Outcome:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(reportMeta.getSec_4_3(), NORMAL_FONT)).applyIndent(20));
            pdf.addElement(new MyParagraph(new Chunk("4.4. Conclusion:", BOLD_FONT)).applyIndent(10));
            pdf.addElement(new MyParagraph(new Chunk(reportMeta.getSec_4_4(), NORMAL_FONT)).applyIndent(20));
        }
        return pdf;
    }

    private static class MyParagraph extends Paragraph {

        public MyParagraph(String string) {
            super(string);
        }

        public MyParagraph(Chunk chunk) {
            super(chunk);
        }

        public MyParagraph() {
            super();
        }

        public MyParagraph(String string, Font font) {
            super(string, font);
        }

        public MyParagraph justify() {
            this.setAlignment(Element.ALIGN_JUSTIFIED);
            return this;
        }

        public MyParagraph applyIndent(float ind) {
            this.setIndentationLeft(ind);
            this.setAlignment(Element.ALIGN_JUSTIFIED);
            return this;
        }
    }
}
