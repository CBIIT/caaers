<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

  <xsl:output method="xml"/>

  <xsl:attribute-set name="sub-head"><xsl:attribute name="height">10mm</xsl:attribute><xsl:attribute name="font-family">arial</xsl:attribute><xsl:attribute name="font-size">8pt</xsl:attribute><xsl:attribute name="font-weight">bold</xsl:attribute><xsl:attribute name="text-decoration">underline</xsl:attribute><xsl:attribute name="color">black</xsl:attribute></xsl:attribute-set>
  <xsl:attribute-set name="label"><xsl:attribute name="height">1mm</xsl:attribute><xsl:attribute name="font-family">arial</xsl:attribute><xsl:attribute name="font-size">8pt</xsl:attribute><xsl:attribute name="color">black</xsl:attribute></xsl:attribute-set>
  <xsl:attribute-set name="value"><xsl:attribute name="height">1mm</xsl:attribute><xsl:attribute name="font-family">arial</xsl:attribute><xsl:attribute name="font-size">8pt</xsl:attribute><xsl:attribute name="font-weight">bold</xsl:attribute></xsl:attribute-set>
  <xsl:attribute-set name="normal"><xsl:attribute name="height">1mm</xsl:attribute><xsl:attribute name="font-family">arial</xsl:attribute><xsl:attribute name="font-size">8pt</xsl:attribute></xsl:attribute-set>
  <xsl:attribute-set name="tr-height-1"><xsl:attribute name="height">4mm</xsl:attribute><xsl:attribute name="color">black</xsl:attribute></xsl:attribute-set>


  <xsl:template match="/">

	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<fo:layout-master-set>
		  	<fo:simple-page-master master-name="A4" margin-left="2mm" margin-top="2mm" margin-right="0.25in" margin-bottom="10mm">
		    	<fo:region-body margin-top="3in" margin-bottom="1.2in"/>
		    	<fo:region-before extent="3in"/>
		    	<fo:region-after extent="1.2in"/>
		  	</fo:simple-page-master>
		</fo:layout-master-set>

		<fo:page-sequence master-reference="A4">

		<fo:static-content flow-name="xsl-region-after">
				<fo:block font-size="8pt" font-family="arial" text-align-last="center" >
						<xsl:value-of select="/AdverseEventReport/Report/ReportDefinition/footer" />
				</fo:block>
				<fo:block font-size="8pt" font-family="arial" text-align-last="right" >
						Page <fo:page-number/> of <fo:page-number-citation ref-id="content_terminator"/>
				</fo:block>
		</fo:static-content>

		  <fo:static-content flow-name="xsl-region-before">
		    <fo:table>
				<fo:table-column column-width="25%"/>
				<fo:table-column column-width="50%"/>
				<fo:table-column column-width="25%"/>
				<fo:table-body>
				  <fo:table-row>
				    <fo:table-cell><fo:block><!--<fo:external-graphic src="url(http://www.hhs.gov/images/system/hlogo.gif)" content-height="3em" content-width="3em"/>--></fo:block></fo:table-cell>
				    <fo:table-cell>
				        <fo:block font-weight="bold" font-size="12pt" font-family="arial" text-align-last="center" display-align="center"><xsl:value-of select="/AdverseEventReport/Report/ReportDefinition/header" /></fo:block>
				    </fo:table-cell>
				    <fo:table-cell font-weight="bold" font-size="8pt" font-family="arial" text-align-last="left"><fo:block></fo:block></fo:table-cell>
				  </fo:table-row>
				  <fo:table-row>
				    <fo:table-cell>
				      <fo:block font-weight="bold" font-size="8pt" font-family="arial" text-align-last="left">
				      	Run Date :
    					<xsl:value-of select="java:format(java:java.text.SimpleDateFormat.new ('MM/d/yyyy h:mm:ss a '), java:java.util.Date.new())"/>
				      </fo:block>
				    </fo:table-cell>

				    <fo:table-cell><fo:block font-weight="bold" font-size="14pt" font-family="arial" text-align-last="center" display-align="center"><xsl:value-of select="/AdverseEventReport/Report/ReportDefinition/label" /></fo:block></fo:table-cell>
				  </fo:table-row>
				</fo:table-body>

				</fo:table>

                <fo:block space-after="5pt"><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px" color="black"/></fo:block>

				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Study ID : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/Identifier/value"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Study Title : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/shortTitle"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Study Subject ID : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/studySubjectIdentifier"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Subject organization : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Organization/name"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Report ID : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Report/id"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Amendment # : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Report/ReportVersion/reportVersionId"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Ticket # : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Report/assignedIdentifer"/></fo:inline></fo:block>
				<fo:block margin-left="14mm"><fo:inline xsl:use-attribute-sets="label">Date submited : </fo:inline><fo:inline xsl:use-attribute-sets="value"></fo:inline></fo:block>

                <fo:block space-after="5pt"><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

		  </fo:static-content>

		  <fo:flow flow-name="xsl-region-body">

              <!-- REPORTER INFORMATION TABLE   START -->
              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.lastName'] or
                /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[phone]'] or
                /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[fax]'] or
                /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[e-mail]']
                /AdverseEventReport/Report[applicableField='physician.lastName'] or
                /AdverseEventReport/Report[applicableField='physician.contactMechanisms[phone]'] or
                /AdverseEventReport/Report[applicableField='physician.contactMechanisms[fax]'] or
                /AdverseEventReport/Report[applicableField='physician.contactMechanisms[e-mail]']">

		  		<fo:block xsl:use-attribute-sets="sub-head" >Reporter Information</fo:block>
		  		<fo:block><xsl:text disable-output-escaping="yes"> </xsl:text></fo:block>
		  		<fo:table>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="5%"/>
					<fo:table-column column-width="18%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="25%"/>

		  			<fo:table-body>

                          <!--REPORTER DATA    START -->
                          <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.lastName'] or
                            /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[phone]'] or
                            /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[fax]'] or
                            /AdverseEventReport/Report[applicableField='reporter.contactMechanisms[e-mail]']">
                                        <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.lastName']">
                                            <fo:table-row>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Reporter Name :</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/firstName"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="AdverseEventReport/Reporter/lastName"/></fo:block></fo:table-cell>
                                            </fo:table-row>
                                        </xsl:if>

                                        <fo:table-row>
                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[phone]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Phone :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'phone'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>

                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[fax]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Fax :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'fax'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>

                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[e-mail]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Email :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'e-mail'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>
                                              <fo:table-cell><fo:block /></fo:table-cell>
                                      </fo:table-row>

                                          <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.street'] or
                                          /AdverseEventReport/Report[applicableField='reporter.address.zip'] or
                                          /AdverseEventReport/Report[applicableField='reporter.address.state'] or
                                          /AdverseEventReport/Report[applicableField='reporter.address.city']">
                                        <fo:table-row>
                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.street']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Street :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/address/street"></xsl:value-of></fo:block></fo:table-cell>
                                              </xsl:if>
                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.city'] or /AdverseEventReport/Report[applicableField='reporter.address.state'] ">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >City, State :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/address/city"></xsl:value-of></fo:block><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Reporter/address/state"></xsl:value-of></fo:block></fo:table-cell>
                                              </xsl:if>
                                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.zip']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Zip :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/address/zip"></xsl:value-of></fo:block></fo:table-cell>
                                              </xsl:if>
                                              <fo:table-cell><fo:block /></fo:table-cell>
                                      </fo:table-row>
                                          </xsl:if>

                                      <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block></fo:table-cell></fo:table-row>

                                      <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.lastName']">
                                            <fo:table-row>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" space-before="5mm">Submitter Name :</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Reporter/firstName"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="AdverseEventReport/Reporter/lastName"/></fo:block></fo:table-cell>
                                            </fo:table-row>
                                      </xsl:if>
                                      <fo:table-row>
                                            <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[phone]']">
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Phone :</fo:block></fo:table-cell>
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'phone'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                            </xsl:if>

                                            <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[fax]']">
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Fax :</fo:block></fo:table-cell>
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'fax'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                            </xsl:if>

                                            <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.contactMechanisms[e-mail]']">
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Email :</fo:block></fo:table-cell>
                                                  <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism"><xsl:if test="key = 'e-mail'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                            </xsl:if>
                                            <fo:table-cell><fo:block /></fo:table-cell>
                                    </fo:table-row>
                              
                              <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.street'] or
                              /AdverseEventReport/Report[applicableField='reporter.address.zip'] or
                              /AdverseEventReport/Report[applicableField='reporter.address.state'] or
                              /AdverseEventReport/Report[applicableField='reporter.address.city']">
                            <fo:table-row>
                                  <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.street']">
                                        <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Street :</fo:block></fo:table-cell>
                                        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/address/street"></xsl:value-of></fo:block></fo:table-cell>
                                  </xsl:if>
                                  <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.city'] or /AdverseEventReport/Report[applicableField='reporter.address.state'] ">
                                        <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >City, State :</fo:block></fo:table-cell>
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Reporter/address/city"></xsl:value-of></fo:block><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Reporter/address/state"></xsl:value-of></fo:block></fo:table-cell>
                                  </xsl:if>
                                  <xsl:if test="/AdverseEventReport/Report[applicableField='reporter.address.zip']">
                                        <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Zip :</fo:block></fo:table-cell>
                                        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Reporter/address/zip"></xsl:value-of></fo:block></fo:table-cell>
                                  </xsl:if>
                                  <fo:table-cell><fo:block /></fo:table-cell>
                          </fo:table-row>
                              </xsl:if>
                                    <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block></fo:table-cell></fo:table-row>
                          </xsl:if>
                          <!--REPORTER DATA    END -->

                          <!--PHYSICIAN DATA    START -->
                          <xsl:if test="/AdverseEventReport/Report[applicableField='physician.lastName'] or
                            /AdverseEventReport/Report[applicableField='physician.contactMechanisms[phone]'] or
                            /AdverseEventReport/Report[applicableField='physician.contactMechanisms[fax]'] or
                            /AdverseEventReport/Report[applicableField='physician.contactMechanisms[e-mail]']">
                              
                                        <xsl:if test="/AdverseEventReport/Report[applicableField='physician.lastName']">
                                            <fo:table-row>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Physician Name :</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Physician/firstName"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="AdverseEventReport/Physician/lastName"/></fo:block></fo:table-cell>
                                            </fo:table-row>
                                        </xsl:if>

                                        <fo:table-row>
                                              <xsl:if test="/AdverseEventReport/Report[applicableField='physician.contactMechanisms[phone]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Phone :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Physician/ContactMechanism"><xsl:if test="key = 'phone'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>

                                              <xsl:if test="/AdverseEventReport/Report[applicableField='physician.contactMechanisms[fax]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Fax :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Physician/ContactMechanism"><xsl:if test="key = 'fax'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>

                                              <xsl:if test="/AdverseEventReport/Report[applicableField='physician.contactMechanisms[e-mail]']">
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Email :</fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:for-each select="AdverseEventReport/Physician/ContactMechanism"><xsl:if test="key = 'e-mail'"><xsl:value-of select="value"/></xsl:if></xsl:for-each></fo:block></fo:table-cell>
                                              </xsl:if>
                                              <fo:table-cell><fo:block /></fo:table-cell>
                                      </fo:table-row>

                              <xsl:if test="/AdverseEventReport/Report[applicableField='physician.address.street'] or
                              /AdverseEventReport/Report[applicableField='physician.address.zip'] or
                              /AdverseEventReport/Report[applicableField='physician.address.state']
                              /AdverseEventReport/Report[applicableField='physician.address.city']">
                            <fo:table-row>
                                <xsl:if test="/AdverseEventReport/Report[applicableField='physician.address.street']">
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Street :</fo:block></fo:table-cell>
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Physician/address/street"></xsl:value-of></fo:block></fo:table-cell>
                                </xsl:if>
                                <xsl:if test="/AdverseEventReport/Report[applicableField='physician.address.city'] or /AdverseEventReport/Report[applicableField='reporter.address.state'] ">
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >City, State :</fo:block></fo:table-cell>
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Physician/address/city"></xsl:value-of></fo:block><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/Physician/address/state"></xsl:value-of></fo:block></fo:table-cell>
                                </xsl:if>
                                <xsl:if test="/AdverseEventReport/Report[applicableField='physician.address.zip']">
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Zip :</fo:block></fo:table-cell>
                                      <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/Physician/address/zip"></xsl:value-of></fo:block></fo:table-cell>
                                </xsl:if>
                                  <fo:table-cell><fo:block /></fo:table-cell>
                          </fo:table-row>
                              </xsl:if>

                                      <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block></fo:table-cell></fo:table-row>
                          </xsl:if>
                          <!--PHYSICIAN DATA    END -->
                          <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes"></xsl:text></fo:block></fo:table-cell></fo:table-row>
		  			</fo:table-body>
		  		</fo:table>

                <fo:block space-after="5pt"><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

              </xsl:if>
              <!-- REPORTER INFORMATION TABLE   END -->



                <!-- PATIENT INFORMATION TABLE   END -->
 		  		<fo:block xsl:use-attribute-sets="sub-head" >Patient Information</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="10%"/>

		  			<fo:table-body>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Patient ID :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/studySubjectIdentifier"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Birth Date :</fo:block>
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
										<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/monthString"/>/
										<xsl:if test="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/dayString">
											<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/dayString"/>/
										</xsl:if>
										<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/yearString"/>
						  		</fo:block>
      						</fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Gender :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/gender"/></fo:block></fo:table-cell>
		  			    </fo:table-row>


                        <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Race :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/race"/></fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Ethnicity :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/ethnicity"/></fo:block></fo:table-cell>
                        </fo:table-row>

		  			    <fo:table-row>
      						<xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.height.unit']">
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Height(<xsl:value-of select="AdverseEventReport/ParticipantHistory/height/unit"/> ) :</fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.height.quantity']">
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/ParticipantHistory/height/quantity"/></fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.weight.unit']">
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Weight(<xsl:value-of select="AdverseEventReport/ParticipantHistory/weight/unit"/> ) :</fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.weight.quantity']">
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/ParticipantHistory/weight/quantity"/></fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.height.quantity'] and /AdverseEventReport/Report[applicableField='participantHistory.weight.quantity']">
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Body Surface Area :</fo:block></fo:table-cell>
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/ParticipantHistory/bsa" /></fo:block></fo:table-cell>
                            </xsl:if>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" /></fo:table-cell>
		  			    </fo:table-row>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='participantHistory.baselinePerformanceStatus']">
                            <fo:table-row>
                                <fo:table-cell number-columns-spanned="3"><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Baseline performance status at initiation of protocol - ECOG/Zubrod scale :</fo:block></fo:table-cell>
                                <fo:table-cell><xsl:variable name="statusVar" select="AdverseEventReport/ParticipantHistory/baselinePerformanceStatus"/><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="substring($statusVar,1,1)"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Disease Name :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/DiseaseHistory/CtepStudyDisease/DiseaseTerm/ctepTerm"/></fo:block></fo:table-cell>
                        </fo:table-row>
                          
                        <fo:table-row>
                            <fo:table-cell number-columns-spanned="2"><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Disease Name Not Listed	:</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/DiseaseHistory/otherPrimaryDisease"/></fo:block></fo:table-cell>
                        </fo:table-row>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='diseaseHistory.codedPrimaryDiseaseSite']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Primary Site of Disease :</fo:block></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="value" >
                                        <xsl:if test="AdverseEventReport/DiseaseHistory/AnatomicSite/name != '' and AdverseEventReport/DiseaseHistory/AnatomicSite/category != 'Other'"><xsl:value-of select="AdverseEventReport/DiseaseHistory/AnatomicSite/name"/></xsl:if>
                                        <xsl:if test="AdverseEventReport/DiseaseHistory/otherPrimaryDiseaseSite != '' and AdverseEventReport/DiseaseHistory/AnatomicSite/category = 'Other'"><xsl:value-of select="AdverseEventReport/DiseaseHistory/otherPrimaryDiseaseSite"/></xsl:if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='diseaseHistory.diagnosisDate.month'] or /AdverseEventReport/Report[applicableField='diseaseHistory.diagnosisDate.year'] or /AdverseEventReport/Report[applicableField='diseaseHistory.diagnosisDate.day']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Date of Initial Diagnosis :</fo:block></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="value">
                                        <xsl:if test="/AdverseEventReport/Report[applicableField='diseaseHistory.diagnosisDate.month']">
                                            <xsl:if test="AdverseEventReport/DiseaseHistory/diagnosisDate/monthString">
                                                    <xsl:value-of select="AdverseEventReport/DiseaseHistory/diagnosisDate/monthString"/>/<xsl:value-of select="AdverseEventReport/DiseaseHistory/diagnosisDate/yearString"/>
                                            </xsl:if>
                                        </xsl:if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

		  			</fo:table-body>
		  		</fo:table>

                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>              
                <!-- REPORTER INFORMATION TABLE   END -->



              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.treatmentAssignment'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.firstCourseDate'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.adverseEventCourse.date'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.adverseEventCourse.number'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.totalCourses']">

              <!-- COURSE TABLE   START -->
		  		<fo:block xsl:use-attribute-sets="sub-head" >Course Information</fo:block>
              
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="70%"/>

		  			<fo:table-body>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.treatmentAssignment']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Treatment Assignment Code :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Description :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  			<xsl:choose>
						  				<xsl:when test="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description">
						  					<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description"/>
						  				</xsl:when>
						  				<xsl:otherwise>
						  					<xsl:value-of select="AdverseEventReport/TreatmentInformation/treatmentDescription"/>
						  				</xsl:otherwise>
						  			</xsl:choose>

						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.firstCourseDate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Start date of first course :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/TreatmentInformation/firstCourseDate"/></xsl:call-template></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.adverseEventCourse.date']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Start date of course associated with Expedited Report :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/TreatmentInformation/AdverseEventCourse/date"/></xsl:call-template></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>
                          
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Start date of primary AE :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  			<xsl:for-each select="AdverseEventReport/AdverseEvent">
						  				<xsl:if test="AdverseEventCtcTerm/universal-term = ../Summary[@id='Primary AE']/value">
								  			<xsl:call-template name="standard_date">
								        		<xsl:with-param name="date" select="startDate"/>
		   									</xsl:call-template>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >End date of primary AE :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  			<xsl:for-each select="AdverseEventReport/AdverseEvent">
						  				<xsl:if test="AdverseEventCtcTerm/universal-term = ../Summary[@id='Primary AE']/value">
								  			<xsl:call-template name="standard_date">
								        		<xsl:with-param name="date" select="endDate"/>
		   									</xsl:call-template>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.adverseEventCourse.number']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Course Number on which event(s) occurred :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/TreatmentInformation/AdverseEventCourse/number"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.totalCourses']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Total number of courses to date :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/TreatmentInformation/totalCourses"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Was Investigational Agent(s) administered on this protocol?:</fo:block>
      						</fo:table-cell>
                              <fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  		<xsl:variable name="flg">
						  			<xsl:for-each select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/StudyAgent">
						  				<xsl:if test="INDType = 'DCP_IND'">Yes</xsl:if>
						  				<xsl:if test="INDType = 'CTEP_IND'">Yes</xsl:if>
						  				<xsl:if test="INDType = 'OTHER'">Yes</xsl:if>
						  			</xsl:for-each>
						  		</xsl:variable>
						  		<xsl:value-of select="substring($flg,1,3)"/>
						  		<xsl:if test="string-length($flg)=0">No</xsl:if>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>
		  			</fo:table-body>
		  		</fo:table>

                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

              </xsl:if>
              <!-- COURSE TABLE   END -->

