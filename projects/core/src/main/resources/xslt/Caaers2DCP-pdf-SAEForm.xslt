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
  <xsl:attribute-set name="tr-height-2">
    <xsl:attribute name="height">8mm</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="small-cell">
    <xsl:attribute name="padding">6pt</xsl:attribute>
    <xsl:attribute name="border">0.5pt solid black</xsl:attribute>
  </xsl:attribute-set>       

  <xsl:template match="/">
  	
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	    
		<fo:layout-master-set>
		  	<fo:simple-page-master master-name="A4" margin-left="2mm" margin-top="2mm" margin-right="0.25in">
		    	<fo:region-body margin-top="0.5in"/>
		    	<fo:region-before extent="2in"/>
		    	<fo:region-after extent="0.5in"/>
		  	</fo:simple-page-master>


		</fo:layout-master-set>
		
		<fo:page-sequence master-reference="A4">
			
		<fo:static-content flow-name="xsl-region-after">

				<fo:block font-size="8pt" font-family="arial" text-align-last="right"> 
						Page <fo:page-number/> of <fo:page-number-citation ref-id="content_terminator"/>
				</fo:block>
		</fo:static-content>
				
		  <fo:static-content flow-name="xsl-region-before">
		    <fo:table>
				<fo:table-column column-width="100%"/>
				<fo:table-body>
				  <fo:table-row>
				    <fo:table-cell>
				    	<fo:block font-size="10pt" font-family="Times New Roman" text-align-last="left">
				    		NCI Contract/Grant No.		 
				    	</fo:block>
				    	<fo:block font-size="10pt" font-family="Times New Roman" text-align-last="left">
				    		IRB Protocol No.		 
				    	</fo:block>
				    </fo:table-cell>
				  </fo:table-row>
				</fo:table-body>				
			</fo:table>									
		  </fo:static-content>
		  
		  <fo:flow flow-name="xsl-region-body">		  	
   			  <fo:block font-size="14pt" font-family="Times New Roman" text-align-last="center" display-align="center">
		      	NCI, DIVISION OF CANCER PREVENTION (DCP)
		      </fo:block>
		      <fo:block font-size="14pt" font-family="Times New Roman" text-align-last="center" display-align="center">
			      SERIOUS ADVERSE EVENT FORM
			  </fo:block>
				<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		      <fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left">
			      REQUIRED FIELDS ON ALL REPORTS
			  </fo:block>
   

		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="33%"/>
					<fo:table-column column-width="33%"/>
					<fo:table-column column-width="34%"/>

										
		  			<fo:table-body >
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Today's Date: 
						  				<xsl:value-of select="java:format(java:java.text.SimpleDateFormat.new ('MM/dd/yyyy'), java:java.util.Date.new())"/>				      
				  
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Sponsor: NCI, DCP
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell"  number-rows-spanned="2" >
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Study (Indication):
									<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Study/shortTitle"/>
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Drug(s) under Investigation:
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									IND No.:
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>

		  			</fo:table-body>
		  		</fo:table>


				<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		      <fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left">
			      A. Study Subject Information 
			  </fo:block>
   

		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="25%"/>
					<fo:table-column column-width="25%"/>
					<fo:table-column column-width="25%"/>
					<fo:table-column column-width="25%"/>
										
		  			<fo:table-body >
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			1.  Study Participant # or  PID #   
						  			 <xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/Participant/Identifier/value"/>
						  		</fo:block> 
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									2.  Year of Birth:   
									<xsl:value-of select="substring(AdverseEventReport/StudyParticipantAssignment/Participant/dateOfBirth, 1, 4)" />
						  		</fo:block>
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									3.  Weight at Time of Event:  
									<xsl:value-of select="AdverseEventReport/ParticipantHistory/weight/quantity"/>
						  		</fo:block>   
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal">						  		
						  			[<xsl:if test="AdverseEventReport/ParticipantHistory/weight/unit = 'Kilogram'">
						  				x
						  			</xsl:if>] kg <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  				
						  			[ <xsl:if test="AdverseEventReport/ParticipantHistory/weight/unit = 'Pound'">
						  				x
						  			</xsl:if>] lbs <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			
						  			[ <xsl:if test="AdverseEventReport/ParticipantHistory/weight/unit = ''">
						  				x
						  			</xsl:if>] not avialable
						  		 </fo:block>   							
      						</fo:table-cell>
      						
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									4. Height at Time of Event:
									<xsl:value-of select="AdverseEventReport/ParticipantHistory/height/quantity"/>
						  		</fo:block>      							
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal">						  		
						  			[<xsl:if test="AdverseEventReport/ParticipantHistory/height/unit = 'Centimeter'">
						  				x
						  			</xsl:if>] cm <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  				
						  			[ <xsl:if test="AdverseEventReport/ParticipantHistory/height/unit = 'Inch'">
						  				x
						  			</xsl:if>] in <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			
						  			[ <xsl:if test="AdverseEventReport/ParticipantHistory/height/unit = ''">
						  				x
						  			</xsl:if>] not avialable
						  		 </fo:block> 

      						</fo:table-cell>
      					</fo:table-row>
		  			</fo:table-body>
		  		</fo:table>


				<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		      <fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left">
			      B. Event Information 
			  </fo:block>
   

		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="33%"/>
					<fo:table-column column-width="33%"/>
					<fo:table-column column-width="34%"/>

										
		  			<fo:table-body >
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			[  ]  Initial Event Report  
						  		</fo:block>      				
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			[  ]  Follow-up  
						  		</fo:block> 						  					
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  		Gender:  <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text> 
						  		[ <xsl:if test="AdverseEventReport/StudyParticipantAssignment/Participant/gender = 'Male'">
						  				x
						  			</xsl:if>] M <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		[ <xsl:if test="AdverseEventReport/StudyParticipantAssignment/Participant/gender = 'Female'">
						  				x
						  			</xsl:if>] F <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Dose at Event: 
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Event Onset Date: 
				                    <xsl:call-template name="standard_date">
				                        <xsl:with-param name="date" select="AdverseEventReport/AdverseEvent/startDate"/>
				                    </xsl:call-template>						  			
						  		</fo:block>      				
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			(Month/Day/Year)  
						  		</fo:block> 						  					
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="2" number-rows-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Primary Event (diagnosis):
									<xsl:for-each select="AdverseEventReport/AdverseEvent">
										<xsl:if test="substring(gridId,1,3) = 'PRY'">
                        					<fo:block xsl:use-attribute-sets="normal" > <xsl:value-of select="AdverseEventCtcTerm/ctc-term/term"/> </fo:block> 
                    					</xsl:if>
									</xsl:for-each>
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Event Approx. Time:
						  		</fo:block>      				
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			(Indicate A.M./P.M.)  
						  		</fo:block> 						  					
      						</fo:table-cell>
      					</fo:table-row>
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Event Occurred at:
						  		</fo:block>      										  					
      						</fo:table-cell>
      					</fo:table-row>
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Duration of Drug Exposure at Event:
						  		</fo:block>      				 						  					
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Primary Treatment Approx. Time (A.M./P.M.):									
						  		</fo:block>    
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>  							
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Primary Treatment of Event:			
									<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>				
						  		</fo:block> 
      						</fo:table-cell>
      					</fo:table-row>      					
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Attending Physician (Name):		
									<xsl:value-of select="AdverseEventReport/Physician/firstName"/>
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
									<xsl:value-of select="AdverseEventReport/Physician/lastName"/>					
						  		</fo:block>    
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>  							
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Phone/FAX No.:	
									<xsl:for-each select="AdverseEventReport/Physician/ContactMechanism">
					                    <xsl:if test="key = 'phone'">
					                        <xsl:value-of select="value"/>
					                    </xsl:if>
					                </xsl:for-each>
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>  							
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Hospital/Clinic:					
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>  							
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Address:					
						  		</fo:block> 						  		
      						</fo:table-cell>
      					</fo:table-row>  
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Describe Event (if applicable, include dates of hospitalization for event):						
						  		</fo:block>    	
						  		<fo:block xsl:use-attribute-sets="normal" > 
						  			<xsl:value-of select="AdverseEventReport/AdverseEventResponseDescription/eventDescription"/>
						  		</fo:block>
						  							  		
      						</fo:table-cell>
      					</fo:table-row> 
						<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Form Completed by:  (Print Name) 
									<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
									Title						
						  		</fo:block>    
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>  							
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Investigator Signature 
									<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160; &amp;#160;</xsl:text>
									Date   (Month/Day/Year)
									<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
									Phone No						
						  		</fo:block> 
						  		
      						</fo:table-cell>
      					</fo:table-row>      					
		  			</fo:table-body>
		  		</fo:table>
		  		
		  		
	  			<fo:block break-before="page" font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left"> 
		  			ALL FIELDS APPEARING IN THE FOLLOWING PAGES (C F) MUST BE COMPLETED FOR THE INITIAL REPORT; THEREAFTER, FILL IN ONLY SECTIONS THAT PROVIDE  ADDITIONAL/ CORRECTIVE INFORMATION. 
		  		</fo:block>
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
	  			<fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left"> 
		  			C.  Site information
		  		</fo:block>		  		
	  			<fo:table border="0.5pt solid black">


										
		  			<fo:table-body >
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			1.  Investigator Name :
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Organization/SiteInvestigator/Investigator/firstName"/>
						  			<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
						  			<xsl:value-of select="AdverseEventReport/StudyParticipantAssignment/StudySite/Organization/SiteInvestigator/Investigator/lastName"/>
						  		</fo:block>      							
      						</fo:table-cell>

      					</fo:table-row>
      					
		  			    <fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			2.  Address
						  		</fo:block>      							
      						</fo:table-cell>

      					</fo:table-row>

		  			</fo:table-body>
		  		</fo:table>
		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
	  			<fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left"> 
		  			D.  Suspect Medication(s)
		  		</fo:block>	
		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="40%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="9%"/>
					<fo:table-column column-width="9%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="9%"/>
					<fo:table-column column-width="9%"/>
					<fo:table-column column-width="8%"/>
										
		  			<fo:table-body >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="8">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			1.  Study Design:  [  ] Blind  <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text> 
						  			[  ] Open/Unblind
						  		</fo:block>      				
	
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>					  					
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Possible Dose (e.g., 300 mg) ____ 
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>Frequency (e.g., qd) __ 
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>Route (e.g., po)__
						  		</fo:block>
      						</fo:table-cell>		  			
		  			</fo:table-row >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="2" number-rows-spanned="2">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			2. Study Drug
						  		</fo:block>      				
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/CourseAgent/StudyAgent/Agent/name"/>
						  		</fo:block>
      						</fo:table-cell>		  			
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="6">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Formulation (e.g., tablet, solution)
						  		</fo:block>      				
      						</fo:table-cell>
		  			</fo:table-row >					
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >		  			
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="6">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Lot No. (If known)
						  		</fo:block>      				
      						</fo:table-cell>
		  			</fo:table-row >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="8">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			3.  Start Date of Study Drug (Month/Day/Year):
						  		    <xsl:call-template name="standard_date">
				                        <xsl:with-param name="date" select="AdverseEventReport/TreatmentInformation/firstCourseDate"/>
				                    </xsl:call-template>						  			
						  		</fo:block>      				
      						</fo:table-cell>		  			
		  			</fo:table-row >		  			
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="8">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			4. Was blind broken due to event? 
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>                           
						  			[ ] No    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>                             
						  			[ ] Yes   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[ ] NA
						  		</fo:block>      				
      						</fo:table-cell>		  			
		  			</fo:table-row >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="8">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			5.  Was Study Drug stopped/interrupted/reduced in response to event?  
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[  ] No  
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[  ] Yes 
						  		</fo:block>  
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm">
						  		>> If yes, complete a-e: 
						  		</fo:block>  
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  		     a.  If stopped, specify date study drug last taken (Month/Day/Year):      [  ] NA
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  		b.  If reduced, specify: New dose _  Date reduced (Month/Day/Year) _    [  ] NA
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm">
						  		     c.  If interrupted, specify total number of days not given:  __________  	[  ] NA 
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  		     d.  Did event abate after study drug was stopped or dose reduced? 
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>           	
						  		     [  ] NA     
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		     [  ] Yes     
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		     [  ] No  
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  		     e.  Did event reappear after study drug was reintroduced?     
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>                    	
						  		     [  ] NA     
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		     [  ] Yes    
						  		     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  		      [  ] No 
						  		</fo:block> 
						  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
 						  		  				
      						</fo:table-cell>		  			
		  			</fo:table-row >
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="8">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			6.  Was patient taking any other medications concomitantly at the time of the event?	
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[ <xsl:if test="AdverseEventReport/ConcomitantMedication/name = ''">
						  			x </xsl:if>] No   
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[ <xsl:if test="AdverseEventReport/ConcomitantMedication/name != ''">
						  			x </xsl:if>] Yes  
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			>>  If yes, complete below. 
   
						  		</fo:block>   
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										   (DO NOT LIST DRUGS USED TO TREAT EVENT)

						  		</fo:block>    										  					
      						</fo:table-cell>
      				</fo:table-row>		  			
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
						  			Drug Name  
						  		</fo:block>      				
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
						  			Doses (units, frequency, route, indication for use) 
						  		</fo:block> 						  					
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" text-align-last="center"> 
									Start Date
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="4">
						  		<fo:block xsl:use-attribute-sets="normal" text-align-last="center"> 
									Stop Date  
						  		</fo:block>      							
						  		<fo:block xsl:use-attribute-sets="normal" text-align-last="center"> 
									or mark (X) if continuing  
						  		</fo:block> 
      						</fo:table-cell>
      					</fo:table-row>
					<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			 
						  		</fo:block>  
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Month
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Day 
						  		</fo:block>      							
      						</fo:table-cell>
       						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Year 
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Month
						  		</fo:block>      							
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Day 
						  		</fo:block>      							
      						</fo:table-cell>
       						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									Year 
						  		</fo:block>      							
      						</fo:table-cell>
       						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" > 
									(X) 
						  		</fo:block>      							
      						</fo:table-cell>
      					</fo:table-row>
      					<xsl:for-each select="AdverseEventReport/ConcomitantMedication">
							<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		
							  			<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			 	<xsl:value-of select="name"/>
							  			</fo:block>  
							  		
	      						</fo:table-cell>
	       					</fo:table-row>
      					</xsl:for-each>      					
	  			</fo:table-body>
		  		</fo:table>
		  		
		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
	  			<fo:block break-before="page" font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left"> 
		  			E. Adverse Event
		  		</fo:block>	
		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="10%"/>
					<fo:table-column column-width="40%"/>
					<fo:table-column column-width="15%"/>
					<fo:table-column column-width="15%"/>

										
		  			<fo:table-body >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="6">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			1.	Relevant Laboratory/Diagnostic Tests  
						  			<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
						  			[ ] No tests performed
						  		</fo:block>      				
      						</fo:table-cell>		  			
		  			</fo:table-row >
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="3">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
						  			Date
						  		</fo:block>      				
      						</fo:table-cell>		  			
      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-rows-spanned="2">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
						  			Test
						  		</fo:block>      				
      						</fo:table-cell>
       						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="2">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
						  			Results
						  		</fo:block>      				
      						</fo:table-cell>     						
		  			</fo:table-row >					
		  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Month
						  		</fo:block>      				
      						</fo:table-cell>		  			
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Day
						  		</fo:block>      				
      						</fo:table-cell>
      						<fo:table-cell xsl:use-attribute-sets="small-cell">
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Year
						  		</fo:block>      				
      						</fo:table-cell>
       						<fo:table-cell xsl:use-attribute-sets="small-cell" >
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Actual Value
						  		</fo:block>      				
      						</fo:table-cell>     						
       						<fo:table-cell xsl:use-attribute-sets="small-cell" >
						  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
						  			Normal Range
						  		</fo:block>      				
      						</fo:table-cell> 
		  			</fo:table-row >
		  			<xsl:for-each select="AdverseEventReport/Lab">
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:value-of select="substring(baseline/date,6,2)"/>
							  		</fo:block>      				
	      						</fo:table-cell>		  			
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:value-of select="substring(baseline/date,9,2)"/>
							  		</fo:block>      				
	      						</fo:table-cell>
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:value-of select="substring(baseline/date,1,4)"/>
							  		</fo:block>      				
	      						</fo:table-cell>
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:value-of select="labTerm/term"/>
							  			<xsl:value-of select="other"/>
							  		</fo:block>      				
	      						</fo:table-cell>
	       						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:value-of select="baseline/value"/>   <xsl:value-of select="units"/>
							  		</fo:block>      				
	      						</fo:table-cell>     						
	       						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			
							  		</fo:block>      				
	      						</fo:table-cell> 
			  			</fo:table-row >
			  		</xsl:for-each>
		  			
	  			</fo:table-body>
		  		</fo:table>

		  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 

		  		<fo:table border="0.5pt solid black">
					<fo:table-column column-width="20%"/>
					<fo:table-column column-width="80%"/>
		  			<fo:table-body >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell" number-columns-spanned="2">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			2.	Relevant Medical History, including preexisting conditions (e.g., allergies, pregnancy, smoking and alcohol use, hepatic/renal dysfunction, medical/surgical history, etc.)
							  		</fo:block>      				
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
							  			Date (if known)
							  		</fo:block>      				
	      						</fo:table-cell>		  			
	      						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
							  			Diseases/Surgeries/Treatment
							  		</fo:block>      				
	      						</fo:table-cell>    						
			  			</fo:table-row >					
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="center"> 
							  			
							  		</fo:block>      				
	      						</fo:table-cell>		  			
	      						<fo:table-cell xsl:use-attribute-sets="small-cell" >
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="left"> 
							  			<xsl:value-of select="AdverseEventReport/DiseaseHistory/CtepStudyDisease/DiseaseTerm/ctepTerm"/>
							  		</fo:block>
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="left">	
							  			<xsl:value-of select="AdverseEventReport/TreatmentInformation/TreatmentAssignment/code"/>
							  		</fo:block>
							  		
							  			<xsl:for-each select="AdverseEventReport/SAEReportPreExistingCondition">
							  				<fo:block xsl:use-attribute-sets="normal" margin-left="2mm" text-align-last="left">	
							  					<xsl:value-of select="PreExistingCondition/text"/>
							  					<xsl:value-of select="other"/>
							  				</fo:block>
							  			</xsl:for-each>
							  		      				
	      						</fo:table-cell>    						
			  			</fo:table-row >
	  				</fo:table-body>
		  		</fo:table>
				<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
		  		<fo:table border="0.5pt solid black">

		  			<fo:table-body >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
							  			<xsl:variable name="grade" select="AdverseEventReport/AdverseEvent/grade"/>
							  			
										3. 	NCI Toxicity GRADE of the Event (use NCI Common Toxicity Criteria): 
										[ <xsl:if test="substring($grade,1,1)=1">x</xsl:if>] 1
										<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[ <xsl:if test="substring($grade,1,1)=2">x</xsl:if>] 2
										<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[ <xsl:if test="substring($grade,1,1)=3">x</xsl:if>] 3
										<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[ <xsl:if test="substring($grade,1,1)=4">x</xsl:if>] 4
										<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[ <xsl:if test="substring($grade,1,1)=5">x</xsl:if>] 5
										
									</fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										If not gradable by NCI CTC, check one of the following:  
										[  ] Mild     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Moderate     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Severe    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Life-threatening     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Fatal
							  		</fo:block>      				
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										4. Why Serious?
									</fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='DEATH'">
									  				[ x ] Results in death <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>

									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='LIFE_THREATENING'">
									  				[ x ] Is life-threatening   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text> 
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>
									  
									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='HOSPITALIZATION'">
									  				[ x ] Requires inpatient hospitalization or prolongation of existing hospitalization
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>
									  </fo:block>
									  <fo:block xsl:use-attribute-sets="normal" margin-left="2mm">
									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='DISABILITY'">
									  				[ x ] Results in persistent or significant disability/incapacity <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>	
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>

									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='CONGENITAL_ANOMALY'">
									  				[ x ] Is a congenital anomaly/birth defect	
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>
									</fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
									  <xsl:for-each select="AdverseEventReport/Outcome">
									  		<xsl:choose>
									  			<xsl:when test="OutcomeType='OTHER_SERIOUS'">
									  				[ x ] Other, specify: <xsl:value-of select="other"/>
									  			</xsl:when>
									  		</xsl:choose>	
									  </xsl:for-each>									  
									</fo:block>    									  									  									  									  							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >					
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										5.	Outcome of Event (at time of report)
									</fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										[  ] Resolved date (Month/Day/Year) : ___<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>    
										[  ] Improved   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [  ] Unchanged   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [  ] Worse   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [  ] Not available
							  		</fo:block>      
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										[  ] Fatal date of death (Month/Day/Year) :  ____<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>      
										Autopsy performed?        <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[ ]Y        <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [ ]N
							  		</fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Cause of death:  _____  
										(please attach death certificate and autopsy report, if applicable) 
							  		</fo:block> 							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										6.	Investigator's opinion of the relationship between the event and the study drug  (If more than one event is being reported, list secondary events and corresponding relationship to study drug in the comments section below.) Check applicable box:
									</fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
									<xsl:variable name="attribution" select="AdverseEventReport/AdverseEvent/attributionSummary"/>
									[ <xsl:if test="substring($attribution,1,1)=1">x</xsl:if>] Not related
									<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
									[ <xsl:if test="substring($attribution,1,1)=2">x</xsl:if>] Unlikely
						            <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>  
						            [ <xsl:if test="substring($attribution,1,1)=3">x</xsl:if>] Possible           
								    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
								    [ <xsl:if test="substring($attribution,1,1)=4">x</xsl:if>] Probable
				                    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>  
				                    [ <xsl:if test="substring($attribution,1,1)=5">x</xsl:if>] Definite                       
							  		</fo:block>      							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Was this event reported by the Investigator to (check all that apply):   
										[  ] IRB       <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>  
										 [  ] Manufacturer/Distributor        <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
									</fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										[  ] Other Investigators participating in this study, if checked, please list names and institutions
									</fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
									---------
							  		</fo:block>      							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >
	  				</fo:table-body>
		  		</fo:table>		
		  		
	  			<fo:block break-before="page" font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="left"> 
		  			F.  Comments/Clarifications:
		  		</fo:block>

		  		<fo:table border="0.5pt solid black">

		  			<fo:table-body >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block font-size="10pt" font-weight="bold" font-family="Times New Roman" text-align-last="center"> 
										FOR NCI USE ONLY
									</fo:block>     				
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										1.	Date NCI notified of event (Month/Day/Year):
									</fo:block> 							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >					
			  			<fo:table-row height="120mm" >
	      						<fo:table-cell border="0.5pt solid black">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										2.	Medical Monitor Review:
									</fo:block>
									<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Medical Assessment of Event (including drug relationship and expectancy): 
							  		</fo:block>      							  									  						
	      						</fo:table-cell>		  			
			  			</fo:table-row >
			  			<fo:table-row xsl:use-attribute-sets="tr-height-1" >
	      						<fo:table-cell xsl:use-attribute-sets="small-cell">
							  		<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Is this an FDA reportable (7 calendar days) event?    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Yes   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] No
									</fo:block>
									<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Is this an FDA reportable (15 calendar days) event?  <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Yes   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] No
							  		</fo:block>  
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										>> If No, specify reason:__
							  		</fo:block>  
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Is more information expected?     <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [  ] Yes   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										  [  ] No
							  		</fo:block>  
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										>> If Yes, specify:__
							  		</fo:block>  
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Is this event to be communicated to other NCI contractors using this investigational drug?  <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>      
										[  ] Yes    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] No
							  		</fo:block>  
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										>> If Yes, how?	<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;</xsl:text>
										By telephone (attach a TC Form):	<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160;</xsl:text>
										[  ] Yes, attached TC Form    <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] No
							  		</fo:block>  
									<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block>
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
									<xsl:text disable-output-escaping="yes">&amp;#160;  &amp;#160; &amp;#160; &amp;#160; &amp;#160;&amp;#160; &amp;#160; &amp;#160; &amp;#160;&amp;#160; &amp;#160; &amp;#160; &amp;#160;&amp;#160; &amp;#160; &amp;#160; &amp;#160;</xsl:text>
										Other (FAX, mail, e-mail, etc.): 	<xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										[  ] Yes, attached a copy of the correspondence   <xsl:text disable-output-escaping="yes">&amp;#160; &amp;#160; </xsl:text>
										 [  ] No
							  		</fo:block>  	
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 
							  		<fo:block> <xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text> </fo:block> 						  									  									  									  									  									  		    							  									  						
									<fo:block xsl:use-attribute-sets="normal" margin-left="2mm"> 
										Medical Monitor:  Print name  __________________________________________________  Signature ___________________________  Date  ____________
							  		</fo:block> 
	      						</fo:table-cell>		  			
			  			</fo:table-row >
	  				</fo:table-body>
		  		</fo:table>	
		  				  				  		  				  		
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

  
</xsl:stylesheet>
