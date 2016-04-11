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

import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.study.MetadataFormatType;
import edu.harvard.iq.dvn.core.study.RemoteAccessAuth;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import org.apache.commons.io.FileUtils;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class DeletedStudyServiceBean implements DeletedStudyServiceLocal, java.io.Serializable {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName = "jms/DSBIngest")
    Queue queue;
    @Resource(mappedName = "jms/DSBQueueConnectionFactory")
    QueueConnectionFactory factory;
    @EJB
    GNRSServiceLocal gnrsService;
    private static final Logger logger = Logger.getLogger("nl.knaw.dans.dataverse.DeletedStudyServiceBean");

    /**
     * Creates a new instance of DeletedStudyServiceBean
     */
    public DeletedStudyServiceBean() {
    }


    public void updateDeletedStudy(DeletedStudy detachedDeletedStudy) {
        em.merge(detachedDeletedStudy);
    }

    public DeletedStudy getDeletedStudyByHarvestInfo(VDC dataverse, String harvestIdentifier) {
        String queryStr = "SELECT s FROM DeletedStudy s WHERE s.owner.id = '" + dataverse.getId() + "' and s.harvestIdentifier = '" + harvestIdentifier + "'";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();
        DeletedStudy deletedStudy = null;
        if (resultList.size() > 1) {
            throw new EJBException("More than one study found with owner_id= " + dataverse.getId() + " and harvestIdentifier= " + harvestIdentifier);
        }
        if (resultList.size() == 1) {
            deletedStudy = (DeletedStudy) resultList.get(0);
        }
        return deletedStudy;
    }

    /**
     *   Gets DeletedStudy without any of its dependent objects
     *
     */
    public DeletedStudy getDeletedStudy(Long deletedStudyId) {

        DeletedStudy deletedStudy = em.find(DeletedStudy.class, deletedStudyId);
        if (deletedStudy == null) {
            throw new IllegalArgumentException("Unknown studyId: " + deletedStudyId);
        }
        return deletedStudy;
    }

    public List getStudies() {
        String query = "SELECT s FROM DeletedStudy s ORDER BY s.id";
        return (List) em.createQuery(query).getResultList();
    }

    public List getStudiesByIdRange(long begin, long end) {
        String query = "SELECT s FROM DeletedStudy s WHERE s.id > " + begin + " AND s.id < " + end + " ORDER BY s.id";
        logger.info("query: "+query);
        try {
            return (List) em.createQuery(query).getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Long> getAllDeletedStudyIds() {
        String queryStr = "select id from deletedStudy order by id";
        Query query = em.createNativeQuery(queryStr);
        List<Long> returnList = new ArrayList<Long>();
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult).longValue()));
        }

        return returnList;
    }

    public List<Long> getAllNonHarvestedDeletedStudyIds() {
        String queryStr = "select id from deletedStudy where isharvested='false'";
        Query query = em.createNativeQuery(queryStr);
        List<Long> returnList = new ArrayList<Long>();
        for (Object currentResult : query.getResultList()) {
            // convert results into Longs
            returnList.add(new Long(((Integer)currentResult).longValue()));
        }

        return returnList;
    }

    public String generateDeletedStudyIdSequence(String protocol, String authority) {
        String deletedStudyId = null;
        deletedStudyId = gnrsService.getNewObjectId(protocol, authority);
        return deletedStudyId;
    }

    public RemoteAccessAuth lookupRemoteAuthByHost (String hostName) {
        String queryStr = "SELECT r FROM RemoteAccessAuth r WHERE r.hostName= :hostName" ;

        RemoteAccessAuth remoteAuth = null;

        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("hostName", hostName);
	    List resultList = query.getResultList();
	    if (resultList.size() > 0) {
		remoteAuth = (RemoteAccessAuth) resultList.get(0);
	    }
	} catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return remoteAuth;
    }


    public DeletedStudy getDeletedStudyByGlobalId(String identifier) {
        String protocol = null;
        String authority = null;
        String deletedStudyId = null;
        int index1 = identifier.indexOf(':');
        int index2 = identifier.indexOf('/');
        int index3 = 0;
        if (index1 == -1) {
            throw new EJBException("Error parsing identifier: " + identifier + ". ':' not found in string");
        } else {
            protocol = identifier.substring(0, index1);
        }
        if (index2 == -1) {
            throw new EJBException("Error parsing identifier: " + identifier + ". '/' not found in string");

        } else {
            authority = identifier.substring(index1 + 1, index2);
        }
        if (protocol.equals("doi")){
           index3 = identifier.indexOf('/', index2 + 1 );
           if (index3== -1){
              deletedStudyId = identifier.substring(index2 + 1).toUpperCase();
           } else {
              authority = identifier.substring(index1 + 1, index3);
              deletedStudyId = identifier.substring(index3 + 1).toUpperCase();
           }
        }  else {
           deletedStudyId = identifier.substring(index2 + 1).toUpperCase();
        }

        String queryStr = "SELECT s from DeletedStudy s where s.deletedStudyId = :deletedStudyId  and s.protocol= :protocol and s.authority= :authority";

        DeletedStudy deletedStudy = null;
        try {
            Query query = em.createQuery(queryStr);
            query.setParameter("deletedStudyId", deletedStudyId);
            query.setParameter("protocol", protocol);
            query.setParameter("authority", authority);
            deletedStudy = (DeletedStudy) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return deletedStudy;
    }

    private File transformToDDI(File xmlFile, String xslFileName) {
        File ddiFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            // prepare source
            in = new FileInputStream(xmlFile);
            StreamSource source = new StreamSource(in);

            // prepare result
            ddiFile = File.createTempFile("ddi", ".xml");
            out = new FileOutputStream(ddiFile);
            StreamResult result = new StreamResult(out);

            // now transform
            StreamSource xslSource = new StreamSource(new File(xslFileName));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
            transformer.transform(source, result);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EJBException("Error occurred while attempting to transform file: " + ex.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }


        return ddiFile;

    }

    public File transformToDDI(String xml, String xslFileName, String tmpDirPath) {
        File uploadDir = new File(tmpDirPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new EJBException("Could not create directory: " + uploadDir.getAbsolutePath());
            }
        }
        String tmpFilePath = tmpDirPath + File.separator + "study.xml";
        File tmpFile = new File(tmpFilePath);
        try {
            FileUtils.writeStringToFile(tmpFile, xml);
        } catch (IOException ex) {
            throw new EJBException("Could not write temporary file");
        } finally {
            uploadDir.delete();
        }

        File ddiFile = transformToDDI(tmpFile, xslFileName);
        tmpFile.delete();
        uploadDir.delete();
        return ddiFile;
    }

    private String generateTempTableString(List<Long> studyIds) {
        // first step: create the temp table with the ids

        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; DROP TABLE IF EXISTS tempid; END;").executeUpdate();
        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; CREATE TEMPORARY TABLE tempid (tempid integer primary key, orderby integer); END;").executeUpdate();
        em.createNativeQuery(" BEGIN; SET TRANSACTION READ WRITE; INSERT INTO tempid VALUES " + generateIDsforTempInsert(studyIds) + "; END;").executeUpdate();
        return "select tempid from tempid";
    }

    private String generateIDsforTempInsert(List idList) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        Iterator iter = idList.iterator();
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            sb.append("(").append(id).append(",").append(count++).append(")");
            if (iter.hasNext()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }

    public boolean isValidStudyIdString(String str) {
        final char[] chars = str.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if (StringUtil.isAlphaNumericChar(c) || c == '-' || c == '_' || c == '.') {
                continue;
            }
            return false;
        }
        return true;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setIndexTime(Long studyId, Date indexTime) {
        DeletedStudy deletedStudy = em.find(DeletedStudy.class, studyId);
        deletedStudy.setLastIndexTime(indexTime);
        em.merge(deletedStudy);
    }

    public Timestamp getLastUpdatedTime(Long vdcId) {
        String queryString  = "SELECT max(lastupdatetime) from study where owner_id=" + vdcId;
        Query query         = em.createNativeQuery(queryString);
        Timestamp timestamp = (Timestamp) query.getSingleResult();
        return timestamp;
    }

    public long getStudyDownloadCount(List studyIds) {
        String queryString  = "select sum(downloadcount) " +
                "from studyfileactivity  sfa " +
                "where sfa.study_id in (" + generateTempTableString(studyIds) + ")";
        Long studyDownloadCount = null;
        Query query = em.createNativeQuery(queryString);
        try {
            studyDownloadCount = (Long) query.getSingleResult();
        } catch (Exception nre) {} // empty catch; return 0

        return studyDownloadCount != null ? studyDownloadCount.longValue() : 0;
    }
    public long getStudyDownloadCount(Long studyId) {
        String queryString  = "select sum(downloadcount) " +
                "from studyfileactivity  sfa " +
                "where sfa.study_id = " + studyId;
        Long studyDownloadCount = null;
        Query query = em.createNativeQuery(queryString);
        try {
            studyDownloadCount = (Long) query.getSingleResult();
        } catch (Exception nre) {} // empty catch; return 0

        return studyDownloadCount != null ? studyDownloadCount.longValue() : 0;
    }

    public List<MetadataFormatType> findAllMetadataExportFormatTypes() {
        String queryStr = "SELECT f FROM MetadataFormatType f";
        Query query = em.createQuery(queryStr);
        return query.getResultList();
    }

    public Long getMaxStudyTableId () {
        Long lastId = null; 
        
        String queryStr = "SELECT s.id FROM Study s ORDER BY s.id DESC";
        Query query = em.createQuery(queryStr).setMaxResults(1);
        try {
            lastId = (Long)query.getSingleResult();
        } catch (Exception ex) {
            logger.info("caught exception while trying to determine the last id in the study table.");
            lastId = null; 
        }
        
        return lastId; 
    }
}