<!--ADVERSE EVENTS-->

              <fo:block  xsl:use-attribute-sets="sub-head" >
        					<xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventCtcTerm/universal-term">
        		  				Adverse Events (CTCAE)
        		  			</xsl:if>
        		  			<xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventMeddraLowLevelTerm/universalTerm">
        		  				Adverse Events (MedDRA)
        		  			</xsl:if>
        		  		</fo:block>
                        <fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>

        		  		<fo:table>
        					<fo:table-column column-width="20%"/>
        					<fo:table-column column-width="20%"/>
        					<fo:table-column column-width="5%"/>
        					<fo:table-column column-width="15%"/>
        					<fo:table-column column-width="10%"/>
        					<fo:table-column column-width="10%"/>
        					<fo:table-column column-width="10%"/>
        					<fo:table-column column-width="10%"/>

        		  			<fo:table-body>

        		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  		<xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventCtcTerm/universal-term">
        						  			CTCAE CATEGORY
        						  		</xsl:if>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Adverse Event
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			 Grade
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Hospitalization/
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Prolongation of
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Hospitalization
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Start Date
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			of AE
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			End Date
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			of AE
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Is
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Primary
        						  		</fo:block>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			AE?
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="label" >
        						  			Comments
        						  		</fo:block>
              						</fo:table-cell>
        		  			    </fo:table-row>

         					<xsl:for-each select="AdverseEventReport/AdverseEvent">
        		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:value-of select="AdverseEventCtcTerm/ctc-term/CtcCategory/name"/>

        						  		</fo:block>
              						</fo:table-cell>

                                      <fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >

        						  			<xsl:value-of select="AdverseEventCtcTerm/ctc-term/term"/>
        						  			<xsl:if test="LowLevelTerm/fullName != ''">
        						  				: <xsl:value-of select="LowLevelTerm/fullName"/>
        						  			</xsl:if>
        									<xsl:choose>
        						         		<xsl:when test="LowLevelTerm/fullName"></xsl:when>
        						                <xsl:otherwise>
        						                	<xsl:if test="AdverseEventCtcTerm/ctc-term/otherRequired = 'true'">: <xsl:value-of select="otherSpecify"/></xsl:if>
        						                </xsl:otherwise>
        								 	</xsl:choose>

                                            <xsl:value-of select="AdverseEventMeddraLowLevelTerm/universalTerm"/>

        						  		</fo:block>
              						</fo:table-cell>

              						<fo:table-cell>
              							<xsl:variable name="gradeVar0" select="grade"/>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:value-of select="substring($gradeVar0,1,1)"/>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
              							<xsl:variable name="hospitalizationVar" select="hospitalization"/>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:value-of select="substring($hospitalizationVar,4,10)"/>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:call-template name="standard_date">
        						        		<xsl:with-param name="date" select="startDate"/>
           									</xsl:call-template>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:call-template name="standard_date">
        						        		<xsl:with-param name="date" select="endDate"/>
           									</xsl:call-template>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:choose>
        							  			<xsl:when test="AdverseEventCtcTerm/universal-term = ../Summary[@id='Primary AE']/value">Yes</xsl:when>
        							  			<xsl:otherwise>No</xsl:otherwise>
        						  			</xsl:choose>
        						  		</fo:block>
              						</fo:table-cell>
              						<fo:table-cell>
        						  		<fo:block xsl:use-attribute-sets="normal" >
        						  			<xsl:value-of select="detailsForOther"/>
        						  		</fo:block>
              						</fo:table-cell>
        		  			    </fo:table-row>
        					  </xsl:for-each>
        		  			</fo:table-body>
        		  		</fo:table>


            <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
