<?xml version="1.0"?>
<xsl:stylesheet 
     version="1.0" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:fo="http://www.w3.org/1999/XSL/Format"
     xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

  <xsl:output method="xml"/>
  
  <xsl:attribute-set name="sub-head">
    <xsl:attribute name="font-family">arial</xsl:attribute>
    <xsl:attribute name="font-size">10pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-decoration">underline</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="label">
    <xsl:attribute name="font-family">arial</xsl:attribute>
    <xsl:attribute name="font-size">8pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="normal">
    <xsl:attribute name="font-family">arial</xsl:attribute>
    <xsl:attribute name="font-size">8pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="tr-height-1">
    <xsl:attribute name="height">4mm</xsl:attribute>
  </xsl:attribute-set>
        

  <xsl:template match="/">
  	
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	    
		<fo:layout-master-set>
		  	<fo:simple-page-master master-name="A4" margin-left="2mm" margin-top="2mm" margin-right="0.25in" margin-bottom="10mm">
		    	<fo:region-body margin-top="2.1in" margin-bottom="1in"/>
		    	<fo:region-before extent="2in"/>
		    	<fo:region-after extent="1mm"/>
		  	</fo:simple-page-master>		  	
		</fo:layout-master-set>
		
		<fo:page-sequence master-reference="A4">
			
		<fo:static-content flow-name="xsl-region-after">
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
				    <fo:table-cell>
				      <fo:block> 
				      	<fo:external-graphic src="url(http://www.hhs.gov/images/system/hlogo.gif)" content-height="3em" content-width="3em"/>
				      </fo:block>
				      
				    </fo:table-cell>
				    
				    <fo:table-cell>
				      <fo:block font-weight="bold" font-size="8pt" font-family="arial" text-align-last="center" display-align="center">
				      	Department of Health and Human Services</fo:block>
				      
				      <fo:block space-before="5mm" font-weight="bold" font-size="10pt" font-family="arial" text-align-last="center" display-align="center">
				      	(Site Reported)</fo:block>
				      
				    </fo:table-cell>

				    <fo:table-cell font-weight="bold" font-size="8pt" font-family="arial" text-align-last="left">
				      <fo:block>Public Health Service</fo:block>
				      <fo:block>National Institutes of Health</fo:block>
				      <fo:block>National Cancer Institute</fo:block>
				      <fo:block>Bethesda, Maryland 20892</fo:block>
				    </fo:table-cell>
				  </fo:table-row>
				  <fo:table-row>
				    <fo:table-cell>
				      <fo:block font-weight="bold" font-size="8pt" font-family="arial" text-align-last="left"> 
				      	Run Date :   
    					<xsl:value-of select="java:format(java:java.text.SimpleDateFormat.new ('MM/d/yyyy h:mm:ss a '), java:java.util.Date.new())"/>				      
				      </fo:block>
				      
				    </fo:table-cell>
				    
				    <fo:table-cell>
				     <fo:block font-weight="bold" font-size="14pt" font-family="arial" text-align-last="center" display-align="center">
				      	Adverse Event Expedited Report</fo:block>	      
				    </fo:table-cell>
				  </fo:table-row>
				</fo:table-body>
				
				</fo:table>
					<fo:block space-after="5pt">
						<fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="2pt"/>
					</fo:block>
					
				<fo:block margin-left="4mm"> 
					<fo:inline xsl:use-attribute-sets="label" > Protocol Number  :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal" > 
				   		<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/Identifier/value"/>
				    </fo:inline>	 
					<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; </xsl:text>
					<fo:inline xsl:use-attribute-sets="label" > CTC Version  :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal" > 
				   		<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/Ctc/name"/>
				    </fo:inline>
					<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160; &amp;#160; </xsl:text>
					<fo:inline xsl:use-attribute-sets="label" > Principal Investigator :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal"> 
						<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/StudyInvestigator/SiteInvestigator/SiteInvestigator/firstName"/>
						<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
						<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/StudyInvestigator/SiteInvestigator/SiteInvestigator/lastName"/>
						
						<!--						 
						<for-each select="AdverseEventReport/StudyParticipantAssignment/StudySite/SiteInvestigator/"> 
								
								<xsl:value-of select="Investigator/firstName"/>
								<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
								<xsl:value-of select="Investigator/lastName"/>
						</for-each>-->
					</fo:inline>	
				</fo:block>

				<fo:block margin-left="4mm"> 
					<fo:inline xsl:use-attribute-sets="label" > Title :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal" > 
						<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/longTitle"/> 
					</fo:inline>
				</fo:block>

				<fo:block margin-left="4mm"> 
					<fo:inline xsl:use-attribute-sets="label" > Institution :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal" > 
						<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Organization/name"/> 
				    </fo:inline>	 
					<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160;  </xsl:text>
					<fo:inline xsl:use-attribute-sets="label" > Report Type :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal"> 
						<xsl:choose>												 
							<xsl:when test ="AdverseEventReport/Report/ReportVersion/reportVersionId &gt; 0 ">
								Amended
							</xsl:when>
							<xsl:otherwise>
								Original
							</xsl:otherwise>
						</xsl:choose>
					</fo:inline>	
					<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160;  </xsl:text>
					<fo:inline xsl:use-attribute-sets="label" > Ticket #:</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal">   <xsl:value-of select="AdverseEventReport/Report/assignedIdentifer"/> </fo:inline>	
					<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160;  </xsl:text>
					<fo:inline xsl:use-attribute-sets="label" > Amendment #: </fo:inline>
					<fo:inline xsl:use-attribute-sets="normal"> 
						<xsl:value-of select="AdverseEventReport/Report/ReportVersion/reportVersionId"/>  
					</fo:inline>																	
				</fo:block>
				
				<fo:block margin-left="4mm"> 
					<fo:inline xsl:use-attribute-sets="label" > Created Date :</fo:inline>
					<fo:inline xsl:use-attribute-sets="normal" > 
						<xsl:variable name="trimmedDate">
							<xsl:call-template name="trim">
						        <xsl:with-param name="s" select="AdverseEventReport/createdAt"/>
   							</xsl:call-template>				
						</xsl:variable>
						<xsl:call-template name="standard_date">
						        <xsl:with-param name="date" select="$trimmedDate"/>
   						</xsl:call-template>
					</fo:inline>
				</fo:block>

					<fo:block space-after="5pt">
						<fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="2pt"/>
					</fo:block>
									
		  </fo:static-content>
		  
		  <fo:flow flow-name="xsl-region-body">			  
		  		<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Reporter Information 
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="5%"/>
					<fo:table-column column-width="18%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="25%"/>
										
		  			<fo:table-body>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm"> 
						  			Reporter Name :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:value-of select="AdverseEventReport/Reporter/firstName"/> 
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
									<xsl:value-of select="AdverseEventReport/Reporter/lastName"/>
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
      					
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Phone :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism">
						  				<xsl:if test="key = 'phone'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Fax :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism">
						  				<xsl:if test="key = 'fax'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Email :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Reporter/ContactMechanism">
						  				<xsl:if test="key = 'e-mail'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>
		  			  
		  			  <fo:table-row><fo:table-cell><fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> </fo:table-cell></fo:table-row>
		  			  	
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" space-before="5mm"> 
						  			Submitter Name :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:value-of select="AdverseEventReport/Report/Submitter/firstName"/> 
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
									<xsl:value-of select="AdverseEventReport/Report/Submitter/lastName"/>
						  		</fo:block>       							
      						</fo:table-cell>
      					</fo:table-row>
      					
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Phone :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Report/Submitter/ContactMechanism">
						  				<xsl:if test="key = 'phone'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Fax :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Report/Submitter/ContactMechanism">
						  				<xsl:if test="key = 'fax'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Email :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Report/Submitter/ContactMechanism">
						  				<xsl:if test="key = 'e-mail'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>
		  			  	
		  			  <fo:table-row><fo:table-cell><fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> </fo:table-cell></fo:table-row>
		  			  	
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" space-before="5mm"> 
						  			Physician Name :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:value-of select="AdverseEventReport/Physician/firstName"/> 
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
									<xsl:value-of select="AdverseEventReport/Physician/lastName"/>
						  		</fo:block>     							
      						</fo:table-cell>
      					</fo:table-row>
      					
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Phone :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Physician/ContactMechanism">
						  				<xsl:if test="key = 'phone'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Fax :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Physician/ContactMechanism">
						  				<xsl:if test="key = 'fax'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Email :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:for-each select="AdverseEventReport/Physician/ContactMechanism">
						  				<xsl:if test="key = 'e-mail'">
						  					<xsl:value-of select="value"/>
						  				</xsl:if>
						  			</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>	  			  
		  			</fo:table-body>
		  		</fo:table>
		  		
				<fo:block>
					<fo:leader leader-length="95%" leader-pattern="rule"/>
				</fo:block>			  		
 
 		  		<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Patient Information 
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="12%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="10%"/>
										
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Patient ID :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/studySubjectIdentifier"/>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Birth Date :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
										<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/monthString"/>/
										<xsl:if test="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/dayString">
											<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/dayString"/>/
										</xsl:if>
										<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth/yearString"/>					  		
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Gender :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/gender"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>
		  			  
 					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Race :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/race"/>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Ethnicity :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/ethnicity"/> 
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>	
     					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Height(<xsl:value-of select="AdverseEventReport/ParticipantHistory/height/unit"/> ) :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/ParticipantHistory/height/quantity"/> 
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Weight(<xsl:value-of select="AdverseEventReport/ParticipantHistory/weight/unit"/> ) :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/ParticipantHistory/weight/quantity"/>  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Body Surface Area :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/ParticipantHistory/bsa" />
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>		
						 <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Baseline performance status at initiation of protocol - ECOG/Zubrod scale :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
      							<xsl:variable name="statusVar" select="AdverseEventReport/ParticipantHistory/baselinePerformanceStatus"/>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="substring($statusVar,1,1)"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>	  		

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
									Disease Name :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/DiseaseHistory/CtepStudyDisease/DiseaseTerm/ctepTerm"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>
		  			  		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
									Disease Name Not Listed	:
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			  <xsl:value-of select="AdverseEventReport/DiseaseHistory/otherPrimaryDisease"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Primary Site of Disease :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
							  		<xsl:if test="AdverseEventReport/DiseaseHistory/AnatomicSite/name != '' and AdverseEventReport/DiseaseHistory/AnatomicSite/category != 'Other'">
							  			<xsl:value-of select="AdverseEventReport/DiseaseHistory/AnatomicSite/name"/>
							  		</xsl:if>
							  		
							  		<xsl:if test="AdverseEventReport/DiseaseHistory/otherPrimaryDiseaseSite != '' and AdverseEventReport/DiseaseHistory/AnatomicSite/category = 'Other'">
							  			<xsl:value-of select="AdverseEventReport/DiseaseHistory/otherPrimaryDiseaseSite"/>
							  		</xsl:if>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Date of Initial Diagnosis :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  		<!--
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/DiseaseHistory/diagnosisDate"/>
			   						</xsl:call-template>	
			   					-->
			   					<xsl:if test="AdverseEventReport/DiseaseHistory/diagnosisDate/monthString">
			   						<xsl:value-of select="AdverseEventReport/DiseaseHistory/diagnosisDate/monthString"/>/<xsl:value-of select="AdverseEventReport/DiseaseHistory/diagnosisDate/yearString"/>					  		
								</xsl:if>
						  		</fo:block>      							
      						</fo:table-cell>
		  			  </fo:table-row>
		  			  		  			  
		  			  		  			  	  
		  			</fo:table-body>
		  		</fo:table>
		  		
				<fo:block>
					<fo:leader leader-length="95%" leader-pattern="rule"/>
				</fo:block>			  		
	  
		  		<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Course Information
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="70%"/>
										
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Treatment Assignment Code :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
 					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Description :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Start date of first course :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/TreatmentInformation/firstCourseDate"/>
			   						</xsl:call-template>	
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Start date of course associated with Expedited Report :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/TreatmentInformation/AdverseEventCourse/date"/>
			   						</xsl:call-template>						  			
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Start date of primary AE :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			End date of primary AE :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Course Number on which event(s) occurred :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/AdverseEventCourse/number"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Total number of courses to date :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/totalCourses"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Was Investigational Agent(s) administered on this protocol?:
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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
					<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	

				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Description of Event 
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>

										
		  			<fo:table-body>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm"> 
						  			Description and Treatment of Event :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/eventDescription"/>									
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
      					
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Present Status :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
				  		            <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'INTERVENTION_CONTINUES'">Intervention for AE Continues</xsl:if>
					                <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERING'">Recovering/Resolving</xsl:if>
					                <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERED_WITH_SEQUELAE'">Recovered/Resolved with Sequelae</xsl:if>
					                <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'RECOVERED_WITHOUT_SEQUELAE'">Recovered/Resolved without Sequelae</xsl:if>
					                <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'NOT_RECOVERED'">Not recovered/Not resolved</xsl:if>
					                <xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'DEAD'">Fatal/Died</xsl:if>
				  			
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Date of Recovery or Death :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/recoveryDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>      						
		  			  </fo:table-row>

		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Retreated :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:if test="AdverseEventReport/AdverseEventResponseDescription/retreated = 'false'">No</xsl:if>
						  			<xsl:if test="AdverseEventReport/AdverseEventResponseDescription/retreated = 'true'">Yes</xsl:if>
						  		</fo:block>      							
      						</fo:table-cell>     						
		  			  </fo:table-row>
		  			  
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Removed from Protocol Treatment (to date) :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:choose>
						  				<xsl:when test="AdverseEventReport/AdverseEventResponseDescription/dateRemovedFromProtocol">Yes</xsl:when>
						  				<xsl:otherwise>No</xsl:otherwise>
						  			</xsl:choose>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Date Removed from Protocol Treatment :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/dateRemovedFromProtocol"/>
			   						</xsl:call-template>						  		
						  		</fo:block>      							
      						</fo:table-cell>      						
		  			  </fo:table-row>
		  			  
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Cause of Death :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/causeOfDeath"/>		
						  		</fo:block>      							
      						</fo:table-cell>      						
		  			  </fo:table-row>
		  			  
		  			    <fo:table-row>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Death Date :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  		<xsl:if test="AdverseEventReport/AdverseEventResponseDescription/presentStatus = 'DEAD'">
						  			<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/AdverseEventResponseDescription/recoveryDate"/>
			   						</xsl:call-template>	
			   					</xsl:if>					  			
						  		</fo:block> 
						  		     							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Autopsy Performed :  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 	
						  			<xsl:if test="AdverseEventReport/AdverseEventResponseDescription/autopsyPerformed = 'false'">No</xsl:if>
						  			<xsl:if test="AdverseEventReport/AdverseEventResponseDescription/autopsyPerformed = 'true'">Yes</xsl:if>
						  		</fo:block>      							
      						</fo:table-cell>      						
		  			  </fo:table-row>		  			  		  			  		  			  
	  			</fo:table-body>
		  		</fo:table>
		  		
		  		<fo:block break-after="page"/>
		  			<!--					  		  		
					<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	-->


		  		
		  		<xsl:if test="AdverseEventReport/RadiationIntervention">
				<fo:block>
					<fo:leader leader-length="95%" leader-pattern="rule"/>
				</fo:block>
				
				
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Radiation Intervention
		  		</fo:block>
		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
    				
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="70%"/>
										
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Treatment Arm :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
 					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Description of Treatment Arm :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Type of Radiation Administration :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
								  <xsl:choose>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_HDR' ">
					                         Brachytherapy HDR 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_LDR' ">
					                         Brachytherapy LDR 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'BT_NOS' ">
					                         Brachytherapy NOS 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_NOS' ">
					                         External Beam NOS 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_2D' ">
					                         External Beam, 2D 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_3D' ">
					                         External Beam, 3D 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_IMRT' ">
					                         External Beam, IMRT 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'EB_PROTON' ">
					                         External Beam, Proton 
					                    </xsl:when>
					                    <xsl:when test="AdverseEventReport/RadiationIntervention/administration = 'SYSTEMIC_RADIOTHERAPY' ">
					                         Systemic radiotherapy 
					                    </xsl:when>
					                    <xsl:otherwise>
					                         
					                            <xsl:value-of select="AdverseEventReport/RadiationIntervention/administration"/>
					                         
					                    </xsl:otherwise>
					                </xsl:choose>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Total Dose (to date) :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/RadiationIntervention/dosage"/>  <xsl:value-of select="AdverseEventReport/RadiationIntervention/dosageUnit"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Date of Last Treatment :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/RadiationIntervention/lastTreatmentDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Schedule:
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>  Number of Fractions :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/RadiationIntervention/fractionNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		  			  		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>  <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> Number of Elaspsed Days :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/RadiationIntervention/daysElapsed"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Adjustment : 
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/RadiationIntervention/adjustment"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		
		  					  		  			  	  
		  			</fo:table-body>
		  		
		  		</fo:table>		
		  		</xsl:if>	
		  		
		  		<xsl:if test="AdverseEventReport/SurgeryIntervention">
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
					
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Surgery Intervention
		  		</fo:block>
		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
    				
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
										
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Treatment Arm :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
 					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Description of Treatment Arm :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
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
<!--
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Site of Intervention (Category) :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/SurgeryIntervention/AnatomicSite/category"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
-->		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Site of Intervention :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/SurgeryIntervention/InterventionSite/name"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Date of Intervention :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/SurgeryIntervention/interventionDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			</fo:table-body>
		  		</fo:table>								  		  		
				
				</xsl:if>
				
				<!--<fo:block break-before="page"/> -->
		  		
		  		<xsl:if test="AdverseEventReport/MedicalDevice">
		  		
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Medical Device
		  		</fo:block>		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
    				
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
										
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Brand Name :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/brandName"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
 					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Common Name :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/commonName"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Device Type :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/deviceType"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			  
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Manufacturer Name :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerName"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Manufacturer City :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerCity"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Manufacturer State/Province :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/manufacturerState"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>

		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Model Number :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/modelNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Lot Number :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/lotNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Catalog Number :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/catalogNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Expiration Date :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/expirationDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Serial Number :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/serialNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Other Number :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/otherNumber"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Operator of Device :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
				                    <xsl:choose>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'HEALTH_PROFESSIONAL'">
				                            Health Professional
				                        </xsl:when>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'PATIENT'">
				                            Lay User/Patient
				                        </xsl:when>
				                        <xsl:when test="AdverseEventReport/MedicalDevice/DeviceOperator = 'OTHER'">
				                            Other
				                        </xsl:when>
				                        <xsl:otherwise>
				                                <xsl:value-of select="AdverseEventReport/MedicalDevice/DeviceOperator"/>
				                        </xsl:otherwise>
				                    </xsl:choose>						  			
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Implanted Date :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/implantedDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Explanted Date :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/explantedDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Is this a Single-use Device that was Reprocessed and Reused on a Patient? :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
					                <xsl:choose>
					                    <xsl:when test="AdverseEventReport/MedicalDevice/DeviceReprocessed = 'YES'">
					                        Yes
					                    </xsl:when>
					                    <xsl:otherwise>
					                        No
					                    </xsl:otherwise>
					                </xsl:choose>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Name of Reprocessor :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/reprocessorName"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Address of Reprocessor :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/MedicalDevice/reprocessorAddress"/>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>		  			    		  			    		  			    		  			    		  			    		  			    		  			    		  			    		  			    		  			    		  			    		  			    
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell >
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Device Available for Evaluation? (Do not send to FDA) :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
				                <xsl:choose>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'YES'">
				                        Yes
				                    </xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'NO'">
				                        No
				                    </xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'RETURNED'">
				                        Returned
				                    </xsl:when>
				                    <xsl:when test="AdverseEventReport/MedicalDevice/EvaluationAvailability = 'UNKNOWN'">
				                       Unknown
				                    </xsl:when>
				                    <xsl:otherwise>
				                            <xsl:value-of select="AdverseEventReport/MedicalDevice/EvaluationAvailability"/>
				                    </xsl:otherwise>
				                </xsl:choose>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" margin-left="2mm" > 
						  			Returned Date :
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="AdverseEventReport/MedicalDevice/returnedDate"/>
			   						</xsl:call-template>
						  		</fo:block>      							
      						</fo:table-cell>
		  			    </fo:table-row>			  			    

		  			</fo:table-body>
		  		</fo:table>	
		  			
					<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>		  		
		  		
				</xsl:if>
				
				<xsl:if test="AdverseEventReport/SAEReportPriorTherapy">

					
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Prior Therapies
		  		</fo:block>
		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
    				
		  		<fo:table>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="20%"/>
								
						
		  			<fo:table-body>
		  			
		 			  		<fo:table-row xsl:use-attribute-sets="tr-height-1" >
		     						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Therapy 
							  		</fo:block>      							
		     						</fo:table-cell>
		     						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Therapy Start Date
							  		</fo:block>      							
		     						</fo:table-cell>
		     						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Therapy End Date
							  		</fo:block>      							
		     						</fo:table-cell>
		     						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Comments 
							  		</fo:block>      							
		     						</fo:table-cell>
		     						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Chemotherapy Agents 
							  		</fo:block>      							
		     						</fo:table-cell>      						      						      						
			  			    </fo:table-row> 
	  			    		  			

		  	      			
						<xsl:for-each select="AdverseEventReport/SAEReportPriorTherapy">
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			<xsl:value-of select="PriorTherapy/text"/>
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		<xsl:if test="startDate/monthString">
										<xsl:value-of select="startDate/monthString"/>/<xsl:value-of select="startDate/dayString"/>/<xsl:value-of select="startDate/yearString"/>
									</xsl:if>
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		<xsl:if test="endDate/monthString">
			   							<xsl:value-of select="endDate/monthString"/>/<xsl:value-of select="endDate/dayString"/>/<xsl:value-of select="endDate/yearString"/>
			   						</xsl:if>
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  				<xsl:value-of select="other"/>
							  		</fo:block>      							
	      						</fo:table-cell>
	      						
	      						<!--
	      						<fo:table-cell>
	      							<xsl:for-each select="PriorTherapyAgent">
							  			<fo:block xsl:use-attribute-sets="normal" > 
							  				<xsl:value-of select="ChemoAgent/name"/>
							  			</fo:block> 
							  		</xsl:for-each>     							
	      						</fo:table-cell>    
	      						-->
	      						
	      						<xsl:if test="PriorTherapyAgent">
		      						<fo:table-cell>
		      							<xsl:for-each select="PriorTherapyAgent">
								  			<fo:block xsl:use-attribute-sets="normal" > 
								  				<xsl:value-of select="ChemoAgent/name"/>
								  			</fo:block> 
								  		</xsl:for-each>     							
		      						</fo:table-cell> 
	      						</xsl:if>  
	      						
	      						  						      						      						      						
			  			    </fo:table-row> 
		  			    </xsl:for-each>  			  
 					


		  			</fo:table-body>
		  		</fo:table>	
		  		
		  		</xsl:if>
		  		
		  		<xsl:if test="AdverseEventReport/SAEReportPreExistingCondition">
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
					
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Pre-Existing Conditions
		  		</fo:block>
				<xsl:for-each select="AdverseEventReport/SAEReportPreExistingCondition">
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="PreExistingCondition/text"/><xsl:value-of select="other"/>
						  		</fo:block> 				
				</xsl:for-each>
						  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:block xsl:use-attribute-sets="normal" >  </fo:block>
				</xsl:if>
				
				<xsl:if test="AdverseEventReport/DiseaseHistory/MetastaticDiseaseSite">
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
					
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Sites of Metastatic Disease
		  		</fo:block>
				<xsl:for-each select="AdverseEventReport/DiseaseHistory/MetastaticDiseaseSite">
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:if test="AnatomicSite/name != '' and AnatomicSite/category != 'Other'">
						  				<xsl:value-of select="AnatomicSite/name"/>
						  			</xsl:if>
						  			<xsl:if test="otherSite != ''">
						  				<xsl:value-of select="otherSite"/>
						  			</xsl:if>
						  		</fo:block> 				
				</xsl:for-each>		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:block xsl:use-attribute-sets="normal" >  </fo:block>		  		
				</xsl:if>
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	

  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Protocol Agents
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:block xsl:use-attribute-sets="label" > Treatment Assignment Code : 
				 <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> 
				 <xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>
				 </fo:block>
				
		  		<fo:table>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
																				
		  			<fo:table-body>
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Agent
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Total Dose Administered this Course
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Last Administered Date
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Comments 
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Agent Adjustment
						  		</fo:block>      							
      						</fo:table-cell>      						      						      						
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Agent Delayed
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="label" > 
						  			Delay
						  		</fo:block>      							
      						</fo:table-cell> 
		  			    </fo:table-row>
		  			  
 					<xsl:for-each select="AdverseEventReport/TreatmentInformation/CourseAgent">
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="StudyAgent/Agent/name"/>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="Dose/amount"/>
						  			<xsl:value-of select="Dose/units"/>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
									<xsl:variable name="trimmedlastAdministeredDate">
										<xsl:call-template name="trim">
									        <xsl:with-param name="s" select="lastAdministeredDate"/>
			   							</xsl:call-template>									
									</xsl:variable>
									<xsl:call-template name="standard_date">
									        <xsl:with-param name="date" select="$trimmedlastAdministeredDate"/>
			   						</xsl:call-template>
	
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="comments"/>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
					                <xsl:if test="AgentAdjustment='DOSE_INCREASED'">
					                	Dose increased
					                </xsl:if>
					                <xsl:if test="AgentAdjustment='DOSE_NOTCHANGED'">
					                	Dose not changed
					                </xsl:if>
					                <xsl:if test="AgentAdjustment='DOSE_REDUCED'">
					                	Dose reduced
					                </xsl:if>
					                <xsl:if test="AgentAdjustment='DRUG_WITHDRAWN'">
					                	Drug withdrawn
					                </xsl:if>
					                <xsl:if test="AgentAdjustment='NA'">
					                	Not applicable
					                </xsl:if>						  			
						  		</fo:block>      							
      						</fo:table-cell>      						      						      						      						
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
					                <xsl:choose>
					                    <xsl:when test="administrationDelayAmount">
					                        Yes
					                    </xsl:when>
					                    <xsl:otherwise>
					                        No
					                    </xsl:otherwise>
					                </xsl:choose>						  			
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell>
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="administrationDelayAmount"/> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> <xsl:value-of select="administrationDelayUnits"/>
						  		</fo:block>      							
      						</fo:table-cell>  
		  			    </fo:table-row>
					 </xsl:for-each>
		  			</fo:table-body>
		  		</fo:table>	

		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
				
			<xsl:if test = "AdverseEventReport/ConcomitantMedication"	>
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Concomitant Medications
		  		</fo:block>
		  	</xsl:if>	
		  		
		  		
		  			<xsl:for-each select="AdverseEventReport/ConcomitantMedication">
			  			<fo:block xsl:use-attribute-sets="normal" > 
			  				<xsl:value-of select="name"/>  
			  			</fo:block>
			  		</xsl:for-each>
		  	<xsl:if test = "AdverseEventReport/ConcomitantMedication"	>	
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
			</xsl:if>	
			
			<xsl:if test = "AdverseEventReport/OtherCause"	>				
  				<fo:block xsl:use-attribute-sets="sub-head" > 
		  			Other Contributing Causes
		  		</fo:block>
		   </xsl:if>			

		  			<xsl:for-each select="AdverseEventReport/OtherCause">
			  			<fo:block xsl:use-attribute-sets="normal" > 
			  				<xsl:value-of select="text"/>  
			  			</fo:block>
			  		</xsl:for-each>	  		
			<xsl:if test = "AdverseEventReport/OtherCause"	>	
		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
			</xsl:if>
		  		
  				<fo:block  xsl:use-attribute-sets="sub-head" > 
					<xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventCtcTerm/universal-term">
		  				Adverse Events (CTCAE)
		  			</xsl:if>
		  			<xsl:if test="AdverseEventReport/AdverseEvent/AdverseEventMeddraLowLevelTerm/universalTerm">
		  				Adverse Events (MedDRA)
		  			</xsl:if>
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>


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
						                	<xsl:if test="AdverseEventCtcTerm/ctc-term/otherRequired = 'true'">: <xsl:value-of select="detailsForOther"/></xsl:if>
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
							  			<xsl:when test="AdverseEventCtcTerm/universal-term = ../Summary[@id='Primary AE']/value">
							  				Yes
							  			</xsl:when>
							  			<xsl:otherwise>
							  				No
							  			</xsl:otherwise>
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


		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	

  				<fo:block  xsl:use-attribute-sets="sub-head" > 
		  			Attribution for Adverse Events
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		

		  	<xsl:for-each select="AdverseEventReport/AdverseEvent"> 
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="30%"/>
		  			<fo:table-body>
			
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="label" > 
							  			Attribute to 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
	      							<xsl:variable name="gradeVar" select="grade"/>
	      						
							  		<fo:block xsl:use-attribute-sets="label" > 
							  		
							  			Gr.<xsl:value-of select="substring($gradeVar,1,1)"/>
							  			
							  			<xsl:text disable-output-escaping="yes">&amp;#160;   </xsl:text><xsl:value-of select="AdverseEventCtcTerm/universal-term"/><xsl:value-of select="AdverseEventMeddraLowLevelTerm/universalTerm"/>
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>

		  			</fo:table-body>
		  		</fo:table>	
		  		<fo:block space-after="0.2pt">
							<fo:leader leader-length="95%" leader-pattern="rule" rule-thickness="0.2pt"/>
				</fo:block>	
		  		<fo:table>
					<fo:table-column column-width="30%"/>
					<fo:table-column column-width="30%"/>
		  			<fo:table-body>
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			Course 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="CourseAgentAttribution"> 
				  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
		      						<fo:table-cell>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="CourseAgent/StudyAgent/Agent/name"/>
								  		</fo:block>      							
		      						</fo:table-cell>
		      						<fo:table-cell>
		      							<xsl:variable name="attributionVar1" select="attribution"/>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:value-of select="substring($attributionVar1,4,20)"/>
								  		</fo:block>      							
		      						</fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
			  	<xsl:if test="ConcomitantMedicationAttribution">		
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			Concomitant medications 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>
			  			    <xsl:for-each select="ConcomitantMedicationAttribution"> 
				  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
		      						<fo:table-cell>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="ConcomitantMedication/name"/> <xsl:value-of select="ConcomitantMedication/other"/>
								  		</fo:block>      							
		      						</fo:table-cell>
		      						<fo:table-cell>
		      							<xsl:variable name="attributionVar2" select="attribution"/>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:value-of select="substring($attributionVar2,4,20)"/>
								  		</fo:block>      							
		      						</fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
			  	</xsl:if>	
			  	<xsl:if test="OtherCauseAttribution">		    
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			Other causes 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="OtherCauseAttribution"> 
				  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
		      						<fo:table-cell>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="OtherCause/text"/>
								  		</fo:block>      							
		      						</fo:table-cell>
		      						<fo:table-cell>
		      							<xsl:variable name="attributionVar3" select="attribution"/>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:value-of select="substring($attributionVar3,4,20)"/>
								  		</fo:block>      							
		      						</fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
				</xsl:if>
			  	<xsl:if test="DiseaseAttribution">		    
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			Disease 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>

			  			    <xsl:for-each select="DiseaseAttribution"> 
				  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
		      						<fo:table-cell>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="DiseaseHistory/CtepStudyDisease/DiseaseTerm/ctepTerm"/>
								  		</fo:block>      							
		      						</fo:table-cell>
		      						<fo:table-cell>
		      							<xsl:variable name="attributionVar4" select="attribution"/>
								  		<fo:block xsl:use-attribute-sets="normal" > 
								  			<xsl:value-of select="substring($attributionVar4,4,20)"/>
								  		</fo:block>      							
		      						</fo:table-cell>
				  			    </fo:table-row>
			  			    </xsl:for-each>
				</xsl:if>
				<!-- Added Radiation -->
                <xsl:if test="RadiationAttribution">          
                            <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                        Radiation 
                                    </fo:block>                                 
                                </fo:table-cell>
                                
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                    </fo:block>                                 
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:for-each select="RadiationAttribution"> 
                                <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                    <fo:table-cell>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="RadiationIntervention/OtherIntervention/name"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <xsl:variable name="attributionVar5" select="attribution"/>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:value-of select="substring($attributionVar5,4,20)"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                </xsl:if>
                <!-- Added Device -->
                <xsl:if test="DeviceAttribution">          
                            <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                        Medical devices 
                                    </fo:block>                                 
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                    </fo:block>                                 
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:for-each select="DeviceAttribution"> 
                                <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                    <fo:table-cell>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="MedicalDevice/StudyDevice/Device/commonName"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <xsl:variable name="attributionVar6" select="attribution"/>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:value-of select="substring($attributionVar6,4,20)"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                </xsl:if>
			  	<!-- Added Surgery -->
                <xsl:if test="SurgeryAttribution">          
                            <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                        Surgery
                                    </fo:block>                                 
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block xsl:use-attribute-sets="normal" > 
                                    </fo:block>                                 
                                </fo:table-cell>
                            </fo:table-row>

                            <xsl:for-each select="SurgeryAttribution"> 
                                <fo:table-row xsl:use-attribute-sets="tr-height-1" >
                                    <fo:table-cell>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;   </xsl:text><xsl:value-of select="SurgeryIntervention/InterventionSite/name"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <xsl:variable name="attributionVar7" select="attribution"/>
                                        <fo:block xsl:use-attribute-sets="normal" > 
                                            <xsl:value-of select="substring($attributionVar7,4,20)"/>
                                        </fo:block>                                 
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                </xsl:if>	    
			  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  			 
							  		</fo:block>      							
	      						</fo:table-cell>
	      						<fo:table-cell>
							  		<fo:block xsl:use-attribute-sets="normal" > 
							  		</fo:block>      							
	      						</fo:table-cell>
			  			    </fo:table-row>
			  			    			  			    
			  			    			  			    			  			    
		  			</fo:table-body>
		  		</fo:table>					
				
			 </xsl:for-each>	
			 	  			
	  		
	  									  				  		

		  			<fo:block>
						<fo:leader leader-length="95%" leader-pattern="rule"/>
					</fo:block>	
				
				<xsl:if test="AdverseEventReport/Lab">	

  				<fo:block  xsl:use-attribute-sets="sub-head" > 
		  			Abnormal and Relevant Normal Lab Results
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>


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
		  	      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Lab</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Baseline date</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Worst Date</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Recovery/Latest Date</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Value</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Microbiology Site</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Date</fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="label" >Infectious Agent</fo:block>
      						</fo:table-cell> 
		  			    </fo:table-row>
		  	
 					 <xsl:for-each select="AdverseEventReport/Lab"> 
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="labTerm/term"/><xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text><xsl:value-of select="other"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="baseline/date"/></xsl:call-template></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="baseline/value"/><xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="nadir/date"/></xsl:call-template></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="nadir/value"/><xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:call-template name="standard_date"><xsl:with-param name="date" select="recovery/date"/></xsl:call-template></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="recovery/value"/><xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text><xsl:value-of select="units"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="site"/></fo:block></fo:table-cell>
      						<fo:table-cell><fo:block xsl:use-attribute-sets="normal" ></fo:block></fo:table-cell>
                            <fo:table-cell><fo:block xsl:use-attribute-sets="normal" ><xsl:value-of select="infectiousAgent"/></fo:block></fo:table-cell>
		  			    </fo:table-row>
					</xsl:for-each>
		  			</fo:table-body>
		  		</fo:table>		
		  		</xsl:if>	  		
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
