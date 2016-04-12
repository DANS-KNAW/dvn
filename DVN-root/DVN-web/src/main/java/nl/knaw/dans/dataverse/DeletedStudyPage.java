/**
 * Copyright (C) 2016-2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.dataverse;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.grizzly.config.dom.NetworkListener;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import org.glassfish.internal.api.Globals;
import org.glassfish.internal.api.ServerContext;
import org.jvnet.hk2.component.Habitat;

import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.ExternalContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Named
@ConversationScoped
public class DeletedStudyPage extends VDCBaseBean implements java.io.Serializable  {
    private static Logger logger = Logger.getLogger(DeletedStudyPage.class.getCanonicalName());
    @EJB private StudyServiceLocal studyService;

    public DeletedStudyPage() {
    }

    // params
    private String globalId;
    private Long studyId;
    private int selectedIndex;

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;

    }

   public void preRenderView() {
       super.preRenderView();
       // add javascript call on each partial submit to initialize the help tips for added fields
       JavascriptContext.addJavascriptCall(getFacesContext(),"initInlineHelpTip();");

       ExternalContext externalContext = getFacesContext().getExternalContext();
       HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
   }

    public void init() {
        super.init();

        if (studyId != null) {
            if ( globalId == null  ){
                globalId = studyUI.getStudy().getGlobalId();
            }

        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            logger.severe("ERROR: in StudyPage, without a globalId or a studyId");
        }
    }

    private void initPage() {
            initPanelDisplay();
    }

    public String getCitationDate() {
        String str = "";
        if (getStudyUI().getMetadata().getProductionDate() != null) {
            str = getStudyUI().getMetadata().getProductionDate();
        }
        return str;
    }
    /**
     * Holds value of property citationInformationPanel.
     */
    private HtmlPanelGrid citationInformationPanel;

    /**
     * Getter for property citationInformationSection.
     * @return Value of property citationInformationSection.
     */
    public HtmlPanelGrid getCitationInformationPanel() {
        return this.citationInformationPanel;
    }

    /**
     * Setter for property citationInformationSection.
     * @param citationInformationPanel New value of property citationInformationSection.
     */
    public void setCitationInformationPanel(HtmlPanelGrid citationInformationPanel) {
        this.citationInformationPanel = citationInformationPanel;
    }
    /**
     * Holds value of property abstractAndScopePanel.
     */
    private HtmlPanelGrid abstractAndScopePanel;

    /**
     * Getter for property abstractAndScopePanel.
     * @return Value of property abstractAndScopePanel.
     */
    public HtmlPanelGrid getAbstractAndScopePanel() {

        return this.abstractAndScopePanel;
    }

    /**
     * Setter for property abstractAndScopePanel.
     * @param abstractAndScopePanel New value of property abstractAndScopePanel.
     */
    public void setAbstractAndScopePanel(HtmlPanelGrid abstractAndScopePanel) {
        this.abstractAndScopePanel = abstractAndScopePanel;
    }
    /**
     * Holds value of property dataAvailPanel.
     */
    private HtmlPanelGrid dataAvailPanel;

    /**
     * Getter for property dataAvail.
     * @return Value of property dataAvail.
     */
    public HtmlPanelGrid getDataAvailPanel() {
        return this.dataAvailPanel;


    }

    /**
     * Setter for property dataAvail.
     * @param dataAvailPanel New value of property dataAvail.
     */
    public void setDataAvailPanel(HtmlPanelGrid dataAvailPanel) {
        this.dataAvailPanel = dataAvailPanel;
    }
    /**
     * Holds value of property termsOfUsePanel.
     */
    private HtmlPanelGrid termsOfUsePanel;

    /**
     * Getter for property termsOfUse.
     * @return Value of property termsOfUse.
     */
    public HtmlPanelGrid getTermsOfUsePanel() {
        return this.termsOfUsePanel;


    }

    /**
     * Setter for property termsOfUse.
     * @param termsOfUsePanel New value of property termsOfUse.
     */
    public void setTermsOfUsePanel(HtmlPanelGrid termsOfUsePanel) {
        this.termsOfUsePanel = termsOfUsePanel;
    }

    private void updateDisplay(HtmlPanelGrid panel) {
        if (panel.isRendered()) {
            panel.setRendered(false);
        } else {
            panel.setRendered(true);

        }
    }

    public void updateCitationInfoDisplay(ActionEvent actionEvent) {
        studyUI.setCitationInformationPanelIsRendered(!studyUI.isCitationInformationPanelIsRendered());
    }

    public void updateAbstractScopeDisplay(ActionEvent actionEvent) {
        studyUI.setAbstractAndScopePanelIsRendered(!studyUI.isAbstractAndScopePanelIsRendered());
    }

    public void updateDataAvailDisplay(ActionEvent actionEvent) {
        studyUI.setDataAvailPanelIsRendered(!studyUI.isDataAvailPanelIsRendered());
    }

    public void updateTermsOfUseDisplay(ActionEvent actionEvent) {
        studyUI.setTermsOfUsePanelIsRendered(!studyUI.isTermsOfUsePanelIsRendered());
    }

    public void updateNotesDisplay(ActionEvent actionEvent) {
        studyUI.setNotesPanelIsRendered(!studyUI.isNotesPanelIsRendered());
    }
    /**
     * Holds value of property notesPanel.
     */
    private HtmlPanelGrid notesPanel;

    /**
     * Getter for property notesPanel.
     * @return Value of property notesPanel.
     */
    public HtmlPanelGrid getNotesPanel() {
        return this.notesPanel;
    }

    /**
     * Setter for property notesPanel.
     * @param notesPanel New value of property notesPanel.
     */
    public void setNotesPanel(HtmlPanelGrid notesPanel) {
        this.notesPanel = notesPanel;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public boolean getAbstractAndScopePanelIsEmpty() {
        if (isEmpty(studyUI.getKeywords()) && isEmpty(studyUI.getTopicClasses()) && isEmpty(studyUI.getAbstracts()) && isEmpty(studyUI.getAbstractDates()) && isEmpty(studyUI.getRelMaterials()) && isEmpty(studyUI.getRelStudies()) && isEmpty(studyUI.getOtherRefs()) && (studyUI.getMetadata().getTimePeriodCoveredStart() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredStart())) && (studyUI.getMetadata().getTimePeriodCoveredEnd() == null || isEmpty(studyUI.getMetadata().getTimePeriodCoveredEnd())) && (studyUI.getMetadata().getDateOfCollectionStart() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionStart())) && (studyUI.getMetadata().getDateOfCollectionEnd() == null || isEmpty(studyUI.getMetadata().getDateOfCollectionEnd())) && isEmpty(studyUI.getMetadata().getCountry()) && isEmpty(studyUI.getMetadata().getGeographicCoverage()) && isEmpty(studyUI.getMetadata().getGeographicUnit()) && isEmpty(studyUI.getGeographicBoundings()) && isEmpty(studyUI.getMetadata().getUnitOfAnalysis()) && isEmpty(studyUI.getMetadata().getUniverse()) && isEmpty(studyUI.getMetadata().getKindOfData())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDataAvailIsEmpty() {
        return true;
    }

    public boolean getTermsOfUseIsEmpty() {
        if (isEmpty(studyUI.getMetadata().getHarvestDVNTermsOfUse())
            && isEmpty(studyUI.getMetadata().getHarvestDVTermsOfUse())
            && !getVDCRequestBean().getVdcNetwork().isDownloadTermsOfUseEnabled()
            && !studyUI.getStudy().getOwner().isDownloadTermsOfUseEnabled()
            && isEmpty(studyUI.getMetadata().getConfidentialityDeclaration()) && isEmpty(studyUI.getMetadata().getSpecialPermissions()) && isEmpty(studyUI.getMetadata().getRestrictions()) && isEmpty(studyUI.getMetadata().getContact()) && isEmpty(studyUI.getMetadata().getCitationRequirements()) && isEmpty(studyUI.getMetadata().getDepositorRequirements()) && isEmpty(studyUI.getMetadata().getConditions()) && isEmpty(studyUI.getMetadata().getDisclaimer())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getNotesIsEmpty() {
        if (isEmpty(studyUI.getNotes())) {
            return true;
        } else {
            return false;
        }
    }

    public void initPanelDisplay() {
        // We will always have citation info,
        // so this is always rendered the first time we go to this page.
        studyUI.setCitationInformationPanelIsRendered(true);
        if (!getAbstractAndScopePanelIsEmpty()) {
            studyUI.setAbstractAndScopePanelIsRendered(true);
        }

        // When you first go to the page, if these sections contain data, these panels will ALSO be open
        // they were previously set to be closed

        if (!getDataAvailIsEmpty()) {
            studyUI.setDataAvailPanelIsRendered(true);
        }
        if (!getTermsOfUseIsEmpty()) {
            studyUI.setTermsOfUsePanelIsRendered(true);
        }
        if (!getNotesIsEmpty()) {
            studyUI.setNotesPanelIsRendered(true);
        }
    }
    /**
     * Holds value of property panelsInitialized.
     */
    private boolean panelsInitialized;

    /**
     * Getter for property panalsInitialized.
     * @return Value of property panalsInitialized.
     */
    public boolean isPanelsInitialized() {
        return this.panelsInitialized;
    }

    /**
     * Setter for property panalsInitialized.
     * @param panelsInitialized New value of property panalsInitialized.
     */
    public void setPanelsInitialized(boolean panelsInitialized) {
        this.panelsInitialized = panelsInitialized;
    }

    public StudyServiceLocal getStudyService() {
        return studyService;
    }

    public void setStudyService(StudyServiceLocal studyService) {
        this.studyService = studyService;
    }



    /**
     * Holds value of property studyUI.
     */
    private StudyUI studyUI;

    /**
     * Getter for property studyUI.
     * @return Value of property studyUI.
     */
    public StudyUI getStudyUI() {
        return this.studyUI;
    }

    /**
     * Setter for property studyUI.
     * @param studyUI New value of property studyUI.
     */
    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }


    /**
     * web statistics related
     * argument and methods
     *
     * @author wbossons
     */
    private String xff;

    public String getXff() {
        if (this.xff == null) {
            WebStatisticsSupport webstatistics = new WebStatisticsSupport();
            int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
            setXFF(webstatistics.getQSArgument("xff", headerValue));
    }
        return this.xff;
    }

    public void setXFF(String xff) {
        this.xff = xff;
    }


    private String buildSuccessMessage(String inString){
        String successMessage = new String();

        /*  -
         * When we were trying to render out the study title we tried
          to encode the illegal characters.
        try {
               successMessage = URLEncoder.encode( inString + "\"" +
               studyUI.getMetadata().getTitle() + "\", " +
               studyUI.getStudy().getGlobalId(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
               successMessage =  inString + "\"" +
               studyUI.getMetadata().getTitle() + "\", " +
               studyUI.getStudy().getGlobalId();
        }
         */
              successMessage =  inString +
               " " +
               studyUI.getStudy().getGlobalId();
        return successMessage;
    }

    private enum StudyActionRequestType {REVIEW, RELEASE};
    private StudyActionRequestType actionRequested = null;

    public String getApiMetadataUrlWithoutDataSection() {
        return null;
    }

    public String getApiMetadataUrl() {
        return null;
    }

    // The utilities below - we have to jump through all these hoops solely
    // to obtain our own HTTPS port number (!);
    // This code is based on ... from ... by ...
    // We probably want to move it into its own dedicated utility; it could
    // be useful elsewhere.

    /**
     * Get the hostname and port of the secure or non-secure http listener for the default
     * virtual server in this server instance.  The string representation will be of the
     * form 'hostname:port'.
     *
     * @param secure true if you want the secure port, false if you want the non-secure port
     * @return the 'hostname:port' combination or null if there were any errors calculating the address
     */
    public String getServerHostAndPort(boolean secure) {
        final String host = getHostName();
        final String port = getPort(secure);

        if ((host == null) || (port == null)) {
            return null;
        }

        return host + ":" + port;
    }

    /**
     * Lookup the canonical host name of the system this server instance is running on.
     *
     * @return the canonical host name or null if there was an error retrieving it
     */
    private String getHostName() {
        // this value is calculated from InetAddress.getCanonicalHostName when the AS is
        // installed.  asadmin then passes this value as a system property when the server
        // is started.
        //String myHost = System.getProperty(SystemPropertyConstants.HOST_NAME_PROPERTY);
        // Actually, no, we don't necessarily want to use the "real" name of the host!
        // Not when it may be different from the public "front" name, such as in
        // the IQSS production scenario - where you have the content switch/
        // load balancer, with the public DNS name (dvn.iq.harvard.edu), and 2
        // or more "real" servers sitting behind it (dvn-3.hmdc.harvard.edu,
        // dvn-4.hmdc.harvard.edu, etc.)

        String myHost = System.getProperty("dvn.inetAddress");

        return myHost;
    }


    /**
     * Get the http/https port number for the default virtual server of this server instance.
     * <p/>
     * If the 'secure' parameter is true, then return the secure http listener port, otherwise
     * return the non-secure http listener port.
     *
     * @param secure true if you want the secure port, false if you want the non-secure port
     * @return the port or null if there was an error retrieving it.
     */
    private String getPort(boolean secure) {
        try {
            String serverName = System.getProperty(SystemPropertyConstants.SERVER_NAME);
            if (serverName == null) {
                final ServerContext serverContext = Globals.get(ServerContext.class);
                if (serverContext != null) {
                    serverName = serverContext.getInstanceName();
                }

                if (serverName == null) {
                    return null;
                }
            }

            Habitat defaultHabitat = Globals.getDefaultHabitat();
            Config config = defaultHabitat.getInhabitantByType(Config.class).get();
            
            String[] networkListenerNames = config.getHttpService().getVirtualServerByName(serverName).getNetworkListeners().split(",");
            
            for (String listenerName : networkListenerNames) {
                if (listenerName == null || listenerName.length() == 0) {
                    continue;
                }

                NetworkListener listener = config.getNetworkConfig().getNetworkListener(listenerName.trim());

                if (secure == Boolean.valueOf(listener.findHttpProtocol().getSecurityEnabled())) {
                    return listener.getPort();
                }
            }
        } catch (Throwable t) {
            
            // error condition handled - we'll just log it and return null.
            logger.info("Configuratoin lookup: Exception occurred retrieving port configuration... " + t.getMessage());
            
        }

        return null;
    }
    
    
    
}