<!-- ADVERSE EVENTS END -->

              <!-- EVENT DESCRIPTION TABLE   START -->

              <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.eventDescription'] or
              /AdverseEventReport/Report[applicableField='responseDescription.recoveryDate'] or
              /AdverseEventReport/Report[applicableField='responseDescription.primaryTreatmentApproximateTime.hourString'] or
              /AdverseEventReport/Report[applicableField='responseDescription.presentStatus'] or
              /AdverseEventReport/Report[applicableField='responseDescription.retreated'] or
              /AdverseEventReport/Report[applicableField='responseDescription.eventReappear'] or
              /AdverseEventReport/Report[applicableField='responseDescription.daysNotGiven'] or
              /AdverseEventReport/Report[applicableField='responseDescription.reducedDose'] or
              /AdverseEventReport/Report[applicableField='responseDescription.blindBroken'] or
              /AdverseEventReport/Report[applicableField='responseDescription.reducedDate'] or
              /AdverseEventReport/Report[applicableField='responseDescription.eventAbate'] or
              /AdverseEventReport/Report[applicableField='responseDescription.dateRemovedFromProtocol'] or
              /AdverseEventReport/Report[applicableField='responseDescription.studyDrugInterrupted'] or
              /AdverseEventReport/Report[applicableField='responseDescription.causeOfDeath'] or
              /AdverseEventReport/Report[applicableField='responseDescription.autopsyPerformed']
              ">
				<fo:block xsl:use-attribute-sets="sub-head" >Description of Event</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="40%"/>
					<fo:table-column column-width="60%"/>

		  			<fo:table-body>
                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.eventDescription']">
                                <fo:table-row>
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Description and Treatment of Event :</fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/eventDescription"/></fo:block></fo:table-cell>
                                </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.presentStatus']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Present Status :</fo:block></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="value" >
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'INTERVENTION_CONTINUES'">Intervention for AE Continues</xsl:if>
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERING'">Recovering/Resolving</xsl:if>
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERED_WITH_SEQUELAE'">Recovered/Resolved with Sequelae</xsl:if>
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERED_WITHOUT_SEQUELAE'">Recovered/Resolved without Sequelae</xsl:if>
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'NOT_RECOVERED'">Not recovered/Not resolved</xsl:if>
                                        <xsl:if test="/AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'DEAD'">Fatal/Died</xsl:if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                          </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.recoveryDate']">
                        <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Date of Recovery or Death :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/recoveryDate"/></xsl:call-template></fo:block></fo:table-cell>
                        </fo:table-row>
                        </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.retreated']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Retreated :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/retreated = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/retreated = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.studyDrugInterrupted']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Study Drug Interrupted :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/studyDrugInterrupted = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/studyDrugInterrupted = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.eventReappear']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Did event reappear? :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/eventReappear = 'NO'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/eventReappear = 'YES'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.dateRemovedFromProtocol']">
                              <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Removed from Protocol Treatment (to date) :</fo:block></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="value" >
                                        <xsl:choose>
                                            <xsl:when test="AdverseEventReport/AdverseEventResponseDescription/dateRemovedFromProtocol">Yes</xsl:when>
                                            <xsl:otherwise>No</xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Date Removed from Protocol Treatment :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/dateRemovedFromProtocol"/></xsl:call-template></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.causeOfDeath']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Cause of Death :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/causeOfDeath"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.daysNotGiven']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Days not given (if interrupted) :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/daysNotGiven"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.reducedDose']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">New Dose :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/reducedDose"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.reducedDate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Date New Dose :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/reducedDate"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.eventAbate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Did Event Abate ? :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/eventAbate"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.recoveryDate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Death Date :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'DEAD'"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/recoveryDate"/></xsl:call-template></xsl:if></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>
                          
                        <xsl:if test="/AdverseEventReport/Report[applicableField='responseDescription.autopsyPerformed']">
                            <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Autopsy Performed :</fo:block></fo:table-cell><fo:table-cell>
                                <fo:block xsl:use-attribute-sets="value" >
                                    <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/autopsyPerformed = 'false'">No</xsl:if>
                                    <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/autopsyPerformed = 'true'">Yes</xsl:if>
                                </fo:block>
                            </fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                      <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block></fo:table-cell></fo:table-row>
	  			</fo:table-body>
		  		</fo:table>

		  		<!--<fo:block break-after="page"/>-->
                <!-- EVENT DESCRIPTION TABLE   END -->
                  <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>  
                </xsl:if>



              <!-- RADIATION START -->
              <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.administration'] or
              /AdverseEventReport/Report[applicableField='radiationInterventions.dosage'] or
              /AdverseEventReport/Report[applicableField='radiationInterventions.lastTreatmentDate'] or
              /AdverseEventReport/Report[applicableField='radiationInterventions.fractionNumber'] or
              /AdverseEventReport/Report[applicableField='radiationInterventions.daysElapsed'] or
              /AdverseEventReport/Report[applicableField='radiationInterventions.adjustment']
              ">

  				<fo:block xsl:use-attribute-sets="sub-head">Radiation Intervention</fo:block>

		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>

		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="70%"/>

		  			<fo:table-body>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Treatment Arm :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/></fo:block></fo:table-cell>
		  			    </fo:table-row>


		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Description of Treatment Arm :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  			<xsl:choose>
						  				<xsl:when test="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description">
						  					<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description"/>
						  				</xsl:when>
						  				<xsl:otherwise>
						  					<xsl:value-of select="AdverseEventReport/TreatmentInformation/treatmentDescription"/>
						  				</xsl:otherwise>
						  			</xsl:choose>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.administration']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Type of Radiation Administration :</fo:block></fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="value" >
                                      <xsl:choose>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_HDR' ">Brachytherapy HDR</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_LDR' ">Brachytherapy LDR</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_NOS' ">Brachytherapy NOS</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_NOS' ">External Beam NOS</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_2D' ">External Beam, 2D</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_3D' ">External Beam, 3D</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_IMRT' ">External Beam, IMRT</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_PROTON' ">External Beam, Proton</xsl:when>
                                            <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'SYSTEMIC_RADIOTHERAPY' ">Systemic radiotherapy</xsl:when>
                                            <xsl:otherwise><xsl:value-of select="AdverseEventReport/RadiationIntervention/administration"/></xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                         </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.dosage'] and /AdverseEventReport/Report[applicableField='radiationInterventions.dosageUnit']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Total Dose (to date) :</fo:block>
                                </fo:table-cell><fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/RadiationIntervention/dosage"/>  <xsl:value-of select="AdverseEventReport/RadiationIntervention/dosageUnit"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.lastTreatmentDate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Date of Last Treatment :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/RadiationIntervention/lastTreatmentDate"/></xsl:call-template></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.fractionNumber'] or /AdverseEventReport/Report[applicableField='radiationInterventions.daysElapsed']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Schedule:</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.fractionNumber']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" ><xsl:text disable-output-escaping="yes">&#160;</xsl:text> <xsl:text disable-output-escaping="yes">&#160;</xsl:text>  Number of Fractions :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/RadiationIntervention/fractionNumber"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.daysElapsed']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" ><xsl:text disable-output-escaping="yes">&#160;</xsl:text>  <xsl:text disable-output-escaping="yes">&#160;</xsl:text> Number of Elaspsed Days :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/RadiationIntervention/daysElapsed"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='radiationInterventions.adjustment']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Adjustment :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/RadiationIntervention/adjustment"/></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>
		  			</fo:table-body>

		  		</fo:table>

             </xsl:if>



              
		  		<xsl:if test="AdverseEventReport/SurgeryIntervention">
                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
                      
  				<fo:block xsl:use-attribute-sets="sub-head">Surgery Intervention</fo:block>

		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>

		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>

		  			<fo:table-body>

		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Treatment Arm :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/></fo:block></fo:table-cell>
		  			    </fo:table-row>


		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Description of Treatment Arm :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value" >
						  			<xsl:choose>
						  				<xsl:when test="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description"><xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/description"/></xsl:when>
						  				<xsl:otherwise><xsl:value-of select="AdverseEventReport/TreatmentInformation/treatmentDescription"/></xsl:otherwise>
						  			</xsl:choose>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>


                        <xsl:if test="/AdverseEventReport/Report[applicableField='surgeryInterventions.interventionSite']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Site of Intervention :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/SurgeryIntervention/InterventionSite/name"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='surgeryInterventions.interventionDate']">
                            <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Date of Intervention :</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/SurgeryIntervention/interventionDate"/></xsl:call-template></fo:block></fo:table-cell>
                            </fo:table-row>
                        </xsl:if>
                          
		  			</fo:table-body>
		  		</fo:table>

				</xsl:if>




            <!--TABLE MEDICAL DEVICE  START-->

		  		<xsl:if test="AdverseEventReport/MedicalDevice">

  				<fo:block xsl:use-attribute-sets="sub-head" >Medical Device</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>

		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>

		  			<fo:table-body>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.brandName']">
                        <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Brand Name :</fo:block>
                            </fo:table-cell><fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/brandName"/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.commonName']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Common Name :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/commonName"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.deviceType']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Device Type :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/deviceType"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.manufacturerName']">
		  			    <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Manufacturer Name :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerName"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.manufacturerCity']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Manufacturer City :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerCity"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.manufacturerState']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Manufacturer State/Province :</fo:block>
      						</fo:table-cell><fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerState"/></fo:block></fo:table-cell>
                        </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.modelNumber']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Model Number :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/MedicalDevice/modelNumber"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.lotNumber']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Lot Number :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/lotNumber"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.catalogNumber']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Catalog Number :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/catalogNumber"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.expirationDate']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Expiration Date :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/expirationDate"/></xsl:call-template></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.serialNumber']">
		  			    <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Serial Number :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/serialNumber"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.otherNumber']">
		  			    <fo:table-row>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Other Number :</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/otherNumber"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.deviceOperator'] or /AdverseEventReport/Report[applicableField='medicalDevices.otherDeviceOperator']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Operator of Device :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" >
				                    <xsl:choose>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'HEALTH_PROFESSIONAL'">Health Professional</xsl:when>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'PATIENT'">Lay User/Patient</xsl:when>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'OTHER'">Other</xsl:when>
				                        <xsl:otherwise><xsl:value-of select="AdverseEventReport/MedicalDevice/DeviceOperator"/></xsl:otherwise>
				                    </xsl:choose>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.implantedDate']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Implanted Date :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/implantedDate"/></xsl:call-template></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.explantedDate']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Explanted Date :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/explantedDate"/></xsl:call-template></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.deviceReprocessed']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Is this a Single-use Device that was Reprocessed and Reused on a Patient? :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:choose><xsl:when test="AdverseEventReport/MedicalDevice/DeviceReprocessed = 'YES'">Yes</xsl:when><xsl:otherwise>No</xsl:otherwise></xsl:choose></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.reprocessorName']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Name of Reprocessor :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="AdverseEventReport/MedicalDevice/reprocessorName"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.reprocessorAddress']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Address of Reprocessor :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="AdverseEventReport/MedicalDevice/reprocessorAddress"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.evaluationAvailability']">
		  			    <fo:table-row>
      						<fo:table-cell ><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Device Available for Evaluation? (Do not send to FDA) :</fo:block></fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="value">
				                <xsl:choose>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'YES'">Yes</xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'NO'">No</xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'RETURNED'">Returned</xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'UNKNOWN'">Unknown</xsl:when>
				                    <xsl:otherwise><xsl:value-of select="AdverseEventReport/MedicalDevice/EvaluationAvailability"/></xsl:otherwise>
				                </xsl:choose>
						  		</fo:block>
      						</fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <xsl:if test="/AdverseEventReport/Report[applicableField='medicalDevices.returnedDate']">
		  			    <fo:table-row>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm" >Returned Date :</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/returnedDate"/></xsl:call-template></fo:block></fo:table-cell>
		  			    </fo:table-row>
                        </xsl:if>

                        <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes"></xsl:text></fo:block></fo:table-cell></fo:table-row>
		  			</fo:table-body>
		  		</fo:table>

					<fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

				</xsl:if>

              <!--TABLE MEDICAL DEVICE  END-->



				<xsl:if test="AdverseEventReport/SAEReportPriorTherapy">

  				<fo:block xsl:use-attribute-sets="sub-head">Prior Therapies</fo:block>

		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>

		  		<fo:table>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>


		  			<fo:table-body>

                        <fo:table-row>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.priorTherapy']">
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Therapy</fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.startDate.year'] and /AdverseEventReport/Report[applicableField='saeReportPriorTherapies.startDate.month']">
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Therapy Start Date</fo:block></fo:table-cell>
                            </xsl:if>
                            <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.endDate.year'] and /AdverseEventReport/Report[applicableField='saeReportPriorTherapies.endDate.month']">
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Therapy End Date</fo:block></fo:table-cell>
                            </xsl:if>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Comments</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Chemotherapy Agents</fo:block></fo:table-cell>
                        </fo:table-row>

						<xsl:for-each select="AdverseEventReport/SAEReportPriorTherapy">
			  			    <fo:table-row>
	      						<xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.priorTherapy']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="PriorTherapy/text"/></fo:block></fo:table-cell>
                                </xsl:if>
                                <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.startDate.year'] and /AdverseEventReport/Report[applicableField='saeReportPriorTherapies.startDate.month']">
	      						    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:if test="startDate/monthString"><xsl:value-of select="startDate/monthString"/>/<xsl:value-of select="startDate/dayString"/>/<xsl:value-of select="startDate/yearString"/></xsl:if></fo:block></fo:table-cell>
                                </xsl:if>
                                <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.endDate.year'] and /AdverseEventReport/Report[applicableField='saeReportPriorTherapies.endDate.month']">
	      						    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:if test="endDate/monthString"><xsl:value-of select="endDate/monthString"/>/<xsl:value-of select="endDate/dayString"/>/<xsl:value-of select="endDate/yearString"/></xsl:if></fo:block></fo:table-cell>
                                </xsl:if>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="other"/></fo:block></fo:table-cell>

                                <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPriorTherapies.other']">
	      						    <xsl:if test="PriorTherapyAgent"><fo:table-cell><xsl:for-each select="PriorTherapyAgent"><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="ChemoAgent/name"/></fo:block></xsl:for-each></fo:table-cell></xsl:if>
                                </xsl:if>
			  			    </fo:table-row>
		  			    </xsl:for-each>
		  			</fo:table-body>
		  		</fo:table>

		  		</xsl:if>


              <!--PRIOR THERAPIES TABLE     END-->

		  		<xsl:if test="AdverseEventReport/SAEReportPreExistingCondition">
                <xsl:if test="/AdverseEventReport/Report[applicableField='saeReportPreExistingConditions.preExistingCondition']">
                    
                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

  				<fo:block xsl:use-attribute-sets="sub-head" >Pre-Existing Conditions</fo:block>
				<xsl:for-each select="AdverseEventReport/SAEReportPreExistingCondition">
                    <fo:block xsl:use-attribute-sets="value" >
                        <xsl:value-of select="PreExistingCondition/text"/><xsl:value-of select="other"/>
                    </fo:block>
				</xsl:for-each>

		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
		  		<fo:block xsl:use-attribute-sets="value" >  </fo:block>

                </xsl:if>
				</xsl:if>


              
				<xsl:if test="AdverseEventReport/DiseaseHistory/MetastaticDiseaseSite">
				<xsl:if test="/AdverseEventReport/Report[applicableField='diseaseHistory.metastaticDiseaseSites.codedSite']">
		  			<fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
                    <fo:block xsl:use-attribute-sets="sub-head" >Sites of Metastatic Disease</fo:block>
                    <xsl:for-each select="AdverseEventReport/DiseaseHistory/MetastaticDiseaseSite">
                                    <fo:block xsl:use-attribute-sets="value" >
                                        <xsl:if test="AnatomicSite/name != '' and AnatomicSite/category != 'Other'"><xsl:value-of select="AnatomicSite/name"/></xsl:if>
                                        <xsl:if test="otherSite != ''"><xsl:value-of select="otherSite"/></xsl:if>
                                    </fo:block>
                    </xsl:for-each>
                    <fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
                    <fo:block xsl:use-attribute-sets="value" >  </fo:block>
				</xsl:if>
				</xsl:if>

                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>              




              <!-- AGENTS START -->

              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.investigationalAgentAdministered'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.amount'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.units'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lastAdministeredDate'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.comments'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lotNumber'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.formulation'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.agentAdjustment'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayAmount'] or
              /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayUnits']">

  				<fo:block xsl:use-attribute-sets="sub-head">Protocol Agents</fo:block>
		  		<fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block>
		  		<fo:block xsl:use-attribute-sets="label" > Treatment Assignment Code :<xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/></fo:block>

		  		<fo:table>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>

		  			<fo:table-body>

		  			    <fo:table-row>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.investigationalAgentAdministered']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Agent</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.amount'] and /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.units']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Total Dose Administered this Course</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lastAdministeredDate']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Last Administered Date</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lotNumber']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Lot Number</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.formulation']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Formulation</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.comments']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Comments</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.agentAdjustment']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Agent Adjustment</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayAmount'] and /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayUnits']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Agent Delayed</fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Delay</fo:block></fo:table-cell>
                              </xsl:if>
                              <fo:table-cell><fo:block /></fo:table-cell>
		  			    </fo:table-row>

 					<xsl:for-each select="AdverseEventReport/TreatmentInformation/CourseAgent">
		  			    <fo:table-row>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.investigationalAgentAdministered']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="StudyAgent/Agent/name"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.amount'] and /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.dose.units']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="Dose/amount"/><xsl:value-of select="Dose/units"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lastAdministeredDate']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:variable name="trimmedlastAdministeredDate"><xsl:call-template name="trim"><xsl:with-param name="s" select="lastAdministeredDate"/></xsl:call-template></xsl:variable><xsl:call-template name="standard_date"><xsl:with-param name="date" select="$trimmedlastAdministeredDate"/></xsl:call-template></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.lotNumber']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="lotNumber"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.formulation']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" ><xsl:value-of select="formulation"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.comments']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="comments"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.agentAdjustment']">
                                    <fo:table-cell>
                                        <fo:block xsl:use-attribute-sets="value" >
                                            <xsl:if test="AgentAdjustment='DOSE_INCREASED'">Dose increased</xsl:if>
                                            <xsl:if test="AgentAdjustment='DOSE_NOTCHANGED'">Dose not changed</xsl:if>
                                            <xsl:if test="AgentAdjustment='DOSE_REDUCED'">Dose reduced</xsl:if>
                                            <xsl:if test="AgentAdjustment='DRUG_WITHDRAWN'">Drug withdrawn</xsl:if>
                                            <xsl:if test="AgentAdjustment='NA'">Not applicable</xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayAmount'] and /AdverseEventReport/Report[applicableField='treatmentInformation.courseAgents.administrationDelayUnits']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:choose><xsl:when test="administrationDelayAmount">Yes</xsl:when><xsl:otherwise>No</xsl:otherwise></xsl:choose></fo:block></fo:table-cell>
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value"><xsl:value-of select="administrationDelayAmount"/> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> <xsl:value-of select="administrationDelayUnits"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <fo:table-cell><fo:block /></fo:table-cell>
		  			    </fo:table-row>
					 </xsl:for-each>
		  			</fo:table-body>
		  		</fo:table>

                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

              </xsl:if>

              <!-- AGENTS END -->



                <xsl:if test="/AdverseEventReport/Report[applicableField='concomitantMedications.agentName']">
                        <xsl:if test="AdverseEventReport/ConcomitantMedication">
                            <fo:block xsl:use-attribute-sets="sub-head" >Concomitant Medications</fo:block>
                        </xsl:if>

                        <xsl:for-each select="AdverseEventReport/ConcomitantMedication">
                            <fo:block xsl:use-attribute-sets="value"><xsl:value-of select="name"/></fo:block>
                        </xsl:for-each>

                        <xsl:if test = "AdverseEventReport/ConcomitantMedication"	>
                            <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
                        </xsl:if>
                </xsl:if>


