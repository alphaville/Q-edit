package qedit.task;

import javax.swing.DefaultListModel;
import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.util.spiders.CompoundSpider;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 * Download info for a given compound
 * @author chung
 */
public class CompoundInfo extends AbstractTask {

    private ReportIF intFrame;
    private String keyword;

    public CompoundInfo(ReportIF intFrame, String keyword, java.awt.Component busyComponent) {
        this.intFrame = intFrame;
        setTaskName("Downloading Compound Info");
        super.busyComponent = busyComponent;
        this.keyword = keyword;
    }

    @Override
    protected Object doInBackground() throws Exception {
        intFrame.deleteCompoundFields();
        intFrame.getLoadCompoundButton().setEnabled(false);
        intFrame.getCompDetailsButton().setEnabled(false);
        intFrame.getStereoFeaturesButton().setEnabled(false);
        intFrame.getRefreshDepictionButton().setEnabled(false);
        CompoundSpider spider = null;
        try {
            spider = new CompoundSpider(keyword,
                    "http://apps.ideaconsult.net:8080/ambit2/query/compound/%s/all");
        } catch (Exception ex) {
            throw new Exception("asf");
        }
        Compound compound = spider.parse();
        compound.getConformers();
        intFrame.getReport().setCompound(compound);
        printOutResults(compound);
        intFrame.updateCompoundFields();
        intFrame.getSynonymsList().setModel(new DefaultListModel());
        intFrame.getCompDetailsButton().setEnabled(true);
        intFrame.getStereoFeaturesButton().setEnabled(true);
        if (!compound.getSynonyms().isEmpty()) {
            for (String synonym : compound.getSynonyms()) {
                ((DefaultListModel) intFrame.getSynonymsList().getModel()).addElement(synonym.trim());
            }
        } else {
            QEditApp.getView().getStatusLabel().setText("Failed to load synonyms...");
        }        
        return new Object();
    }

    @Override
    protected void finished() {
        intFrame.getLoadCompoundButton().setEnabled(true);   
        super.finished();
    }
    
    

    private void printOutResults(Compound compound) {
        System.out.println("Compound found with URI : " + compound.getUri());
        System.out.println("URI    : " + compound.getUri());
        System.out.println("Smiles : " + compound.getSmiles());
        System.out.println("CAS-RN : " + compound.getCasrn());
        System.out.println("EINECS : " + compound.getEinecs());
        System.out.println("Regist : " + compound.getRegistrationDate());
        System.out.println("InChi  : " + compound.getInchi());
        System.out.println("InChiKey : " + compound.getInchiKey());
        System.out.println("IUPAC  : " + compound.getIupacName());
        System.out.println("Depict : " + (compound.getDepiction(null) != null ? "Present" : "Absent"));
    }
}
