/*
 * Definição dos Controllers
 *
 * @author Luis
 *
 */

package luis.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// TODO: Refactor when available
//import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import java.lang.StringBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

@Controller
public class LoginController {
	
	// Logger
	private static final Logger LOG = LoggerFactory
			.getLogger(LoginController.class);

        // Attributes for SAML token inspection (if needed)
	private static final String samlAttributeKey = "Attribute";
	private static final String samlAttributeValue = "AttributeValue";

        @RequestMapping("/")
        public String getIndexPage() throws Exception {
            return "index.html";
        }

	@RequestMapping("/hasLogged")
	public ResponseEntity<String> landing() {
                Saml2Authentication auth = (Saml2Authentication) SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			LOG.debug("Current authentication instance from security context is null");
                        return new ResponseEntity<String>("Not ok",HttpStatus.BAD_REQUEST);
		}  
		
                return new ResponseEntity<String>("{\"user\":\""+auth.getName()+"\"}",HttpStatus.OK);
	}

        @RequestMapping("/details")
        public ResponseEntity<String> details() throws XMLStreamException {
                Saml2Authentication auth = (Saml2Authentication) SecurityContextHolder.getContext().getAuthentication();
		LOG.debug("Current authentication :"+auth);
		if (auth == null) {
			LOG.debug("Current authentication instance from security context is null");
                        return new ResponseEntity<String>("Not ok",HttpStatus.BAD_REQUEST);
		} 

                // parse SAML response XML for attributes
		
                String xmlResponse = auth.getSaml2Response();
 		// LOG.debug("Current resp :"+xmlResponse);
                Reader reader = new StringReader(xmlResponse);
                 
                XMLInputFactory factory = XMLInputFactory.newInstance(); // Or newFactory()
                XMLStreamReader xmlReader = factory.createXMLStreamReader(reader);

                StringBuilder appender = new StringBuilder();
                appender.append("{");
                boolean attributes = false;
                boolean collect = false;

                while (xmlReader.hasNext()) {
                      int event = xmlReader.next();
          
                      switch (event) {
                          case XMLStreamConstants.START_ELEMENT:
                             if (samlAttributeKey.equals(xmlReader.getLocalName()) && xmlReader.getAttributeCount() > 1) {
                             	if (!attributes)  attributes = true;
			     	else appender.append(", ");
                             	appender.append("\"");
                             	appender.append(xmlReader.getAttributeValue(1));
                             	appender.append("\":\""); 
                             	break;
			     } 
                             if (samlAttributeValue.equals(xmlReader.getLocalName())) {
				collect = true; // set for related value collect
                             	break;
			     } 
			  case XMLStreamConstants.CHARACTERS:
                               if (collect && xmlReader.hasText()) {
                         	    // LOG.debug("Current resp :"+xmlReader.getText());
                             	    appender.append(xmlReader.getText().trim());
                             	    appender.append("\"");
				    collect = false;
			       }
                               break; 

			  default:
			     break;
		      }
                }
                appender.append("}");
                
		return new ResponseEntity<String>(appender.toString(),HttpStatus.OK);
        }

}