<!-- OTHER CAUSES TABLE     START-->
              
			<xsl:if test="/AdverseEventReport/Report[applicableField='otherCauses.text']">
                <xsl:if test = "AdverseEventReport/OtherCause"	>
                    <fo:block xsl:use-attribute-sets="sub-head" >Other Contributing Causes</fo:block>
               </xsl:if>

                <xsl:for-each select="AdverseEventReport/OtherCause">
                    <fo:block xsl:use-attribute-sets="value" >
                        <xsl:value-of select="text"/>
                    </fo:block>
                </xsl:for-each>

                <xsl:if test = "AdverseEventReport/OtherCause"	>
                        <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
                </xsl:if>
            </xsl:if>

<!-- OTHER CAUSES TABLE     END-->



              

<!-- ADVERSE EVENTS      START-->              

<xsl:if test="
                /AdverseEventReport/Report[applicableField='adverseEvents.adverseEventCtcTerm.term'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.grade'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.hospitalization'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.startDate'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.endDate'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.comments'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.eventLocation'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.expected'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.attributionSummary'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.eventApproximateTime.hourString'] or
                /AdverseEventReport/Report[applicableField='adverseEvents.participantAtRisk']
">

    <fo:block  xsl:use-attribute-sets="sub-head" >
      <xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventCtcTerm/universal-term">Adverse Events (CTCAE)</xsl:if>
      <xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventMeddraLowLevelTerm/universalTerm">Adverse Events (MedDRA)</xsl:if>
    </fo:block>
    <fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block>
    
<xsl:for-each select="AdverseEventReport/AdverseEvent">
    <fo:block xsl:use-attribute-sets="value">AE:
        <xsl:value-of select="AdverseEventCtcTerm/ctc-term/term"/>
        <xsl:if test="LowLevelTerm/fullName != ''">: <xsl:value-of select="LowLevelTerm/fullName"/></xsl:if>
        <xsl:choose>
        <xsl:when test="LowLevelTerm/fullName"></xsl:when>
        <xsl:otherwise><xsl:if test="AdverseEventCtcTerm/ctc-term/otherRequired = 'true'">: <xsl:value-of select="otherSpecify"/></xsl:if></xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="AdverseEventMeddraLowLevelTerm/universalTerm"/>,
        <xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:text disable-output-escaping="yes">&#160;</xsl:text> 
        CATEGORY: <xsl:value-of select="AdverseEventCtcTerm/ctc-term/CtcCategory/name"/>
    </fo:block>
    
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.grade']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Grade : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:variable name="gradeVar0" select="grade"/><xsl:value-of select="substring($gradeVar0,1,1)"/></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.hospitalization']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Hospitalizatio / Prolongation of : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:variable name="hospitalizationVar" select="hospitalization"/><xsl:value-of select="substring($hospitalizationVar, 4, 10)"/></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.startDate']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Start Date of AE: </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="startDate"/></xsl:call-template></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.endDate']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">End Date of AE: </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:call-template name="standard_date"><xsl:with-param name="date" select="endDate"/></xsl:call-template></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.adverseEventCtcTerm.term']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Is Primary AE ? : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:choose><xsl:when test="AdverseEventCtcTerm/universal-term = ../Summary[@id='Primary AE']/value">Yes</xsl:when><xsl:otherwise>No</xsl:otherwise></xsl:choose></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.eventApproximateTime.hourString']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Event approximate time : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="eventApproximateTime/hour"/>:<xsl:value-of select="eventApproximateTime/minute"/></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.eventLocation']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Where was the patient when the event occured ? : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="eventLocation"/></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.attributionSummary']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Attribution to study intervention : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="attributionSummary"/></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.participantAtRisk']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Was participant at risk ? : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:if test="participantAtRisk = 'true'">Yes</xsl:if><xsl:if test="participantAtRisk = 'false'">No</xsl:if></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.expected']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Expected ? : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:if test="expected = 'true'">Yes</xsl:if><xsl:if test="expected = 'false'">No</xsl:if></fo:inline></fo:block></xsl:if>
    <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.comments']"><fo:block margin-left="20mm"><fo:inline xsl:use-attribute-sets="label">Comments : </fo:inline><fo:inline xsl:use-attribute-sets="value"><xsl:value-of select="detailsForOther"/></fo:inline></fo:block></xsl:if>
    
