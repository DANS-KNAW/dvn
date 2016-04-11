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

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.vdc.VDC;

import javax.ejb.EJBException;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"authority,protocol,studyId"}))
public class DeletedStudy implements java.io.Serializable {

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastExportTime;
    @ManyToOne
    @JoinColumn(nullable=false)
    private VDCUser creator;
    @ManyToOne
    private VDCUser lastUpdater;
    private boolean isHarvested;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastIndexTime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition="TEXT")
    private String protocol;
    @Column(columnDefinition="TEXT")
    private String authority;
    @ManyToOne
    @JoinColumn(nullable=false)
    private Template template;
    @ManyToOne
    @JoinColumn(nullable=false)
    private VDC owner;

    public DeletedStudy() {
    }

    public DeletedStudy(VDC vdc, VDCUser creator) {
        this(vdc,creator,null);
    }

    public DeletedStudy(VDC vdc, VDCUser creator, Template initTemplate) {
        if (vdc==null) {
            throw new EJBException("Cannot create study with null VDC");
        }
        this.setOwner(vdc);
        if (initTemplate == null ){
            setTemplate(vdc.getDefaultTemplate());
        } else {
            this.setTemplate(initTemplate);

        }
        this.setCreator(creator);
        this.setLastUpdater(creator);
    }


    public String getGlobalId() {
        return protocol+":"+authority+"/"+getStudyId();
    }


    public String getPersistentURL() {
        if (this.getProtocol().equals("hdl")){
            return getHandleURL();
        } else if (this.getProtocol().equals("doi")){
            return getEZIdURL();
        } else {
            return "";
        }
    }

    private String getHandleURL() {
         return "http://hdl.handle.net/"+authority+"/"+getStudyId();
    }

    private String getEZIdURL() {
        return "http://dx.doi.org/"+authority+"/"+getStudyId();
    }

    private String studyId;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = (studyId != null ? studyId.toUpperCase() : null);
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        if (createTime==null) {
            setCreateTime(lastUpdateTime);
        }

    }

    public VDCUser getCreator() {
        return creator;
    }

    public void setCreator(VDCUser creator) {
        this.creator = creator;
    }

    public VDCUser getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(VDCUser lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public boolean isIsHarvested() {
        return isHarvested;
    }

    public void setIsHarvested(boolean isHarvested) {
        this.isHarvested = isHarvested;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Template getTemplate() {
        return this.template;
    }
    
    public void setTemplate(Template template) {
        this.template = template;
    }

    public VDC getOwner() {
        return this.owner;
    }
    
    public void setOwner(VDC owner) {
        this.owner = owner;
    }

    public String getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getAuthority() {
        return this.authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    private String harvestIdentifier;
    
    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }     

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeletedStudy)) {
            return false;
        }
        DeletedStudy other = (DeletedStudy)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    public Date getLastExportTime() {
        return lastExportTime;
    }

    public void setLastExportTime(Date lastExportTime) {
        this.lastExportTime = lastExportTime;
    }


    public Date getLastIndexTime() {
        return lastIndexTime;
    }

    public void setLastIndexTime(Date lastIndexTime) {
        this.lastIndexTime = lastIndexTime;
    }

    public StudyVersion getReleasedVersion() {
        return null;
    }
}
