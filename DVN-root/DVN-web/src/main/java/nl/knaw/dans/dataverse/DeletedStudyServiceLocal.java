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

import edu.harvard.iq.dvn.core.study.MetadataFormatType;
import edu.harvard.iq.dvn.core.study.RemoteAccessAuth;
import edu.harvard.iq.dvn.core.vdc.VDC;

import javax.ejb.Local;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface DeletedStudyServiceLocal extends java.io.Serializable {

    /**
     * Add given DeletedStudy to persistent storage.
     */
    public DeletedStudy getDeletedStudy(Long DeletedStudyId);

    public DeletedStudy getDeletedStudyByGlobalId(String globalId);

    public DeletedStudy getDeletedStudyByHarvestInfo(VDC vdc, String harvestIdentifier);

    public List getStudies();

    public List<Long> getAllNonHarvestedDeletedStudyIds();

    public RemoteAccessAuth lookupRemoteAuthByHost(String remoteHost);

    String generateDeletedStudyIdSequence(String protocol, String authority);

    public List<Long> getAllDeletedStudyIds();

    public void setIndexTime(Long DeletedStudyId, Date indexTime);

    public Timestamp getLastUpdatedTime(Long vdcId);

    public List<MetadataFormatType> findAllMetadataExportFormatTypes();

    public File transformToDDI(String toString, String stylesheetFileName, String tmpDirPath);

}