</xsl:for-each>

    <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

</xsl:if>


<!-- ADVERSE EVENTS      END-->



<!-- ADVERSE OUTCOMES      START-->              

              <xsl:if test="/AdverseEventReport/Report[applicableField='adverseEvents.outcomes']">
  				<fo:block xsl:use-attribute-sets="sub-head" >Adverse Events Outcomes</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block>

		  		<fo:table>
					<fo:table-column column-width="40%"/>
					<fo:table-column column-width="5%"/>
					<fo:table-column column-width="55%"/>

		  			<fo:table-body>

		  			    <fo:table-row>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Edverse Event</fo:block></fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" ></fo:block></fo:table-cell>
      						    <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Outcomes</fo:block></fo:table-cell>
		  			    </fo:table-row>

 					    <xsl:for-each select="AdverseEventReport/AdverseEvent">
                            <fo:table-row>
                                    <fo:table-cell>

                                        <fo:block xsl:use-attribute-sets="value" >
                                            <xsl:value-of select="AdverseEventCtcTerm/ctc-term/term"/>
                                            <xsl:if test="LowLevelTerm/fullName != ''">: <xsl:value-of select="LowLevelTerm/fullName"/></xsl:if>
                                            <xsl:choose>
                                                <xsl:when test="LowLevelTerm/fullName"></xsl:when>
                                                <xsl:otherwise><xsl:if test="AdverseEventCtcTerm/ctc-term/otherRequired = 'true'">: <xsl:value-of select="otherSpecify"/></xsl:if></xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:value-of select="AdverseEventMeddraLowLevelTerm/universalTerm"/>
                                        </fo:block>

                                    </fo:table-cell>

                                    <fo:table-cell><fo:block /></fo:table-cell>

                                    <fo:table-cell>
                                        <xsl:for-each select="Outcome">
                                            <fo:block xsl:use-attribute-sets="label"><xsl:value-of select="OutcomeType" /></fo:block>
                                            <!--<xsl:if test="position() != last()">,<xsl:text disable-output-escaping="yes">&#160;&#160;</xsl:text></xsl:if>-->
                                        </xsl:for-each>
                                        <fo:block><xsl:text disable-output-escaping="yes">&#160;&#160;</xsl:text></fo:block>
                                    </fo:table-cell>
                                <fo:table-cell><fo:block xsl:use-attribute-sets="label" ></fo:block></fo:table-cell>
                          </fo:table-row>
					  </xsl:for-each>
		  			</fo:table-body>

		  		</fo:table>
                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>

              </xsl:if>
