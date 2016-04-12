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

import edu.harvard.iq.dvn.core.study.Study;
import org.w3c.dom.Document;

public class DeletedStudyService implements java.io.Serializable {

    /** Creates a new instance of StudyService */
    public DeletedStudyService() {
    }
    
    /**
     *  Convert given XML document to a Study object.
     */
    public Study importDeletedStudy(Document doc) {
      
        return null;
    }
    
    /**
     * Convert given Study to an XML Metatadata representation, based on schema type.
    */
    public Document exportDeletedStudy(String schemaType, Study study) {
        
         return null;
   }
}