<!-- ADVERSE EVENTS      END-->



<!-- ATTRIBUTION      START-->

  				<fo:block  xsl:use-attribute-sets="sub-head" >Attribution for Adverse Events</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>


		  	<xsl:for-each select="AdverseEventReport/AdverseEvent">
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="30%"/>
		  			<fo:table-body>
			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="label">Attribute to</fo:block></fo:table-cell>
	      						<fo:table-cell>
	      							<xsl:variable name="gradeVar" select="grade"/>
							  		<fo:block xsl:use-attribute-sets="value" >
							  			Gr.<xsl:value-of select="substring($gradeVar,1,1)"/>
							  			<xsl:text disable-output-escaping="yes">&#160;   </xsl:text><xsl:value-of select="AdverseEventCtcTerm/universal-term"/><xsl:value-of select="AdverseEventMeddraLowLevelTerm/universalTerm"/>
							  		</fo:block>
	      						</fo:table-cell>
			  			    </fo:table-row>

		  			</fo:table-body>
		  		</fo:table>

		  		<!--<fo:block space-after="0.2pt"><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.2pt"/></fo:block>-->
                  
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="30%"/>
		  			<fo:table-body>
			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value">Course</fo:block></fo:table-cell>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value"></fo:block></fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="CourseAgentAttribution">
				  			    <fo:table-row>
		      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:text disable-output-escaping="yes">&#160; &#160;   </xsl:text><xsl:value-of select="CourseAgent/StudyAgent/Agent/name"/></fo:block></fo:table-cell>
		      						<fo:table-cell><xsl:variable name="attributionVar1" select="attribution"/><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="substring($attributionVar1,4,20)"/></fo:block></fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
			  	<xsl:if test="ConcomitantMedicationAttribution">
			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" >Concomitant medications</fo:block></fo:table-cell>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
			  			    </fo:table-row>
			  			    <xsl:for-each select="ConcomitantMedicationAttribution">
				  			    <fo:table-row>
		      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:text disable-output-escaping="yes">&#160; &#160;   </xsl:text><xsl:value-of select="ConcomitantMedication/name"/> <xsl:value-of select="ConcomitantMedication/other"/></fo:block></fo:table-cell>
		      						<fo:table-cell><xsl:variable name="attributionVar2" select="attribution"/><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="substring($attributionVar2,4,20)"/></fo:block></fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
			  	</xsl:if>
			  	<xsl:if test="OtherCauseAttribution">
			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" >Other causes</fo:block>
	      						</fo:table-cell><fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="OtherCauseAttribution">
				  			    <fo:table-row>
		      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:text disable-output-escaping="yes">&#160; &#160;   </xsl:text><xsl:value-of select="OtherCause/text"/></fo:block></fo:table-cell>
		      						<fo:table-cell><xsl:variable name="attributionVar3" select="attribution"/><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="substring($attributionVar3,4,20)"/></fo:block></fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
				</xsl:if>
			  	<xsl:if test="DiseaseAttribution">
			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" >Disease</fo:block></fo:table-cell>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="DiseaseAttribution">
				  			    <fo:table-row>
		      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:text disable-output-escaping="yes">&#160; &#160;   </xsl:text><xsl:value-of select="DiseaseHistory/CtepStudyDisease/DiseaseTerm/ctepTerm"/></fo:block></fo:table-cell>
		      						<fo:table-cell><xsl:variable name="attributionVar4" select="attribution"/><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="substring($attributionVar4,4,20)"/></fo:block></fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
				</xsl:if>


			  			    <fo:table-row>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
	      						<fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
			  			    </fo:table-row>


		  			</fo:table-body>
		  		</fo:table>

			 </xsl:for-each>

<!-- ATTRIBUTION      END-->



<!-- LABS      START-->


				<xsl:if test="AdverseEventReport/Lab">
                    <xsl:if test="/AdverseEventReport/Report[applicableField='labs.labTerm'] or
                     /AdverseEventReport/Report[applicableField='labs.baseline.date'] or
                     /AdverseEventReport/Report[applicableField='labs.baseline.value'] or
                     /AdverseEventReport/Report[applicableField='labs.nadir.date'] or
                     /AdverseEventReport/Report[applicableField='labs.nadir.value'] or
                     /AdverseEventReport/Report[applicableField='labs.recovery.date'] or
                     /AdverseEventReport/Report[applicableField='labs.recovery.value'] or
                     /AdverseEventReport/Report[applicableField='labs.site'] or
                     /AdverseEventReport/Report[applicableField='labs.labDate'] or
                     /AdverseEventReport/Report[applicableField='labs.infectiousAgent']
                    ">

                <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
  				<fo:block  xsl:use-attribute-sets="sub-head" >Abnormal and Relevant Normal Lab Results</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>


		  		<fo:table>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>

		  			<fo:table-body>

		  			    <fo:table-row>

                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.labTerm']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Lab</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.baseline.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Baseline date</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.baseline.value']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.nadir.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Worst Date</fo:block></fo:table-cell>
                              </xsl:if>
      						  <xsl:if test="/AdverseEventReport/Report[applicableField='labs.nadir.value']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.recovery.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Recovery/Latest Date</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.recovery.value']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
                              </xsl:if>
      						  <xsl:if test="/AdverseEventReport/Report[applicableField='labs.site']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Microbiology Site</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.labDate']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Date</fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.infectiousAgent']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="label" >Infectious Agent</fo:block></fo:table-cell>
                              </xsl:if>
                              <fo:table-cell><fo:block xsl:use-attribute-sets="label" ></fo:block></fo:table-cell>
		  			    </fo:table-row>

 					 <xsl:for-each select="AdverseEventReport/Lab">
		  			    <fo:table-row>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.labTerm']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="labTerm/term"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="other"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.baseline.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="baseline/date"/></xsl:call-template></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.baseline.value']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="baseline/value"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.nadir.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="nadir/date"/></xsl:call-template></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.nadir.value']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="nadir/value"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.recovery.date']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="recovery/date"/></xsl:call-template></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.baseline.value']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="recovery/value"/><xsl:text disable-output-escaping="yes">&#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.site']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="site"/></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.labDate']">
      						        <fo:table-cell><fo:block xsl:use-attribute-sets="value" ></fo:block></fo:table-cell>
                              </xsl:if>
                              <xsl:if test="/AdverseEventReport/Report[applicableField='labs.infectiousAgent']">
                                    <fo:table-cell><fo:block xsl:use-attribute-sets="value" ><xsl:value-of select="infectiousAgent"/></fo:block></fo:table-cell>
                              </xsl:if>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="label" ></fo:block></fo:table-cell>
		  			    </fo:table-row>
					</xsl:for-each>
		  			</fo:table-body>
		  		</fo:table>
		  		</xsl:if>
		  		</xsl:if>
    <!-- LABS      END-->



              <!--  ADDITIONAL INFO    START-->

              <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.labReports'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.obaForm'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.pathologyReport'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.progressNotes'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.radiologyReports'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.referralLetters'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.irbReport'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.other'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.otherInformation'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.autopsyReporte'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.consults'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.dischargeSummaryl'] or
              /AdverseEventReport/Report[applicableField='additionalInformation.flowChart']
                ">
                <fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
				<fo:block xsl:use-attribute-sets="sub-head" >Additional Information</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="40%"/>
					<fo:table-column column-width="60%"/>

		  			<fo:table-body>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.autopsyReport']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Autopsy report :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/autopsyReport = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/autopsyReport = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.consults']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Consults :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/consults = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/consults = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.dischargeSummary']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Discharge Summary :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/dischargeSummary = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/dischargeSummary = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.flowCharts']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Flow sheets/case report forms :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/flowCharts = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/flowCharts = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.labReports']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Laboratory reports :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/labReports = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/labReports = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.obaForm']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">OBA forms :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/obaForm = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/obaForm = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.pathologyReport']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Pathology report :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/pathologyReport = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/pathologyReport = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.progressNotes']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Progress notes :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/progressNotes = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/progressNotes = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.radiologyReports']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Radiology report :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/radiologyReports = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/radiologyReports = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.referralLetters']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Referral letters :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/referralLetters = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/referralLetters = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.irbReport']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Summary report sent to IRB :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/irbReport = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/irbReport = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.other']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Other :</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value">
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/other = 'false'">No</xsl:if>
                                          <xsl:if test="AdverseEventReport/AdditionalInformation/other = 'true'">Yes</xsl:if>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                          <xsl:if test="/AdverseEventReport/Report[applicableField='additionalInformation.otherInformation']">
                              <fo:table-row><fo:table-cell><fo:block xsl:use-attribute-sets="label" margin-left="2mm">Other information:</fo:block></fo:table-cell>
                                  <fo:table-cell>
                                      <fo:block xsl:use-attribute-sets="value"><xsl:value-of select="/AdverseEventReport/AdditionalInformation/otherInformation" /></fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:if>

                      <fo:table-row><fo:table-cell><fo:block><xsl:text disable-output-escaping="yes">&#160;</xsl:text></fo:block></fo:table-cell></fo:table-row>
	  			</fo:table-body>
		  		</fo:table>

		  		<!--<fo:block break-after="page"/>-->
                <!-- EVENT DESCRIPTION TABLE   END -->
                  <fo:block><fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.5px"/></fo:block>
                </xsl:if>
              
              <!--  ADDITIONAL INFO    END-->


  			  <fo:block id="content_terminator"/>
		  </fo:flow>
		</fo:page-sequence>
	</fo:root>
	</xsl:template>

	<xsl:template name="standard_date">
		<xsl:param name="date" />
		<xsl:if test="$date">
			<!-- Month -->
			<xsl:value-of select="substring($date, 6, 2)" />
			<xsl:text>/</xsl:text>
			<!-- Day -->
			<xsl:value-of select="substring($date, 9, 2)" />
			<xsl:text>/</xsl:text>
			<!-- Year -->
			<xsl:value-of select="substring($date, 1, 4)" />
		</xsl:if>
	</xsl:template>

<xsl:template name="left-trim">
  <xsl:param name="s" />
  <xsl:choose>
    <xsl:when test="substring($s, 1, 1) = ''">
      <xsl:value-of select="$s"/>
    </xsl:when>
    <xsl:when test="normalize-space(substring($s, 1, 1)) = ''">
      <xsl:call-template name="left-trim">
        <xsl:with-param name="s" select="substring($s, 2)" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$s" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="right-trim">
  <xsl:param name="s" />
  <xsl:choose>
    <xsl:when test="substring($s, 1, 1) = ''">
      <xsl:value-of select="$s"/>
    </xsl:when>
    <xsl:when test="normalize-space(substring($s, string-length($s))) = ''">
      <xsl:call-template name="right-trim">
        <xsl:with-param name="s" select="substring($s, 1, string-length($s) - 1)" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$s" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="trim">
  <xsl:param name="s" />
  <xsl:call-template name="right-trim">
    <xsl:with-param name="s">
      <xsl:call-template name="left-trim">
        <xsl:with-param name="s" select="$s" />
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template name="sqrt">
	<xsl:param name="num" select="0"/>  <!-- The number you want to find the square root of -->
    <xsl:param name="try" select="1"/>  <!-- The current 'try'.  This is used internally. -->
    <xsl:param name="iter" select="1"/> <!-- The current iteration, checked against maxiter to limit loop count -->
   <xsl:param name="maxiter" select="10"/>  <!-- Set this up to insure against infinite loops -->

   <!-- This template was written by Nate Austin using Sir Isaac Newton's method of finding roots -->

   <xsl:choose>
     <xsl:when test="$try * $try = $num or $iter &gt; $maxiter">
       <xsl:value-of select="$try"/>
     </xsl:when>
     <xsl:otherwise>
       <xsl:call-template name="sqrt">
         <xsl:with-param name="num" select="$num"/>
         <xsl:with-param name="try" select="$try - (($try * $try - $num) div (2 * $try))"/>
         <xsl:with-param name="iter" select="$iter + 1"/>
         <xsl:with-param name="maxiter" select="$maxiter"/>
       </xsl:call-template>
     </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
        